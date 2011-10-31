import Controller.JoystickController;
import Model.AATModel;
import views.ExportDataDialog;
import views.TestFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:22 PM
 * Main frame for the test. This frame is the controller from which test can be started. This is also the place where the user
 * can choose to display the results and save to file.
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
        mainPanel = new JPanel();
        mainPanel.setSize(800, 600);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadTest = new JMenuItem("Load new AAT");
        final JMenuItem startTest = new JMenuItem("Start test");
        startTest.setEnabled(false);
        final JMenuItem exportData = new JMenuItem("Export Data");
        exportData.setEnabled(false);
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(loadTest);
        fileMenu.add(startTest);
        fileMenu.add(exportData);
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        //...create and add some menus...
        menuBar.add(Box.createHorizontalGlue());
        JMenuItem about = new JMenuItem("About");
//...create the rightmost menu...
        menuBar.add(about);

        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAboutDialog aboutDialog = new SimpleAboutDialog(null);
                aboutDialog.setEnabled(true);
                aboutDialog.setVisible(true);
            }
        });

        //Start a new test.
        loadTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                File configFile = fileOpenDialog();
                if (configFile.exists()) {
                    model = new AATModel(configFile);
                    testFrame = new TestFrame(model);
                    startTest.setEnabled(true);
                    if (model.hasData()) {
                        exportData.setEnabled(true);
                    }
                }
            }
        });

        startTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                joystick = new JoystickController(model);
                joystick.start(); //Start joystick Thread

                model.addObserver(testFrame);
                model.startTest();
            }
        });

        exportData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ExportDataDialog export = new ExportDataDialog(model);
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0); //quit the application
            }
        });

        setJMenuBar(menuBar);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        pack();
        this.setEnabled(true);
        this.setVisible(true);

    }

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
        b.add(new JLabel("This test was created by"));
        b.add(new JLabel("Marcel Zuur"));
        b.add(new JLabel("       &      "));
        b.add(new JLabel("Wilfried Jonker"));
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
