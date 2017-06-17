package com.s2d.bcc.light.controller.test.driver;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.s2d.bcc.light.controller.core.Light;
import com.s2d.bcc.light.controller.core.LightController;
import com.s2d.bcc.light.controller.core.LightControllerInitializationException;
import com.s2d.bcc.light.controller.core.spi.LightControllerProvider;

/**
 * A test simulated implementation of the {@link LightController}
 * to be used during programming so that the actual hardware is
 * not necessary.
 * 
 * @author Zixradoom
 *
 */
public class TestLightController implements LightController
{
  private static final XLogger LOGGER = XLoggerFactory.getXLogger ( TestLightController.class );
  
  // the TestLightController only has 1 implementation
  public static final int ID = 0;
  private static final String DEFAULT_LIGHT_LIST_PROPERTIES = "defaultLights.properties";

  private final TestLightControllerProvider provider;
  
  private final List < Light > lights;
  private Map < Light, Boolean > lastState;
  private Map < Light, Boolean > currentState;
  private FrameHandler handler = null;

  private Lock stateLock;
  
  public TestLightController ( TestLightControllerProvider provider )
  {
    this.provider = Objects.requireNonNull ( provider, "provider is null" );
    
    if ( GraphicsEnvironment.getLocalGraphicsEnvironment ().isHeadlessInstance () )
    {
      throw LOGGER.throwing ( new IllegalStateException ( "The TestLightController implementation cannot be used in a headless environment" ) );
    }
    
    stateLock = new ReentrantLock ();
    lastState = new HashMap < Light, Boolean > ();
    currentState = new HashMap < Light, Boolean > ();
    lights = buildLightList ( this );
    handler = new FrameHandler ( this );
  }

  @Override
  public List < Light > getLights ()
  {
    return lights;
  }

  @Override
  public void set ( Light light, boolean on )
  {
    if ( ! lights.contains ( light ) )
      throw new IllegalArgumentException ( "the light passed to this method did not originate from this controller" );
    
    set0 ( light, on );
  }
  
  private void set0 ( Light light, boolean on )
  {
    stateLock.lock ();
    try
    {
      if ( lastState.containsKey ( light ) )
      {
        if ( lastState.get ( light ).booleanValue () == on )
        {
          // we want to remove the current state if
          // it is equal to the last state
          // as no state transition has taken
          // place
          if ( currentState.containsKey ( light ) && 
              currentState.get ( light ).booleanValue () == on )
          {
              currentState.remove ( light );
          }
        }
        else
        {
          // if the incoming value is not equal to
          // the last state we want to put it on the
          // current state
          currentState.put ( light, Boolean.valueOf ( on ) );
        }
      }
      else
      {
        // last state does not have the light
        currentState.put ( light, Boolean.valueOf ( on ) );
      }
    }
    finally
    {
      stateLock.unlock ();
    }
  }

  @Override
  public void execute ()
  {
    // execute the stored state
    stateLock.lock ();
    try
    {
      lastState.putAll ( currentState );
      currentState = new HashMap < Light, Boolean > ();
      handler.setState ( lastState );
    }
    finally
    {
      stateLock.unlock ();
    }
  }

  @Override
  public LightControllerProvider getProvider ()
  {
    return provider;
  }

  @Override
  public long getId ()
  {
    return ID;
  }

  @Override
  public void close ()
  {
    handler.close ();
  }

  /**
   * Build the list of lights this test controller
   * controls based on a configured properties file
   * or the default light list if a configured
   * properties file is not present
   * @param lc the light controller
   * @return an immutable list of immutable lights
   */
  private static List < Light > buildLightList ( LightController lc )
  {
    List < Light > lights = new ArrayList < Light > ();

    Properties properties = new Properties ();

    URL url = Objects.requireNonNull ( TestLightController.class.getResource ( "/" + DEFAULT_LIGHT_LIST_PROPERTIES ), DEFAULT_LIGHT_LIST_PROPERTIES + " is null" );
    try ( InputStream is = url.openStream () )
    {
      properties.load ( is );
      
      for ( String str : properties.stringPropertyNames () )
      {
        lights.add ( new Light ( lc, Integer.valueOf ( str ).intValue (), properties.getProperty ( str ) ) );
      }
      
      Collections.sort ( lights, ( l1, l2 ) -> Long.compare ( l1.getId (), l2.getId () ) );
    }
    catch ( IOException e )
    {
      throw LOGGER.throwing ( new LightControllerInitializationException ( "could not load default properties" ) );
    }

    return Collections.unmodifiableList ( lights );
  }
}
