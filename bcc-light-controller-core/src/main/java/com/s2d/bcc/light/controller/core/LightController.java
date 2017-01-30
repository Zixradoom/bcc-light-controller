
package com.s2d.bcc.light.controller.core;

import java.util.List;

/**
 * This interface should be implemented by the class that
 * implements the driver that talks to the low level control
 * hardware.
 * 
 * @author Zixradoom
 *
 */
public interface LightController
{
  /**
   * Acquire the system resource required by this 
   * Light Controller
   * @throws LightControllerInitializationException
   */
  void initialize ();
  
  /**
   * Get the list of {@link Light}s that this controller controls.
   * Any method of this interface that requires a {@link Light}
   * as an argument must submit an instance of a light returned
   * by this class. If an instance is passed to a method on this class
   * that did not originate from this list, that method is required
   * to throw an exception.
   * 
   * @return A list of lights this controller controls
   */
  List < Light > getLights ();
  
  /**
   * Switch a light on or off in the driver. The instance of {@link Light}
   * that is passed to this method must originate from the list returned by
   * {@link LightController#getLights}.
   * @param light the light to set on or off
   * @param on true is on false is off
   */
  void set ( Light light, boolean on );
  
  /**
   * Execute light updates cached in the controller
   */
  void execute ();
  
  /**
   * Close this controller and release any underlying system
   * resources.
   */
  void close ();
}
