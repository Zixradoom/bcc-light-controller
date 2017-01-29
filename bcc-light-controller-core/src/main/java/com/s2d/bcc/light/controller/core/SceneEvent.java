package com.s2d.bcc.light.controller.core;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A single event in a {@link Scene}. The event can turn on and off
 * multiple lights and can specify a delay to observe before the
 * event should be executed.
 * 
 * @author Zixradoom
 *
 */
public class SceneEvent
{
  private Map < Light, Boolean > lightMap;
  private long time;
  private TimeUnit unit;
  
  public Map < Light, Boolean > getLightMap ()
  {
    return Collections.unmodifiableMap ( lightMap );
  }
  
  /**
   * 
   * @return the length of the time delay
   */
  public long getDelay ()
  {
    return time;
  }
  
  /**
   * 
   * @return the {@link TimeUnit} the delay is specified in
   */
  public TimeUnit getDelayTimeUnit ()
  {
    return unit;
  }
}
