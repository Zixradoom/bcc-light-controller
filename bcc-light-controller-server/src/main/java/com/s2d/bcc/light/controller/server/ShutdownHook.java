package com.s2d.bcc.light.controller.server;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.s2d.bcc.light.controller.core.Server;

public class ShutdownHook extends Thread
{
  private static XLogger LOGGER = XLoggerFactory.getXLogger ( ShutdownHook.class );
  
  private Server server;
  
  public ShutdownHook ( Server server )
  {
    this.server = server;
  }
  
  @Override
  public void run ()
  {
    LOGGER.info ( "Executing shutdown hook" );
    server.shutdown ();
  }
}
