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

             /*









nimbusSelectedText	#ffffff (255,255,255)

text
              */
        //  customizeNimbusLaF();
        //    UIManager.put("ScrollBar.foreground", Color.decode("#003040"));
        //       UIManager.put("nimbusAlertYellow", Color.YELLOW);

        //     UIManager.put("info", Color.black);
        //    UIManager.put("nimbusLightBackground", Color.lightGray);
        // UIManager.put("nimbusBase",  Color.decode("#ded5dc"));
        //    UIManager.put("nimbusDisabledText", Color.DARK_GRAY);
        //       UIManager.put("nimbusSelectedText", Color.WHITE);
        //   UIManager.put("nimbusFocus", Color.black);
        //     UIManager.put("nimbusGreen", Color.red);
        //     UIManager.put("scrollbar", Color.GREEN);
        //     UIManager.put("nimbusFocus", Color.black);
        //     UIManager.put("nimbusGreen", Color.GREEN);
        //     UIManager.put("nimbusInfoBlue", Color.BLUE);
        //     UIManager.put("nimbusInfoBlue", Color.red);
        //   UIManager.put("nimbusOrange", Color.RED);
        //   UIManager.put("nimbusSelectionBackground",Color.red);
        //    UIManager.put("nimbusRed", Color.RED);
        //     UIManager.put("nimbusSelectionBackground",
        //             Color.lightGray);


        //   UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

        //   UIManager.put("nimbusBlueGrey", new Color(...));

        //    SwingUtilities.updateComponentTreeUI(frame);
        //       UIManager.getLookAndFeelDefaults().put("FileChooser.background", fromHex("e36431"));
        //   SwingUtilities.updateComponentTreeUI( frame );
        //   OyoahaLookAndFeel laf = new OyoahaLookAndFeel();
        //   UIManager.setLookAndFeel(laf);
        //   UIManager.setLookAndFeel("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");

        int height = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200,height));
        //Add content to the window.
        frame.add(new ConfigBuilderPanel(), BorderLayout.CENTER);
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
                UIManager.put("nimbusBase", Color.gray);
                UIManager.put("control", Color.decode("#f2f0ee"));
                UIManager.put("nimbusBlueGrey", Color.decode("#eeece9"));
                UIManager.put("nimbusSelectionBackground", Color.decode("#f27b4b"));
                // UIManager.put("swing.boldMetal", Boolean.FALSE);
                //   UIManager.put("Panel.background", Color.white);
                //  UIManager.put("control", new Color(...));
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        try {
                            UIManager.setLookAndFeel(info.getClassName());
                            //   customizeNimbusLaF();
                            //
                            break;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                createAndShowGUI();
            }
        });
    }
}


