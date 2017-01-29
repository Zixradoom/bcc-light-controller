package com.s2d.bcc.light.controller.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contains all of the update events for a scene
 * in proper execution order.
 * 
 * @author Zixradoom
 *
 */
public class Scene
{
  private final List < SceneEvent > events;
  private final String name;
  
  public Scene ( String name, List < SceneEvent > sceneEvents )
  {
    this.name = ( name == null ) ? "" : name;
    this.events = Collections.unmodifiableList ( sceneEvents.stream ()
        .filter ( Objects::nonNull )
        .collect ( Collectors.toList () ) );
  }
  
  public Scene ( List < SceneEvent > sceneEvents )
  {
    this ( null, sceneEvents );
  }
  
  public List < SceneEvent > getSceneEvents ()
  {
    return events;
  }
  
  public String getName ()
  {
    return name;
  }

  @Override
  public String toString ()
  {
    return "Scene [events=" + events + ", name=" + name + "]";
  }
}
