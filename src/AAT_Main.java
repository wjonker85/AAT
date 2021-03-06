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

import AAT.Configuration.Validation.FalseConfigException;
import Controller.JoystickController;
import DataStructures.AATDataRecorder;
import IO.DataExporter;
import Model.AATModel;
import Views.AAT.TestFrame;
import Views.Export.SelectTestRevision;
import Views.TestUpgrade.ExportDataDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
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


    final JMenuItem exportData;
    final JButton runButton;
    private AATModel model;
    private JoystickController joystick;
    private TestFrame testFrame;
    private AATDataRecorder inputAATDataRecorder;

    //Build the user interface
    public AAT_Main() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setLayout(new GridBagLayout());
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
        JMenuItem exportFrom = new JMenuItem("Export from another data xml file");
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(loadTest);
        fileMenu.add(exportData);
        fileMenu.add(exportFrom);
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        menuBar.add(Box.createHorizontalGlue());
        JMenu aboutMenu = new JMenu("About");
        JMenuItem about = new JMenuItem("About AAT");
        JMenuItem license = new JMenuItem("License");
        aboutMenu.add(about);
        aboutMenu.add(license);
        menuBar.add(aboutMenu);

        model = new AATModel();
        model.addObserver(getInstance());


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
                            try {
                                BufferedReader br = new BufferedReader(new FileReader(model.getTest().getTestConfiguration().getDataFile()));
                                if (br.readLine() != null) {
                                    exportData.setEnabled(true);
                                }
                            } catch (IOException e) {
                                System.out.println("Data read error: " + e.getMessage());
                            }
                            joystick = new JoystickController(model);
                            joystick.start(); //Start joystick Thread
                            model.addObserver(joystick);
                        } catch (FalseConfigException e) {
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
                try {
                    model.deleteObservers();
                    testFrame = new TestFrame(model);
                    model.addObserver(testFrame);
                    model.addObserver(joystick);
                    model.addObserver(getInstance());
                    joystick.enableTrigger(); //set flag to trigger
                    model.startTest(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    runButton.setEnabled(false);
                }
            }
        });


        //Export data. Using the current loaded data.
        exportData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {

                HashMap<String, String> testData = DataExporter.getTestRevisions(model.getAATDataRecorder().getDocument());

                if (testData.size() > 1) {
                    SelectTestRevision testRevision = new SelectTestRevision(testData, model.getTest().getTestConfiguration().getTestID(), model, true);
                    testRevision.setEnabled(true);
                    testRevision.setVisible(true);
                    testRevision.requestFocus();
                } else {                 //Data from just one test.
                    ExportDataDialog export = new ExportDataDialog(model.getAATDataRecorder().getTestMetaData(model.getTest().getTestConfiguration().getTestID()));
                    export.setVisible(true);
                }
            }
        });


        //Export data from another data .xml file.
        exportFrom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                File dataFile = fileOpenDialog(new AAT.Util.ExtensionFileFilter("XML File", new String[]{"XML", "xm;"}));
                if (dataFile != null) {
                    if (dataFile.exists()) {
                        inputAATDataRecorder = new AATDataRecorder(dataFile, model);      //Load data from xml file. Notifies observers when ready.
                    }
                }
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

    /**
     * Start the main thread.
     *
     * @param args no arguments used
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("control", Color.decode("#f2f0ee"));
                UIManager.put("nimbusBlueGrey", Color.decode("#eeece9"));
                UIManager.put("nimbusSelectionBackground", Color.decode("#f27b4b"));
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        try {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                new AAT_Main().setVisible(true);
            }
        });
    }

    //File dialog to select a file to be opened, accepts a file filter.
    public File fileOpenDialog(FileFilter fileFilter) {

        JFileChooser fc = new JFileChooser();
        // fc.setCurrentDirectory(workingDir);
        File file = null;

        fc.setFileFilter(fileFilter);
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        if (file != null) {
            return file;
        } else {
            return null;
        }
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
            testFrame = null;
            model.deleteObservers();
            exportData.setEnabled(true);
            runButton.setEnabled(true);
            System.gc();
            model.addObserver(this.getInstance());
        } else if (o.toString().equalsIgnoreCase("Data_loaded_export")) {
            HashMap<String, String> testData = DataExporter.getTestRevisions(model.getExportDocument());
            if (testData.size() > 1) {
                SelectTestRevision testRevision = new SelectTestRevision(testData, -99, model, false);
                testRevision.setEnabled(true);
                testRevision.setVisible(true);
                testRevision.requestFocus();
            } else {
                System.out.println("Single set of data found for id " + model.getExport_id());
                model.setExport_id(model.getExport_id(), false);        //Set export ID and notify observers
            }
        } else if (o.toString().equalsIgnoreCase("Export")) {
            System.out.println("Showing data exporter, using id " + model.getExport_id());
            ExportDataDialog export = new ExportDataDialog(model.getAATDataRecorder().getTestMetaData(model.getExport_id()));
            export.setVisible(true);
        } else if (o.toString().equalsIgnoreCase("Export_foreign")) {
            inputAATDataRecorder = model.getExportAATDataRecorder();
            System.out.println("Showing data exporter, using id " + model.getExport_id());
            ExportDataDialog export = new ExportDataDialog(inputAATDataRecorder.getTestMetaData(model.getExport_id()));
            export.setVisible(true);
        } else if (o.toString().equalsIgnoreCase("Escape")) {
            model.deleteObservers();
            model.addObserver(getInstance());
        }
    }


    private Observer getInstance() {
        return this;
    }
}


//Dialog showing the license
class LicenseDialog extends JDialog {

    public LicenseDialog(JFrame parent) {
        super(parent, "License", true);
        JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        textPane.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color: black; font-family:times; margin: 0px; background-color: white;font : 10px monaco;}");
        styleSheet.addRule("p {color: black; font-family:times; margin: 0px; background-color: white;font : 10px monaco;}");
        styleSheet.addRule("h1 {color: blue;font : 24px roman;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        textPane.setContentType("text/html");
        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
                if (type == HyperlinkEvent.EventType.ACTIVATED) {
                    final URL url = hyperlinkEvent.getURL();
                    try {
                        URI uri = new URI(url.toString());
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(textPane);
        File f = new File("license" + File.separatorChar + "gpl.html");
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
        setSize(600, 450);
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


