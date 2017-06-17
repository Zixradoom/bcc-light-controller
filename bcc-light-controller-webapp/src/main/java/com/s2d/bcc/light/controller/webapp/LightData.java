package com.s2d.bcc.light.controller.webapp;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.s2d.bcc.light.controller.core.Light;
import com.s2d.bcc.light.controller.core.LightController;
import com.s2d.bcc.light.controller.core.LightControllerManager;
import com.s2d.bcc.light.controller.core.Server;
import com.s2d.bcc.light.controller.core.spi.LightControllerProvider;

public final class LightData extends HttpServlet
{
  private static final long serialVersionUID = 5552072194441706461L;
  
  private static final String CONTENT_TYPE_AND_ENCODING = "application/json; charset=utf-8";
  
  private static final String OPTION_PRETTY = "pretty";
  private static final String OPTION_UNFLATTEN = "unflatten";
  
  private static final String PROVIDER_ID = "providerId";
  private static final String CONTROLLER_ID = "controllerId";
  private static final String LIGHT_ID = "lightId";
  
  @Override
  protected void doGet ( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
  {
    JsonArrayBuilder returnValue = ( checkUnFlattenOption ( req ) ) ? getAllData () : getAllDataFlattened ();
    String rURI = req.getRequestURI ();
    String pInfo = req.getPathInfo ();
    if ( rURI == null ) returnValue.addNull (); else returnValue.add ( rURI );
    if ( pInfo == null ) returnValue.addNull (); else returnValue.add ( pInfo );
    
//    URI reqURI = new URI ( req.getRequestURI () );


    resp.setContentType ( CONTENT_TYPE_AND_ENCODING );
    if ( checkPrettyOption ( req ) )
    {
      Map < String, ? > env = Collections.singletonMap (
          javax.json.stream.JsonGenerator.PRETTY_PRINTING, "true" );
      Json.createWriterFactory ( env )
        .createWriter ( resp.getWriter () )
        .writeArray ( returnValue.build () );
    }
    else
      Json.createWriter ( resp.getWriter () ).writeArray ( returnValue.build () );
  }
  
  private JsonArrayBuilder getAllDataFlattened ()
  {
    LightControllerManager lcm = getLightControllerManager ();
    
    JsonArrayBuilder returnValue = Json.createArrayBuilder ();
    
    for ( LightControllerProvider lcp : lcm.getProviders () )
    {
      for ( Integer controllerId : lcp.getLightControllerIds () )
      {
        LightController lc = lcp.openLightController ( controllerId.intValue () );
        for ( Light l : lc.getLights () )
        {
          returnValue.add ( Json.createObjectBuilder ()
              .add ( PROVIDER_ID, lcp.getUUID ().toString () )
              .add ( CONTROLLER_ID, lc.getId () )
              .add ( LIGHT_ID, l.getId () ) );
        }
      }
    }
    
    return returnValue;
  }

  private JsonArrayBuilder getAllData ()
  {
    LightControllerManager lcm = getLightControllerManager ();
    
    JsonArrayBuilder returnValue = Json.createArrayBuilder ();
    
    for ( LightControllerProvider lcp : lcm.getProviders () )
    {
      JsonObjectBuilder lcpB = Json.createObjectBuilder ();
      lcpB.add ( "id", lcp.getUUID ().toString () );
      lcpB.add ( "name", lcp.getName () );
      JsonArrayBuilder lclB = Json.createArrayBuilder ();
      for ( Integer controllerId : lcp.getLightControllerIds () )
      {
        JsonObjectBuilder lcB = Json.createObjectBuilder ();
        LightController lc = lcp.openLightController ( controllerId.intValue () );
        lcB.add ( "id", lc.getId () );
        JsonArrayBuilder llB = Json.createArrayBuilder ();
        for ( Light l : lc.getLights () )
        {
          JsonObjectBuilder lB = Json.createObjectBuilder ();
          lB.add ( "id", l.getId () );
          lB.add ( "name", l.getName () );
          llB.add ( lB );
        }
        lcB.add ( "lights", llB );
        lclB.add ( lcB );
      }
      lcpB.add ( "lightControllers", lclB );
      returnValue.add ( lcpB );
    }
    
    return returnValue;
  }  
  
  private boolean checkUnFlattenOption ( HttpServletRequest hsr )
  {
    if ( hsr.getParameterMap ().containsKey ( OPTION_UNFLATTEN ) )
    {
      String[] args = hsr.getParameterMap ().get ( OPTION_UNFLATTEN );
      if ( args.length >= 1 )
      {
        return Boolean.valueOf ( args[ 0 ] ).booleanValue ();
      }
    }
    return false;
  }
  
  private boolean checkPrettyOption ( HttpServletRequest hsr )
  {
    if ( hsr.getParameterMap ().containsKey ( OPTION_PRETTY ) )
    {
      String[] args = hsr.getParameterMap ().get ( OPTION_PRETTY );
      if ( args.length >= 1 )
      {
        return Boolean.valueOf ( args[ 0 ] ).booleanValue ();
      }
    }
    return false;
  }
  
  private LightControllerManager getLightControllerManager ()
  {
    return ( LightControllerManager ) getServletContext ().getAttribute ( Server.LIGHT_CONTROLLER_MANAGER );
  }
}
