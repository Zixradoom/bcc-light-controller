package com.s2d.bcc.light.controller.webapp;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.s2d.bcc.light.controller.core.LightControllerManager;
import com.s2d.bcc.light.controller.core.Scene;
import com.s2d.bcc.light.controller.core.Server;

public final class SceneServlet extends HttpServlet
{
  private static final long serialVersionUID = -5642140887859681567L;
  
  private static final String CONTENT_TYPE_AND_ENCODING_JSON = "application/json; charset=utf-8";
  private static final String CONTENT_TYPE_AND_ENCODING_TEXT = "text/plain; charset=utf-8";
  
  private SceneDatabase sceneDatabase;
  private SceneConverter sceneConverter;
  
  @Override
  public void init ()
  {
    sceneDatabase = new SceneDatabase ( getLightControllerManager (), getDataSource () );
    sceneConverter = new SceneConverter ( getLightControllerManager () );
  }
  
  @Override
  protected void doGet ( HttpServletRequest req, HttpServletResponse resp ) throws IOException
  {
    String sceneName = parseSceneName ( req.getPathInfo () );
    if ( sceneName.isEmpty () )
    {
      JsonArrayBuilder sceneArray = Json.createArrayBuilder ();
      for ( Scene scene : sceneDatabase.getScenes () )
        sceneArray.add ( sceneConverter.writeScene ( scene ) );
      resp.setContentType ( CONTENT_TYPE_AND_ENCODING_JSON );
      Json.createWriter ( resp.getWriter () ).writeArray ( sceneArray.build () );
    }
    else
    {
      Scene scene = sceneDatabase.loadScene ( sceneName );
      resp.setContentType ( CONTENT_TYPE_AND_ENCODING_JSON );
      Json.createWriter ( resp.getWriter () ).writeObject ( sceneConverter.writeScene ( scene ) );
    }
  }
  
  @Override
  protected void doPut ( HttpServletRequest req, HttpServletResponse resp ) throws IOException
  {
    String sceneName = parseSceneName ( req.getPathInfo () );
    if ( !sceneName.isEmpty () )
    {
      JsonObject newScene = Json.createReader ( req.getReader () ).readObject ();
      Scene scene = sceneConverter.readScene ( newScene );
      // TODO check that the sceneName matches scene.getName ()
      // if it does not we should return a 400
      boolean isNew = !sceneDatabase.getSceneExists ( sceneName );
      sceneDatabase.storeScene ( scene );
      
      if ( isNew )
        resp.setStatus ( HttpServletResponse.SC_CREATED );
      else
        resp.setStatus ( HttpServletResponse.SC_NO_CONTENT );
    }
    else
    {
      resp.setContentType ( CONTENT_TYPE_AND_ENCODING_TEXT );
      resp.setStatus ( HttpServletResponse.SC_BAD_REQUEST );
      resp.getWriter ().println ( "the resource must have a name" );
    }
  }
  
  private String parseSceneName ( String str )
  {
    if ( str == null )
      return "";
    String[] split = str.split ( "/" );
    if ( split.length >= 2 )
    {
      //the first element will be the empty string
      String possibleSceneName = split[ 1 ];
      if ( sceneDatabase.getSceneExists ( possibleSceneName ) )
        return possibleSceneName;
      else
        return "";
    }
    else
      return "";
  }
  
  private LightControllerManager getLightControllerManager ()
  {
    return ( LightControllerManager ) getServletContext ().getAttribute ( Server.LIGHT_CONTROLLER_MANAGER );
  }
  
  private DataSource getDataSource ()
  {
    return ( DataSource ) getServletContext ().getAttribute ( Server.DATA_SOURCE );
  }
}
