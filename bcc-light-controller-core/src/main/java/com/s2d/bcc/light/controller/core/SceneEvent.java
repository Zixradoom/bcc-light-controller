package com.s2d.bcc.light.controller.core;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SceneEvent
{
  private Map < Light, Boolean > lightMap;
  private long time;
  private TimeUnit unit;
  
  public Map < Light, Boolean > getLightMap ()
  {
    return Collections.unmodifiableMap ( lightMap );
  }
  
  public long getDelay ()
  {
    return time;
  }
  
  public TimeUnit getDelayTimeUnit ()
  {
    return unit;
  }
}
