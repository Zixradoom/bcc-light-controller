package com.s2d.bcc.light.controller.core;

public interface Server
{
  public static final String LIGHT_CONTROLLER_SERVER = "com.s2d.bcc.light.controller.server";
  public static final String LIGHT_CONTROLLER_SCENE_CONTROLLER = "com.s2d.bcc.light.controller.scene.controller";
  
  void shutdown ();
}