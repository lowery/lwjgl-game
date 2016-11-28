import com.jogamp.opengl.awt.GLJPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by lowery on 11/21/2016.
 */
public class ModelViewer {
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GLJPanel panel = new GLJPanel();
        panel.setSize(1024, 768);
        frame.getContentPane().add(panel, BorderLayout.CENTER);


        frame.pack();

        // Center
        frame.setLocationRelativeTo(null);

        //Display the window.
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}