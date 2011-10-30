import javax.swing.*;
import java.awt.*;
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
public class AAT_Main {


    private AATModel model;
    private JFrame frame;
    private JPanel mainPanel;
    private JoystickController joystick;
    private TestFrame testFrame;
    private int screenWidth;
    private int screenHeight;

    /*
Definieer view classes, deze classes worden door Wilfried in Processing geschreven.
    */

    public static void main(String[] args) {
        AAT_Main main = new AAT_Main();
    }

    private void show() {
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.pack();
        frame.setVisible(true);
    }

    public AAT_Main() {
        frame = new JFrame("Approach Avoidance Task");
        model = new AATModel(new File("sampleConfig"));
     //   aatResults = new AATResults();
        testFrame = new TestFrame(model);
        mainPanel = new JPanel();
        mainPanel.setSize(800,600);
    //    DisplayResults results = new DisplayResults();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) dim.getWidth();
        screenHeight = (int) dim.getHeight();
                joystick = new JoystickController(model);
        joystick.start(); //Start joystick Thread

        model.addObserver(testFrame);
  //      model.addObserver(results);
     //   aatResults.init();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem startTest = new JMenuItem("Start new Test");
        fileMenu.add(startTest);
        menuBar.add(fileMenu);

        //Start a new test.
        startTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                model.startTest();
            }
        });

        frame.setJMenuBar(menuBar);
        this.show();
        //   model.startTest(1,4);
    }
}

