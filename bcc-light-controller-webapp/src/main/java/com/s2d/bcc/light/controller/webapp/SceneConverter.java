package com.s2d.bcc.light.controller.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.s2d.bcc.light.controller.core.Light;
import com.s2d.bcc.light.controller.core.LightController;
import com.s2d.bcc.light.controller.core.LightControllerManager;
import com.s2d.bcc.light.controller.core.Scene;
import com.s2d.bcc.light.controller.core.SceneEvent;
import com.s2d.bcc.light.controller.core.spi.LightControllerProvider;

public final class SceneConverter
{
  private static final String NAME = "name";
  private static final String EVENT_LIST = "events";
  private static final String DELAY_DURATION = "delayDuration";
  private static final String DELAY_DURATION_UNIT = "delayDurationUnit";
  private static final String LIGHT_STATE = "lights";
  private static final String LIGHT = "light";
  private static final String STATE = "state";
  private static final String PROVIDER_ID = "providerId";
  private static final String CONTROLLER_ID = "controllerId";
  private static final String LIGHT_ID = "lightId";

  private LightControllerManager lightControllerManager;
  
  public SceneConverter ( LightControllerManager lightControllerManager )
  {
    this.lightControllerManager = lightControllerManager;
  }
  
  public JsonObject writeScene ( Scene scene )
  {
    Objects.requireNonNull ( scene, "scene is null" );
    
    JsonObjectBuilder sceneObjectBuilder = Json.createObjectBuilder ();

    sceneObjectBuilder.add ( NAME, scene.getName () );
    JsonArrayBuilder eventArrayBuilder = Json.createArrayBuilder (); 
    
    for ( SceneEvent event : scene.getSceneEvents () )
    {
      JsonObjectBuilder eventBuilder = Json.createObjectBuilder ();
      eventBuilder.add ( DELAY_DURATION, event.getDelay () );
      eventBuilder.add ( DELAY_DURATION_UNIT, event.getDelayTimeUnit ().toString () );
      
      JsonArrayBuilder lightStateList = Json.createArrayBuilder ();
      
      for ( Entry < Light, Boolean > entry : event.getLightMap ().entrySet () )
      {
        lightStateList.add ( Json.createObjectBuilder ()
            .add ( LIGHT, Json.createObjectBuilder ()
                .add ( PROVIDER_ID, entry.getKey ().getLightController ().getProvider ().getUUID ().toString () )
                .add ( CONTROLLER_ID, entry.getKey ().getLightController ().getId () )
                .add ( LIGHT_ID, entry.getKey ().getId () ) )
            .add ( STATE, entry.getValue ().booleanValue () ) );
      }
      
      eventBuilder.add ( LIGHT_STATE, lightStateList );
    }
    
    sceneObjectBuilder.add ( EVENT_LIST, eventArrayBuilder );
    return sceneObjectBuilder.build ();
  }
  
  public Scene readScene ( JsonObject obj )
  {
    // TODO add much more stringent checking in the future
    // what is here is only the happy path case
    
    String sceneName = obj.getString ( NAME );
    List < SceneEvent > newEvents = new ArrayList < SceneEvent > ();
    List < JsonObject > events = obj.getJsonArray ( EVENT_LIST ).getValuesAs ( JsonObject.class );
    for ( JsonObject event : events )
    {
      long delayDuration = event.getJsonNumber ( DELAY_DURATION ).longValue ();
      TimeUnit delayUnit = TimeUnit.valueOf ( event.getString ( DELAY_DURATION_UNIT ) );
      
      Map < Light, Boolean > lightStateMap = new HashMap < Light, Boolean > ();
      List < JsonObject > stateList = event.getJsonArray ( LIGHT_STATE ).getValuesAs ( JsonObject.class );
      for ( JsonObject lightState : stateList )
      {
        UUID pid = UUID.fromString ( lightState.getJsonObject ( LIGHT ).getString ( PROVIDER_ID ) );
        long cid = lightState.getJsonObject ( LIGHT ).getJsonNumber ( CONTROLLER_ID ).longValue ();
        long lid = lightState.getJsonObject ( LIGHT ).getJsonNumber ( LIGHT_ID ).longValue ();
        Boolean state = Boolean.valueOf ( lightState.getBoolean ( STATE ) );
        LightControllerProvider lcProvider = lightControllerManager.getProviders ().stream ()
          .filter ( ( lcp ) -> lcp.getUUID ().equals ( pid ) )
          .findFirst ().get ();
        LightController lController = lcProvider.openLightController ( cid );
        Light light = lController.getLights ().stream ()
          .filter ( ( l ) -> l.getId () == lid )
          .findFirst ().get ();
        lightStateMap.put ( light, state );
      }
      newEvents.add ( new SceneEvent ( lightStateMap, delayDuration, delayUnit ) );
    }
    return new Scene ( sceneName, newEvents );
  }
}
