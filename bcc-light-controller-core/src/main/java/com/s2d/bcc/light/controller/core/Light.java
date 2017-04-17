package com.s2d.bcc.light.controller.core;

import java.util.Objects;

/**
 * A Light is one of possibly many devices controlled by the {@link LightController}.
 * 
 * @author Zixradoom
 *
 */
public final class Light
{
  private final LightController lightController;
  private final long id;
  private final String name;
  
  public Light ( LightController lightController, long id, String name )
  {
    this.lightController = Objects.requireNonNull ( lightController );
    this.id = id;
    this.name = ( name == null ) ? "" : name;
  }

  /**
   * 
   * @return the id assigned to this light by the {@link LightController}
   */
  public long getId ()
  {
    return id;
  }
  
  /**
   * 
   * @return the name assigned to this light by the {@link LightController}
   */
  public String getName ()
  {
    return name;
  }

  /**
   * 
   * @return the light controller this light belongs too.
   */
  public LightController getLightController ()
  {
    return lightController;
  }
  
  @Override
  public int hashCode ()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( int ) ( id ^ ( id >>> 32 ) );
    result = prime * result + ( ( name == null ) ? 0 : name.hashCode () );
    return result;
  }

  @Override
  public boolean equals ( Object obj )
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass () != obj.getClass ())
      return false;
    Light other = ( Light ) obj;
    if (id != other.id)
      return false;
    if (name == null)
    {
      if (other.name != null)
        return false;
    }
    else if (!name.equals ( other.name ))
      return false;
    return true;
  }
}
