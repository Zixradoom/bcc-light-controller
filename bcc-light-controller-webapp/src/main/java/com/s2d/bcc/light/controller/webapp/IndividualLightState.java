package com.s2d.bcc.light.controller.webapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.s2d.bcc.light.controller.core.Light;
import com.s2d.bcc.light.controller.core.LightController;
import com.s2d.bcc.light.controller.core.LightControllerManager;
import com.s2d.bcc.light.controller.core.Scene;
import com.s2d.bcc.light.controller.core.SceneController;
import com.s2d.bcc.light.controller.core.SceneEvent;
import com.s2d.bcc.light.controller.core.Server;
import com.s2d.bcc.light.controller.core.spi.LightControllerProvider;

public class IndividualLightState extends HttpServlet
{
  private static final long serialVersionUID = -3418269342469742979L;

  @Override
  protected void doPost ( HttpServletRequest req, HttpServletResponse resp )
      throws ServletException, IOException
  {
    JsonObject obj = null;
    try ( JsonReader reader = Json.createReader ( req.getInputStream () ) )
    {
      obj = reader.readObject ();
    }
    catch ( Exception e )
    {
      resp.sendError ( HttpServletResponse.SC_BAD_REQUEST );
      return;
    }
    
    UUID providerId = UUID.fromString ( obj.getString ( "providerId" ) );
    int controllerId = obj.getInt ( "controllerId" );
    int lightId = obj.getInt ( "lightId" );
    boolean state = obj.getBoolean ( "state" );
    
    LightControllerManager lcm = getLightControllerManager ();
    Optional < LightControllerProvider > lcpOption = lcm.getProviders ().stream ()
      .filter ( provider -> provider.getUUID ().equals ( providerId ) )
      .findAny ();
    if ( !lcpOption.isPresent () )
    {
      resp.sendError ( HttpServletResponse.SC_BAD_REQUEST );
      return;
    }
    LightControllerProvider lcp = lcpOption.get ();
    Optional < Integer > lcIdOption = lcp.getLightControllerIds ().stream ()
      .filter ( id -> id.intValue () == controllerId )
      .findAny ();
    if ( !lcIdOption.isPresent () )
    {
      resp.sendError ( HttpServletResponse.SC_BAD_REQUEST );
      return;
    }
    LightController lc = lcp.openLightController ( controllerId );
    Optional < Light > lightOption = lc.getLights ().stream ()
        .filter ( l -> l.getId () == lightId )
        .findAny ();
    if ( !lightOption.isPresent () )
    {
      resp.sendError ( HttpServletResponse.SC_BAD_REQUEST );
      return;
    }
    Light l = lightOption.get ();
    SceneController sc = ( SceneController ) getServletContext ().getAttribute ( Server.LIGHT_CONTROLLER_SCENE_CONTROLLER );
    Scene temp = new Scene ( Arrays.asList ( new SceneEvent (
        Collections.singletonMap ( l, Boolean.valueOf ( state ) ), 0, TimeUnit.MILLISECONDS ) ) );
    sc.executeSceneUpdate ( temp );
    resp.setStatus ( HttpServletResponse.SC_OK );
  }
  
  private LightControllerManager getLightControllerManager ()
  {
    return ( LightControllerManager ) getServletContext ().getAttribute ( Server.LIGHT_CONTROLLER_MANAGER );
  }
}
