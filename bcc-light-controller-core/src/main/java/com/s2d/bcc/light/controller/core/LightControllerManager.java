package com.s2d.bcc.light.controller.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class LightControllerManager
{
  private final Object lock = new Object ();
  
  private ServiceLoader < LightControllerProvider > loader;
  
  public LightControllerManager ()
  {
    loader = ServiceLoader.load ( LightControllerProvider.class );
  }
  
  public List < LightControllerProvider > gertProviders ()
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
