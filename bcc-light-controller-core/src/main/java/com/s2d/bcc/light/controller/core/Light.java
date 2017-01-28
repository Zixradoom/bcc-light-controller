package com.s2d.bcc.light.controller.core;

public final class Light
{
  private final long id;
  private final String name;
  
  private Light ( long id, String name )
  {
    this.id = id;
    this.name = ( name == null ) ? "" : name;
  }

  public long getId ()
  {
    return id;
  }
  
  public String getName ()
  {
    return name;
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
