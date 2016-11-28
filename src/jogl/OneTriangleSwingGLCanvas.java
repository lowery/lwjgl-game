package jogl;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A minimal program that draws with JOGL in a Swing JFrame using the AWT GLCanvas.
 *
 * @author Wade Walker
 */
public class OneTriangleSwingGLCanvas {

    public static void main( String [] args ) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());

        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener( new GLEventListener() {
            
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                OneTriangle.setup( glautodrawable.getGL(), width, height );
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {
            }
            
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
            }
            
            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                OneTriangle.render( glautodrawable.getGL(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
            }
        });

        final JFrame jframe = new JFrame( "One Triangle Swing GLCanvas" ); 
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });


        final ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equalsIgnoreCase("OPEN")) {

                } else if (e.getActionCommand().equalsIgnoreCase("EXIT")) {
                    System.exit(0);
                }
            }
        };

        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();

        //Build the file menu.
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        //a group of JMenuItems
        JMenuItem menuItem = new JMenuItem("Open");
        menuItem.addActionListener(listener);
        menu.add(menuItem);

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(listener);
        menu.add(menuItem);

        jframe.setJMenuBar(menuBar);
        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( 640, 640 );
        jframe.setLocationRelativeTo(null);
        jframe.setVisible( true );
    }

}