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
 * TODO let the size of the images depend on the screen size.
 */
public class AAT_Main extends JFrame {


    private AATModel model;
    private JPanel mainPanel;
    private JoystickController joystick;
    private TestFrame testFrame;
    private File configFile;


    /*
Definieer view classes, deze classes worden door Wilfried in Processing geschreven.
    */

    public static void main(String[] args) {
        AAT_Main main = new AAT_Main();

    }

    public AAT_Main() {
        mainPanel = new JPanel();
        mainPanel.setSize(800, 600);
        //      model.addObserver(results);
        //   aatResults.init();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadTest = new JMenuItem("Load new AAT");
        final JMenuItem startTest = new JMenuItem("Start test");
        startTest.setEnabled(false);
        JMenuItem exportData = new JMenuItem("Export Data");
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(loadTest);
        fileMenu.add(startTest);
        fileMenu.add(exportData);
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        //Start a new test.
        loadTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                File configFile = fileOpenDialog();
                if (configFile.exists()) {
                    model = new AATModel(configFile);
                            testFrame = new TestFrame(model);
                    startTest.setEnabled(true);
                }
                //    QuestionFrame questionFrame = new QuestionFrame(model);
                //    questionFrame.displayQuestions(model.getExtraQuestions());
                //    questionFrame.display();
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

