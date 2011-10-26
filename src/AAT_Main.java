import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:22 PM
 * Main frame for the test. This frame is the controller from which test can be started. This is also the place where the user
 * can choose to display the results and save to file.
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
    private AATView aatView;
    private AATResults aatResults;


    public static void main(String[] args) {
        AAT_Main main = new AAT_Main();
    }

    private void show() {
//        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.pack();
        frame.setVisible(true);
    }

    public AAT_Main() {
        frame = new JFrame("Approach Avoidance Task");
        model = new AATModel();
        aatResults = new AATResults();
        testFrame = new TestFrame();
        DisplayResults results = new DisplayResults();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) dim.getWidth();
        screenHeight = (int) dim.getHeight();

        model.addObserver(testFrame);
        model.addObserver(results);
        joystick = new JoystickController(model);
        joystick.start(); //Start joystick Thread

        aatResults.init();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem startTest = new JMenuItem("Start new Test");
        fileMenu.add(startTest);
        menuBar.add(fileMenu);

        //Start a new test.
        startTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(aatView !=null) {
                    model.deleteObserver(aatView);  //Remove previous instance of the aatView from the observers list.
                    testFrame.remove(aatView);
                }
                aatView = new AATView(screenWidth, screenHeight, 433, 433, model.getStepRate());
                aatView.init();             //Make sure the PApplet gets initialised.
                model.addObserver(aatView);    //Add a new aatView to the observers list.
                testFrame.getContentPane().add(aatView);
                model.startTest(3, 9);
            }
        });

        frame.setJMenuBar(menuBar);
        this.show();
        //   model.startTest(1,4);
    }
}

