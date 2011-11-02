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
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:22 PM
 * Main frame for the test. This frame is the controller from which test can be started. This is also the place where the user
 * can choose to export the gathered data for further analysis
 */

public class AAT_Main extends JFrame {


    private AATModel model;
    private JPanel mainPanel;
    private JoystickController joystick;
    private TestFrame testFrame;
    private File configFile;


    public static void main(String[] args) {
        AAT_Main main = new AAT_Main();
    }

    public AAT_Main() {
        this.setEnabled(true);
        this.setVisible(true);
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setLayout(new GridBagLayout());
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
        final JMenuItem exportData = new JMenuItem("Export Data");
        exportData.setEnabled(false);
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(loadTest);
        fileMenu.add(exportData);
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        menuBar.add(Box.createHorizontalGlue());
        JMenu aboutMenu = new JMenu("About");
        JMenuItem about = new JMenuItem("About AAT");
        aboutMenu.add(about);
        menuBar.add(aboutMenu);

        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAboutDialog aboutDialog = new SimpleAboutDialog(null);
                aboutDialog.setEnabled(true);
                aboutDialog.setVisible(true);
            }
        });

        //Load a new test.
        loadTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                File configFile = fileOpenDialog();
                if (configFile.exists()) {
                    model = new AATModel(configFile);
                    testFrame = new TestFrame(model);
                model.addObserver(testFrame);
                    runButton.setEnabled(true);
                    if (model.hasData()) {
                        exportData.setEnabled(true);
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
}


//Simple dialog copied from the internet
class SimpleAboutDialog extends JDialog {

    public SimpleAboutDialog(JFrame parent) {
        super(parent, "About Dialog", true);

        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        b.add(new JLabel("          This AAT was created by"));
        b.add(new JLabel("          Marcel Zuur"));
        b.add(new JLabel("                 &      "));
        b.add(new JLabel("          Wilfried Jonker"));
        b.add(Box.createGlue());
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

        setSize(250, 150);
    }
}

