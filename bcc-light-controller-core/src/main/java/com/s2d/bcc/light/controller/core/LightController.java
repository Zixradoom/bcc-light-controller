
package com.s2d.bcc.light.controller.core;

public interface LightController
{
  void initialize ();
  int getLightCount ();
  void set ( int lightIndex, boolean on );
  void execute ();
}