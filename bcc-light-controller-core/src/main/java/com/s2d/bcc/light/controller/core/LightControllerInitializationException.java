package com.s2d.bcc.light.controller.core;

/**
 * 
 * This exception signifies a failure by the light controller driver
 * to initialize and acquire the necessary system resources.
 * 
 * @author Zixradoom
 *
 */
public class LightControllerInitializationException extends RuntimeException
{
  private static final long serialVersionUID = -7246824457742689180L;
  
  public LightControllerInitializationException ()
  {
    super ();
  }
  
  public LightControllerInitializationException ( String message )
  {
    super ( message );
  }

  public LightControllerInitializationException ( Throwable throwable )
  {
    super ( throwable );
  }
  
  public LightControllerInitializationException ( String message, Throwable throwable )
  {
    super ( message, throwable );
  }
}
