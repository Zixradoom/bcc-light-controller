package com.s2d.bcc.light.controller.core;

import java.util.Objects;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * A single event in a {@link Scene}. The event can turn on and off
 * multiple lights and can specify a delay to observe before the
 * event should be executed.
 * 
 * @author Zixradoom
 *
 */
public final class SceneEvent
{
  private final Map < Light, Boolean > lightMap;
  private final long time;
  private final TimeUnit unit;
  
  /**
   * Create a SceneEvent with the specified {@link Light}s on or off. The map will have entries
   * with null keys or null values filtered out. Only the Lights specified in the map will be
   * effected all others will retain their current state. The time duration will not be checked
   * for validity, the behavior is of the time is defined by
   * {@link java.util.concurrent.ScheduledExecutorService#schedule(java.util.concurrent.Callable,long,java.util.concurrent.TimeUnit)}
   *
   * @param lightMap the lighting state to update in the {@link LightController} driver
   * @param time the duration to wait before execution of this {@link SceneEvent}
   * @param unit the {@link TimeUnit} of the delay
   */
  public SceneEvent ( Map < Light, Boolean > lightMap, long time, TimeUnit unit )
  {
    this.time = time;
    this.unit = Objects.requireNonNull ( unit );
    Map < Light, Boolean > temp = new HashMap < Light, Boolean > ();
    for ( Entry < Light, Boolean > entry : lightMap.entrySet () )
    {
      if ( entry.getKey () != null && entry.getValue () != null )
        temp.put ( entry.getKey (), entry.getValue () );
    }
    this.lightMap = Collections.unmodifiableMap ( temp );
  }
  
  /**
   * return a map containing the lights to be set and the state to set them to.
   */
  public Map < Light, Boolean > getLightMap ()
  {
    return lightMap;
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
