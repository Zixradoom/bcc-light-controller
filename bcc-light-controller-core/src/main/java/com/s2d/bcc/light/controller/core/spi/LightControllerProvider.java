package com.s2d.bcc.light.controller.core.spi;

import java.util.Collection;
import java.util.UUID;

import com.s2d.bcc.light.controller.core.LightController;
import com.s2d.bcc.light.controller.core.LightControllerInitializationException;

public abstract class LightControllerProvider
{
  /**
   * 
   * @return a LightController that is initialized.
   * @throws LightControllerInitializationException if the initialization fails
   */
  public abstract LightController openLightController ();
  
  public abstract LightController openLightController ( long id );
  
  public abstract Collection < Integer > getLightControllerIds ();

  /**
   * 
   * @return the name of this provider
   */
  public abstract String getName ();
  
  /**
   * 
   * @return the unique id of this provider
   */
  public abstract UUID getUUID ();
}
