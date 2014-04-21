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
        try {
             /*









nimbusSelectedText	#ffffff (255,255,255)

text
              */
            //  customizeNimbusLaF();
              //    UIManager.put("ScrollBar.foreground", Color.decode("#003040"));
     //       UIManager.put("nimbusAlertYellow", Color.YELLOW);
            UIManager.put("nimbusBase",Color.gray);
            UIManager.put("control", Color.decode("#f2f0ee"));
            UIManager.put("nimbusBlueGrey",Color.decode("#eeece9"));
            UIManager.put("nimbusSelectionBackground",Color.decode("#f27b4b"));
       //     UIManager.put("info", Color.black);
            UIManager.put("nimbusLightBackground", Color.lightGray);
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
            UIManager.put("nimbusSelectionBackground",Color.decode("#f27b4b"));
       //    UIManager.put("nimbusRed", Color.RED);
       //     UIManager.put("nimbusSelectionBackground",
       //             Color.lightGray);


         //   UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

              //   UIManager.put("nimbusBlueGrey", new Color(...));
              //  UIManager.put("control", new Color(...));
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                 //   customizeNimbusLaF();
              //
                    break;
                }
            }
        //    SwingUtilities.updateComponentTreeUI(frame);
     //       UIManager.getLookAndFeelDefaults().put("ScrollBar.foreground", fromHex("e36431"));
         //   SwingUtilities.updateComponentTreeUI( frame );
            //   OyoahaLookAndFeel laf = new OyoahaLookAndFeel();
            //   UIManager.setLookAndFeel(laf);
            //   UIManager.setLookAndFeel("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");
        }  catch (Exception e) {
            e.printStackTrace();
        }


        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Add content to the window.
        frame.add(new ConfigBuilderPanel(), BorderLayout.CENTER);
        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    private static void customizeNimbusLaF() {
     //   UIManager.put("control", Color.BLACK);
        UIManager.put("nimbusAlertYellow", Color.YELLOW);
        UIManager.put("nimbusBase", Color.white);
        UIManager.put("nimbusDisabledText", Color.DARK_GRAY);
        UIManager.put("nimbusFocus", Color.black);
        UIManager.put("nimbusGreen", Color.GREEN);
        UIManager.put("nimbusInfoBlue", Color.BLUE);
        UIManager.put("nimbusRed", Color.RED);
        UIManager.put("nimbusSelectionBackground",
                Color.lightGray);

      //  UIManager.put("background", Color.lightGray);
      //  UIManager.put("controlDkShadow", Color.darkGray);
      //  UIManager.put("controlShadow", Color.GRAY);
       // UIManager.put("desktop", Color.blue);
     //   UIManager.put("menu", Color.GRAY);
     //   UIManager.put("nimbusBorder", Color.GRAY);
    //    UIManager.put("nimbusSelection", Color.blue);
    //    UIManager.put("textBackground", Color.cyan);
    //    UIManager.put("textHighlight", Color.cyan);
    //    UIManager.put("textInactiveText", Color.GRAY);

        // panel
        UIManager.put("Panel.background", Color.white);
    //    UIManager.put("Panel.disabled", Color.lightGray);
        //   UIManager.put( "Panel.font", Color.DEFAULT_FONT );
    //    UIManager.put("Panel.opaque", true);

        // button
    //    UIManager.put("Button.background", Color.lightGray);
    //    UIManager.put("Button.disabled", Color.lightGray);
        //   UIManager.put( "Button.disabledText", Color.BLUE_MIDDLE );
        //   UIManager.put( "Button.font", Color.DEFAULT_FONT );

        // menu
   //     UIManager.put("Menu.background", Color.lightGray);
   //     UIManager.put("Menu.disabled", Color.lightGray);
   //     UIManager.put("Menu.disabledText", Color.darkGray);
        //   UIManager.put( "Menu.font", Color.MENU_FONT );
    //    UIManager.put("Menu.foreground", Color.BLACK);
    //    UIManager.put("Menu[Disabled].textForeground",
     //           Color.GRAY);
     //   UIManager.put("Menu[Enabled].textForeground", Color.BLACK);
     //   UIManager.put("MenuBar.background", Color.BLUE);
    //    UIManager.put("MenuBar.disabled", Color.lightGray);
        //    UIManager.put( "MenuBar.font", Color.MENU_FONT );
   //     UIManager.put("MenuBar:Menu[Disabled].textForeground",
    //            Color.gray);
    //    UIManager.put("MenuBar:Menu[Enabled].textForeground",
    //            Color.BLACK);
    //    UIManager.put("MenuItem.background", Color.lightGray);
    //    UIManager.put("MenuItem.disabled", Color.lightGray);
    //    UIManager.put("MenuItem.disabledText", Color.gray);
        //  UIManager.put( "MenuItem.font", Color.MENU_FONT );
    //    UIManager.put("MenuItem.foreground", Color.BLACK);
    //    UIManager.put("MenuItem[Disabled].textForeground",
    //            Color.gray);
    //    UIManager.put("MenuItem[Enabled].textForeground",
    //            Color.BLACK);

        // tree
    //    UIManager.put("Tree.background", Color.BLACK);
    //    UIManager.put("TabbedPane.background", Color.WHITE);
        UIManager.put("ScrollBar.foreground", fromHex("e36431"));

    }



    private static Color fromHex(String hex) {
        return Color.decode("#"+hex);

    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts

               // UIManager.put("swing.boldMetal", Boolean.FALSE);
             //   UIManager.put("Panel.background", Color.white);

                createAndShowGUI();
            }
        });


        }
    }


