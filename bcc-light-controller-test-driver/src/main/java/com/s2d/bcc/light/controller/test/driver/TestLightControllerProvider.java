package com.s2d.bcc.light.controller.test.driver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.s2d.bcc.light.controller.core.LightController;
import com.s2d.bcc.light.controller.core.spi.LightControllerProvider;

public final class TestLightControllerProvider extends LightControllerProvider
{
  private static final XLogger LOGGER = XLoggerFactory.getXLogger ( TestLightControllerProvider.class );
  
  private static final String NAME = "";
  private static final UUID ID = UUID.fromString ( "b0b9f292-ef65-42e0-ba89-3f9e82a0290a" );

  private Object lock = new Object ();
  private TestLightController tlc;
  private final Collection < Integer > controllers;
  
  public TestLightControllerProvider ()
  {
    controllers = Collections.unmodifiableList ( Arrays.asList ( 
        Integer.valueOf ( TestLightController.ID ) ) );
  }
  
  @Override
  public LightController openLightController ( int id )
  {
    if ( !getLightControllerIds ().contains ( Integer.valueOf ( id ) ) )
      throw LOGGER.throwing ( new IllegalArgumentException ( "Unsupported Controller ID ["+id+"]" ) );
    return openLightController ();
  }
  
  @Override
  public LightController openLightController ()
  {
    if ( tlc == null )
      synchronized ( lock )
      {
        if ( tlc == null )
          tlc = new TestLightController ( this );
      }
    return tlc;
  }

  @Override
  public Collection < Integer > getLightControllerIds ()
  {
    return controllers;
  }
  
  @Override
  public String getName ()
  {
    return NAME;
  }

  @Override
  public UUID getUUID ()
  {
    return ID;
  }
}
