package scene;

import data.TestResult;

import javax.swing.*;
import java.awt.*;


public class WindowFrame extends JFrame {


    public WindowFrame(TestResult testResult) throws HeadlessException {
        setBounds(200, 300, 800, 600);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("K-means clustering");

        //panel
        MapViewPanel mapViewPanel = new MapViewPanel();
        //set view panels window
        getContentPane().add(mapViewPanel);
        //draws dots
        mapViewPanel.addDots(testResult.getSites(), testResult.getClusterNo(), testResult.getCentroids());
        //visualze the panel
        setVisible(true);
    }

}
