package com.s2d.bcc.light.controller.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLogger.Level;
import org.slf4j.ext.XLoggerFactory;

import com.s2d.bcc.light.controller.core.LightController;
import com.s2d.bcc.light.controller.core.LightControllerManager;
import com.s2d.bcc.light.controller.core.SceneController;
import com.s2d.bcc.light.controller.core.Server;
import com.s2d.bcc.light.controller.core.spi.LightControllerProvider;

public final class Driver implements Runnable, Server
{
  private static final XLogger LOGGER = XLoggerFactory.getXLogger ( Driver.class );
  
  private final Semaphore semaphore;
  
  public Driver ()
  {
    semaphore = new Semaphore ( 0 );
  }
  
  @Override
  public void run ()
  {
    try
    {
      run0 ();
    }
    catch ( Exception e )
    {
      LOGGER.catching ( e );
    }
  }
  
  private void run0 () throws Exception
  {
    // setup
    // check for installed war files
    // we should be executing from the <root>/bin/ directory
    Path webappDir = Paths.get ( "../webapp" ).toRealPath ();
    LOGGER.info ( "searching for insalled war file in: [{}]", webappDir );
    List < Path > warFiles = getInstalledWarFiles ( webappDir );
    if ( warFiles.isEmpty () )
      throw LOGGER.throwing ( new IllegalStateException ( "there are no war files installed" ) );
    
    // check for an installed light controller
    LightControllerManager lcm = new LightControllerManager ();
    List < LightControllerProvider > providers = lcm.getProviders ();
    // TODO support multiple light controllers
    if ( providers.isEmpty () || providers.size () > 1 )
      throw LOGGER.throwing ( new IllegalStateException ( "There must be exactly 1 LightControllerProvider installed at a time" ) );
    LightControllerProvider lcp = providers.get ( 0 );
    LightController lc = lcp.openLightController ();
    
    // executors
    ExecutorService es = Executors.newSingleThreadExecutor ();
    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor ();
    
    // create the scene controller
    SceneController sceneController = new SceneController ( es, ses );
    
    // get database
    Path databaseDir = Paths.get ( "../database/lc" ).toRealPath ();
    LOGGER.info ( "setting up database: [{}]", databaseDir );
    EmbeddedDataSource ds = createDataSource ( databaseDir );
    
    // setup the web server
    InetSocketAddress address = new InetSocketAddress ( 8080 );
    org.eclipse.jetty.server.Server webServer =
        new org.eclipse.jetty.server.Server ( address );
    
    List < WebAppContext > waps = new ArrayList < WebAppContext > ();
    for ( Path warFile : warFiles )
    {
      LOGGER.info ( "setting up web app: {}", warFile );
      String fileName = warFile.getFileName ().toString ().replace ( ".war", "" );
      WebAppContext wap = new WebAppContext ();
      wap.setContextPath ( fileName );
      wap.setWar ( warFile.toString () );
      wap.setAttribute ( Server.LIGHT_CONTROLLER_SERVER, this );
      wap.setAttribute ( Server.LIGHT_CONTROLLER_SCENE_CONTROLLER, sceneController );
      LOGGER.info ( wap.getContextPath () );
      waps.add ( wap );
    }
    ContextHandlerCollection chc = new ContextHandlerCollection ();
    chc.setHandlers ( waps.toArray ( new WebAppContext[ waps.size () ] ) );
    webServer.setHandler ( chc );
    webServer.start ();
    
    // register shutdown hook
    Runtime.getRuntime ().addShutdownHook ( new ShutdownHook ( this ) );
    
    // wait
    try
    {
      semaphore.acquire ();
    }
    catch ( InterruptedException e )
    {
      // we shutdown if the wait is interrupted
      LOGGER.catching ( Level.DEBUG, e );
    }
    
    // shutdown the web server
    webServer.stop ();
    
    // shutdown the executors
    es.shutdown ();
    ses.shutdown ();
    
    // close the light controller
    lc.close ();
    
    // close the database
    ds.setShutdownDatabase ( "shutdown" );
    ds.getConnection ();
  }

  private EmbeddedDataSource createDataSource ( Path databaseDir )
  {
    EmbeddedDataSource eds = new EmbeddedDataSource ();
    eds.setDatabaseName ( databaseDir.toString () );
    eds.setCreateDatabase ( "create" );
    return eds;
  }
  
  @Override
  public void shutdown ()
  {
    semaphore.release ();
  }
  
  private static List < Path > getInstalledWarFiles ( Path dir ) throws IOException
  {
    if ( Files.exists ( dir ) )
      throw LOGGER.throwing ( new IllegalArgumentException ( String.format ( "dir does not exist: [%s]", dir ) ) );
    
    if ( !Files.isDirectory ( dir ) )
      throw LOGGER.throwing ( new IllegalArgumentException ( String.format ( "dir is not a directory: [%s]", dir ) ) );
    
    List < Path > returnValue = null;
    try ( Stream < Path > dirContents = Files.list ( dir ) )
    {
      returnValue = Collections.unmodifiableList ( dirContents
        .filter ( p -> Files.isRegularFile ( p ) )
        .filter ( p -> p.getFileName ().toString ().toLowerCase ().endsWith ( ".war" ) )
        .collect ( Collectors.toList () ) );
    }
    
    return returnValue;
  }
  
  public static void main ( String[] args )
  {
    new Driver ().run ();
  }
}
