package com.s2d.bcc.light.controller.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.s2d.bcc.light.controller.core.spi.LightControllerProvider;

/**
 * Used to search the system for installed {@link LightControllerProvider}s
 * 
 * @author Zixradoom
 *
 */
public final class LightControllerManager
{
  private final Object lock = new Object ();
  
  private ServiceLoader < LightControllerProvider > loader;
  
  public LightControllerManager ()
  {
    loader = ServiceLoader.load ( LightControllerProvider.class );
  }
  
  /**
   * 
   * @return all installed {@link LightControllerProvider}s
   */
  public List < LightControllerProvider > getProviders ()
  {
    List < LightControllerProvider > temp = new ArrayList < LightControllerProvider > ();
    synchronized (lock)
    {
      for ( LightControllerProvider lcp : loader )
        temp.add ( lcp );
    }
    return temp;
  }
}
