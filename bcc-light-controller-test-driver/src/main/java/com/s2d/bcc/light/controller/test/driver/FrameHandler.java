package com.s2d.bcc.light.controller.test.driver;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.s2d.bcc.light.controller.core.Light;
import com.s2d.bcc.light.controller.core.LightController;

public final class FrameHandler
{
  private static final XLogger LOGGER = XLoggerFactory.getXLogger ( FrameHandler.class );

  private JFrame frame;
  private LightPane lightPane;

  public FrameHandler ( LightController lc )
  {
    try
    {
      SwingUtilities.invokeAndWait ( new Runnable () {
        @Override
        public void run ()
        {
          JFrame frame = new JFrame ( "Light Controller Demonstration Driver" );
          frame.setLayout ( new BorderLayout ( 5, 5 ) );

          JLabel label = new JLabel ( "Light Controller Demonstration Driver" );
          label.setHorizontalAlignment ( JLabel.CENTER );
          label.setVerticalAlignment ( JLabel.CENTER );
          frame.add ( label, BorderLayout.NORTH );

          LightPane lightPane = new LightPane ( lc );
          frame.add ( lightPane, BorderLayout.CENTER );

          frame.setResizable ( false );
          frame.pack ();
          frame.setDefaultCloseOperation ( JFrame.DISPOSE_ON_CLOSE );
          FrameHandler.this.lightPane = lightPane;
          FrameHandler.this.frame = frame;
        }
      } );
    }
    catch ( InvocationTargetException | InterruptedException e )
    {
      throw LOGGER.throwing ( new RuntimeException ( e ) );
    }
  }

  public void setState ( Map < Light, Boolean > state )
  {
    SwingUtilities.invokeLater ( new Runnable() {
      @Override
      public void run ()
      {
        frame.setVisible ( true );
        lightPane.setState ( state );
      }
    });
  }

  public void close ()
  {
    if ( !frame.isDisplayable () )
    {
      SwingUtilities.invokeLater ( new Runnable() {
        @Override
        public void run ()
        {
          frame.dispose ();
        }
      } );
    }
  }
}
