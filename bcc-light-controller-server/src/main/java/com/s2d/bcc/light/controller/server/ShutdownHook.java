package com.s2d.bcc.light.controller.server;

import com.s2d.bcc.light.controller.core.Server;

public class ShutdownHook extends Thread
{
  private Server server;
  
  public ShutdownHook ( Server server )
  {
    this.server = server;
  }
  
  @Override
  public void run ()
  {
    server.shutdown ();
  }
}
