package com.s2d.bcc.light.controller.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLogger.Level;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class will be used by the Webservice to
 * execute the update calls to the {@link LightController} driver.
 * 
 * @author Zixradoom
 *
 */
public final class SceneController
{
  private static final XLogger LOGGER = XLoggerFactory.getXLogger ( SceneController.class );
  
  private LightController lightController;
  private ExecutorService es;
  private ScheduledExecutorService ses;
  
  public SceneController ( LightController lightController,
      ExecutorService executorService,
      ScheduledExecutorService scheduledExecutorService )
  {
    this.lightController = lightController;
    this.es = executorService;
    this.ses = scheduledExecutorService;
  }

  /**
   * Update the current scene. This method executes asynchronously.
   * 
   * @param scene Instruct the Scene controller to update the current scene
   * @return
   */
  public Future < Void > executeSceneUpdate ( Scene scene )
  {
    return es.submit ( new SceneUpdate ( scene, ses, lightController ) );
  }
  
  public LightController getLightController ()
  {
    return lightController;
  }
  
  /**
   * A {@link SceneUpdate} executes the list of {@link SceneEvent}s in
   * order until there are no more in the list. This process
   * can end early if the Thread is interrupted.
   * 
   * @author Zixradoom
   *
   */
  private static final class SceneUpdate implements Callable < Void >
  {
    private Scene scene;
    private ScheduledExecutorService ses;
    private LightController lightController;
    
    public SceneUpdate ( Scene scene, ScheduledExecutorService ses,
        LightController lightController )
    {
      this.scene = scene;
      this.ses = ses;
      this.lightController = lightController;
    }

    @Override
    public Void call () throws ExecutionException
    {
      for ( SceneEvent se : scene.getSceneEvents () )
      {
        if ( Thread.interrupted () )
        {
          LOGGER.trace ( "interrupted" );
          return null;
        }
        ScheduledFuture < ? > sf = ses.schedule ( new LightUpdate ( lightController,
            se.getLightMap () ), se.getDelay (), se.getDelayTimeUnit () );
        try
        {
          sf.get ();
        }
        catch ( InterruptedException e )
        {
          // Interruption is allowed
          LOGGER.catching ( Level.DEBUG, e );
          return null;
        }
        catch ( ExecutionException e )
        {
          if ( !( e.getCause () instanceof InterruptedException ) )
            throw e;
          else
            // Interruption is allowed
            LOGGER.catching ( Level.DEBUG, e );
            return null;
        }
      }
      return null;
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
      // set the scene in the driver
      for ( Entry < Light, Boolean > entry : lightMap.entrySet () )
        lc.set ( entry.getKey (), entry.getValue ().booleanValue () );
      // instruct the driver to execute the scene
      lc.execute ();
    }
  }
}
