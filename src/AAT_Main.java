import Controller.JoystickController;
import Model.AATModel;
import views.ExportDataDialog;
import views.TestFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
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
    private JPanel mainPanel;
    private JoystickController joystick;
    private TestFrame testFrame;
    final JMenuItem exportData;

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                new AAT_Main().setVisible(true);
            }
        });
    }


    public AAT_Main() {
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setLayout(new GridBagLayout());
        model = new AATModel();
        testFrame = new TestFrame(model);
        model.addObserver(this);
        model.addObserver(testFrame);
        joystick = new JoystickController(model);
        this.setTitle("Approach avoidance Task");
        BufferedImage buttonIcon = null;
        try {
            buttonIcon = ImageIO.read(new File("playButton100.png"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        final JButton runButton = new JButton(new ImageIcon(buttonIcon));
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

        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String text = "<h1>Approach avoidance task</h1>";
                text += "<center>Created By</center>";
                text += "<center><h3>Marcel Zuur</h3></center>";
                text += "<center>&</center>";
                text += "<center><h3>Wilfried Jonker</h3></center>";
                AboutDialog aboutDialog = new AboutDialog(null, text);
                aboutDialog.setVisible(true);
            }
        });

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

                            model.loadConfig(configFile);
                            runButton.setEnabled(true);
                            if (model.hasData()) {
                                exportData.setEnabled(true);
                            }
                        } catch (AATModel.FalseConfigException e) {
                            JOptionPane.showMessageDialog(null,
                                    e.getMessage(),
                                    "Configuration error",
                                    JOptionPane.ERROR_MESSAGE);
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
                model.startTest();
            }
        });


        //Export data
        exportData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ExportDataDialog export = new ExportDataDialog(model);
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

    private String getLicense() {
        File license = new File("License" + File.separatorChar + "lgpl.html");
        String text = "";
        String strLine;
        try {
            BufferedReader br = new BufferedReader(new FileReader(license));
            while ((strLine = br.readLine()) != null) {
                text += strLine + "\n";
                System.out.println(strLine);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return text;
    }

    public void update(Observable observable, Object o) {
        if (o.toString().equals("Finished")) {
            joystick.exit();
            exportData.setEnabled(true);
        }
    }
}


//Dialog showing the license
class LicenseDialog extends JDialog {

    public LicenseDialog(JFrame parent) {
        super(parent, "About AAT", true);
        JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        File f = new File("License" + File.separatorChar + "lgpl.html");
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


        //    textPane.setText(text);
        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        JScrollPane scrollPane = new JScrollPane(textPane);
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

//About dialog
class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent, String text) {
        super(parent, "About AAT", true);
        JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        textPane.setText(text);


        JScrollPane scrollPane = new JScrollPane(textPane);
        //    textPane.setText(text);
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


