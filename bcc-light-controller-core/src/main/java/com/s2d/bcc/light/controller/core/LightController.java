
package com.s2d.bcc.light.controller.core;

import java.util.List;

public interface LightController
{
  void initialize ();
  List < Light > getLights ();
  void set ( Light light, boolean on );
  void execute ();
  void close ();
}