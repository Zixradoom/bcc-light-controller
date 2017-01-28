package com.s2d.bcc.light.controller.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public final class SceneController
{
  private LightController controller;
  private ScheduledExecutorService ses;

  public SceneController ()
  {

  }

  public void executeScene ( Scene scene )
  {
    for ( SceneEvent se : scene.getSceneEvents () )
    {
      ScheduledFuture < ? > sf = ses.schedule ( new LightUpdate ( controller,
          se.getLightMap () ), se.getDelay (), se.getDelayTimeUnit () );
      try
      {
        sf.get ();
      }
      catch ( InterruptedException | ExecutionException e )
      {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * The actual working class, this will contain the code that
   * calls the {@link LightController} methods to execute the updates
   * 
   * @author Zixradoom
   *
   */
  private static final class LightUpdate implements Runnable
  {
    private LightController lc;
    private Map < Light, Boolean > lightMap;
    
    private LightUpdate ( LightController lc, Map < Light, Boolean > lightMap )
    {
      this.lc = lc;
      this.lightMap = lightMap;
    }

    @Override
    public void run ()
    {
      for ( Entry < Light, Boolean > entry : lightMap.entrySet () )
        lc.set ( entry.getKey (), entry.getValue ().booleanValue () );
    }
  }
}
