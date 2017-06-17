package com.s2d.bcc.light.controller.webapp;

import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import com.s2d.bcc.light.controller.core.LightControllerManager;
import com.s2d.bcc.light.controller.core.Scene;

public final class SceneDatabase
{
  private DataSource dataSource;
  private LightControllerManager lightControllerManager;
  
  public SceneDatabase ( LightControllerManager lightControllerManager, DataSource dataSource )
  {
    this.lightControllerManager = lightControllerManager;
    this.dataSource = dataSource;
  }
  
  public List < Scene > getScenes ()
  {
    return Collections.emptyList ();
  }
  
  public boolean getSceneExists ( String name )
  {
    throw new UnsupportedOperationException ( "not implemented" );
  }
  
  public Scene loadScene ( String name )
  {
    throw new UnsupportedOperationException ( "not implemented" );
  }
  
  public Scene loadScene ( long id )
  {
    throw new UnsupportedOperationException ( "not implemented" );
  }
  
  public void storeScene ( Scene scene )
  {
    throw new UnsupportedOperationException ( "not implemented" );
  }
}
