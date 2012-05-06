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

import AAT.AatObject;
import Controller.JoystickController;
import Model.AATModel;
import views.ExportDataDialog;
import views.TestFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:22 PM
 * Main frame for the test. This frame is the controller from which test can be started. This is also the place where the user
 * can choose to export the gathered data for further analysis
 * TODO: beter about scherm.
 */

public class AAT_Main extends JFrame implements Observer {


    private AATModel model;
    private JoystickController joystick;
    private TestFrame testFrame;
    final JMenuItem exportData;
    final JButton runButton;

    /**
     * Start the main thread.
     *
     * @param args no arguments used
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                new AAT_Main().setVisible(true);
            }
        });
    }

    //Build the user interface
    public AAT_Main() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setLayout(new GridBagLayout());
        model = new AATModel();
        model.addObserver(this);
        joystick = new JoystickController(model);
        this.setTitle("Approach avoidance Task");
        BufferedImage buttonIcon = null;
        try {
            buttonIcon = ImageIO.read(new File("playButton100.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        runButton = new JButton(new ImageIcon(buttonIcon));
        runButton.setBorder(BorderFactory.createEmptyBorder());
        runButton.setContentAreaFilled(false);
        runButton.setEnabled(false);
        runButton.setPreferredSize(new Dimension(100, 100));
        mainPanel.add(runButton, new GridBagConstraints());
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadTest = new JMenuItem("Load new AAT");
        exportData = new JMenuItem("Export Data");
        exportData.setEnabled(false);
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(loadTest);
        fileMenu.add(exportData);
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        menuBar.add(Box.createHorizontalGlue());
        JMenu aboutMenu = new JMenu("About");
        JMenuItem about = new JMenuItem("About AAT");
        JMenuItem license = new JMenuItem("License");
        aboutMenu.add(about);
        aboutMenu.add(license);
        menuBar.add(aboutMenu);


        //Show the about dialog
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String text = "<center><h1>Approach avoidance task</h1></center>";
                text += "<center>Created By</center>";
                text += "<center><h3>Marcel Zuur <a href=\"mailto:marcelzuur@gmail.com\">Email</a></h3></center>";
                text += "<center>&</center>";
                text += "<center><h3>Wilfried Jonker <a href=\"mailto:wilfried@wjonker.nl\">Email</a></h3></center>";
                AboutDialog aboutDialog = new AboutDialog(null, text);
                aboutDialog.setVisible(true);
            }
        });


        //Show the license dialog
        license.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                LicenseDialog licenseDialog = new LicenseDialog(null);

                licenseDialog.setVisible(true);
            }
        });

        //Load a new test.
        loadTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                File configFile = fileOpenDialog();
                if (configFile != null) {
                    if (configFile.exists()) {
                        try {
                            model.loadNewAAT(configFile);     //Only start when config is valid
                            runButton.setEnabled(true);
                            if (model.getTest().hasData()) {
                                exportData.setEnabled(true);
                            }
                        } catch (AatObject.FalseConfigException e) {
                            runButton.setEnabled(false);
                            exportData.setEnabled(false);
                            JOptionPane.showMessageDialog(null,
                                    e.getMessage(),
                                    "Configuration error",
                                    JOptionPane.ERROR_MESSAGE);
                            System.out.println("Configuration error: " + e.getMessage());
                        }

                    }
                }
            }
        });


        //Start a new test
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                joystick = new JoystickController(model);
                joystick.start(); //Start joystick Thread

                if (testFrame != null) {
                    model.deleteObserver(testFrame); //Remove old instance
                }
                testFrame = new TestFrame(model);
                model.addObserver(testFrame);
                model.startTest();
                runButton.setEnabled(false);
            }
        });


        //Export data
        exportData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ExportDataDialog export = new ExportDataDialog(model);
                export.setVisible(true);
            }
        });


        //Exit
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0); //quit the application
            }
        });

        setJMenuBar(menuBar);
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(400, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }


    //File dialog to select a file to be opened
    public File fileOpenDialog() {

        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        return file;
    }

    public void update(Observable observable, Object o) {
        if (o.toString().equals("Finished")) {
            joystick.exit();
            joystick = null; //Remove instance when finished
            exportData.setEnabled(true);
            runButton.setEnabled(true);

        }
    }
}


//Dialog showing the license
class LicenseDialog extends JDialog {

    public LicenseDialog(JFrame parent) {
        super(parent, "License", true);
        JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPane);
        File f = new File("License" + File.separatorChar + "gpl.html");
        java.net.URL fileURL = null;
        try {
            fileURL = f.toURI().toURL(); // Transform path into URL
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        try {
            textPane.setPage(fileURL); // Load the file to the editor
        } catch (IOException e) {
            e.printStackTrace();
        }

        Box b = Box.createVerticalBox();
        b.add(scrollPane);
        getContentPane().add(b, "Center");
        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        p2.add(ok);
        getContentPane().add(p2, "South");

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        doLayout();
        setSize(400, 300);
    }
}

//About dialog
class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent, String text) {
        super(parent, "About AAT", true);
        JEditorPane textPane = new JEditorPane();
        textPane.setEditable(false);
        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
                if (type == HyperlinkEvent.EventType.ACTIVATED) {
                    final URL url = hyperlinkEvent.getURL();
                    try {
                        URI uri = new URI(url.toString());
                        Desktop.getDesktop().mail(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        HTMLEditorKit kit = new HTMLEditorKit();
        textPane.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color: black; font-family:times; margin: 0px; background-color: white;font : 16px monaco;}");
        styleSheet.addRule("p {color: white; font-family:times; margin: 0px; background-color: #000;font : 30px monaco;}");
        styleSheet.addRule("h1 {color: blue;font : 24px roman;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        textPane.setContentType("text/html");
        textPane.setText(text);
        JScrollPane scrollPane = new JScrollPane(textPane);
        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        b.add(scrollPane);
        getContentPane().add(b, "Center");

        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        p2.add(ok);
        getContentPane().add(p2, "South");

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        setSize(400, 300);
    }
}


