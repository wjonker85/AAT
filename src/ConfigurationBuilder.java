/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import views.ConfigurationBuilder.ConfigBuilderPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 5/7/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationBuilder {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("AAT Config generator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Add content to the window.
        frame.add(new ConfigBuilderPanel(), BorderLayout.CENTER);
        JMenuBar menu = new JMenuBar();
        JMenuItem file = new JMenuItem("exit");
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(file);
        menu.add(fileMenu);
        frame.setJMenuBar(menu);
        file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(-1);
            }
        });
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                 //   OyoahaLookAndFeel laf = new OyoahaLookAndFeel();
                 //   UIManager.setLookAndFeel(laf);
                 //   UIManager.setLookAndFeel("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");
                }  catch (Exception e) {
                    e.printStackTrace();
                }
               // UIManager.put("swing.boldMetal", Boolean.FALSE);
                UIManager.put("Panel.background", Color.white);
                createAndShowGUI();
            }
        });
    }
}

