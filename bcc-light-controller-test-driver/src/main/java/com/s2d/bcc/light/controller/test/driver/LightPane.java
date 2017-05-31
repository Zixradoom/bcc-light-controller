package com.s2d.bcc.light.controller.test.driver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import com.s2d.bcc.light.controller.core.Light;
import com.s2d.bcc.light.controller.core.LightController;

public class LightPane extends JComponent
{
  private static final long serialVersionUID = -9086655359572324002L;

  private LightController lc;
  private Map < Light, Boolean > state;
  
  private static final Paint ON = Color.GREEN;
  private static final Paint OFF = Color.RED;
  private static final int H_GAP = 5;
  private static final int V_GAP = 5;
  private static final int TILE_WIDTH = 50;
  private static final int TILE_HEIGHT = 50;
  
  private int gX;
  private int gY;
  
  public LightPane ( LightController lc )
  {
    this.lc = lc;
    state = new HashMap < Light, Boolean > ();
    for ( Light l : lc.getLights () )
      state.put ( l, Boolean.FALSE );
    calcGrid ();
    setMinimumSize ( calcMinSize () );
    setPreferredSize ( calcPrefSize () );
  }
  
  public void setState ( Map < Light, Boolean > state )
  {
    this.state.putAll ( state );
    this.repaint ();
  }
  
  @Override
  protected void paintComponent ( Graphics g )
  {
    super.paintComponent ( g );

    Graphics2D g2d = ( Graphics2D ) g.create ();

    g2d.setPaint ( getBackground () );
    g2d.fillRect ( 0, 0, getWidth (), getHeight () );
    
    List < Light > lights = lc.getLights ();
    int count = 0;
    for ( int row = 0; row < gY; row++ )
    {
      for ( int col = 0; col < gX; col++ )
      {
        if ( count < lights.size () )
        {
          Light l = lights.get ( count );
          int x = H_GAP + ( col * ( TILE_WIDTH + H_GAP ) );
          int y = V_GAP + ( row * ( TILE_HEIGHT + V_GAP ) );
          Paint p = state.get ( l ).booleanValue () ? ON : OFF;
          g2d.setPaint ( p );
          g2d.fillRect ( x, y, TILE_WIDTH, TILE_HEIGHT );
        }
        count++;
      }
    }
  }
  
  private Dimension calcMinSize ()
  {
    int width = H_GAP + ( gX * ( TILE_WIDTH + H_GAP ) );
    int height = V_GAP + ( gY * ( TILE_HEIGHT + V_GAP ) ) ;
    return new Dimension ( width, height );
  }
  
  private Dimension calcPrefSize ()
  {
    return calcMinSize ();
  }
  
  private void calcGrid ()
  {
    int size = lc.getLights ().size ();
    if ( size <= 1 )
    {
      gX = 1;
      gY = 1;
    }
    else
    {
      int root = ( int ) Math.ceil ( Math.sqrt ( size ) ); 
      gX = root;
      gY = ( ( ( int ) ( Math.pow ( root, 2 ) ) - size ) > root ) ? root - 1 : root ;
    }
  }
}
