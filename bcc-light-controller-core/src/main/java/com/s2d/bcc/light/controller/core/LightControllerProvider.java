package com.s2d.bcc.light.controller.core;

public abstract class LightControllerProvider
{
  /**
   * 
   * @return a LightController that is initialized.
   * @throws LightControllerInitializationException if the initialization fails
   */
  public abstract LightController createLightController ();
  
  /**
   * 
   * @return the name of this provider
   */
  public abstract String getName ();
}
