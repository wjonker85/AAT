import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 5:09 PM
 * JoystickController die met behulp van de JInput library verbinding maakt met een aangesloten joystick. Twee functies zijn van belang
 * De waarden van de y-as en of de "Throttle" button ingedrukt wordt.
 * <p/>
 * Y-as geeft waarden van -3 tot 3 via 0 als middenpunt
TODO: Iets grotere foutmarge maken en nog wat meer stappen toevoegen.
 TODO: Aantal stappen moet uit configuratiebestand komen.
 */
public class JoystickController extends Thread {

    private static final int DELAY = 5;  // ms  (polling interval)
    private static final float EPSILON = 0.0001f;
    private AATModel model;
    private Component yAxis;
    private Component trigger;
    private ControllerEnvironment ce;
    private Controller joyStick;


    public JoystickController(AATModel model) {
        this.model = model;
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment(); //Verbinding via het os met de joystick maken.

        Controller[] cs = ce.getControllers();      //Lijst met alle aangesloten controllers
        if (cs.length == 0) {           //foutmelding geven als er een joystick is gevonden.
            System.out.println("No controllers found");
            System.exit(0);
        }

        joyStick = findJoystick(cs);   //Search for an attached joystick
        yAxis = joyStick.getComponent(Component.Identifier.Axis.Y);   //Y-as
        trigger = getTrigger(joyStick);  //Search for trigger button

        //  pollComponent(cs[0], trigger);


    }

    //Start the thread
    public void run() {
        pollController(joyStick);
    }


    //Find the trigger button. Button may be called Trigger or Button 0, maybe even different with other joysticks.
    //TODO: Check different systems/joysticks

    private Component getTrigger(Controller con) {
        Component trigger = con.getComponent(Component.Identifier.Button.TRIGGER);
        if(trigger !=null) {
            return trigger;
        }
        else {
            trigger = con.getComponent(Component.Identifier.Button._0);
        }
        if(trigger !=null) {
            return trigger;
        }
        else {
            System.out.println("No trigger button found");
            return null;
        }
    }

    //Search all attached controllers and returns the first joystick found
    private Controller findJoystick(Controller[] controllers) {
        for (int x = 0; x < controllers.length; x++) {
            if (controllers[x].getType().equals(Controller.Type.STICK)) {
                System.out.println("JoystickController found " + controllers[x].getName());
                return controllers[x];
            }
        }
        return null;
    }


    //Will be run in a seperate Thread. Will poll the joystick with a given delay. Will poll the y-axis and the trigger button
    //Will call methods in the model when there is a change.
    private void pollController(Controller c) {
        float prevYValue = 0.0f;
        float yAxisValue;
        float prevTrigger = 0.0f;
        boolean pollIsValid;  // new

        //       int i = 1;   // used to format the output
        while (true) {
            try {
                Thread.sleep(DELAY);      // wait a while
            } catch (Exception ex) {
            }

            pollIsValid = c.poll(); // update the controller's components
            if (pollIsValid) {
                yAxisValue = yAxis.getPollData();
                if (yAxisValue != prevYValue) {  // value has changed
                    //    if (Math.abs(currValue) > EPSILON) {
                    model.changeYaxis(convertValue(yAxisValue));
                    //  }
                    prevYValue = yAxisValue;
                }
                if (trigger.getPollData() == 1 && prevTrigger != 1.0f) {   // only changes
                    model.triggerPressed(); //Notify model that the trigger button is pressed.
                    prevTrigger = 1.0f;
                } else if (trigger.getPollData() == 0) {   // reset prevTrigger
                    prevTrigger = 0f;
                }
            } else
                System.out.println("Controller no longer valid");
        }
    }

  //TODO: Nog veranderen zodat dit dynamisch gebeurt.
    //Changes float value to an Integer from 1 to 7.
    private int convertValue(float value) {
        if (value > 0 && value <= 0.33) {
            return 6;
        }
        if (value > 0.33 && value <= 0.66) {
            return 7;
        }
        if(value >0.66 && value < 1)  {
            return 8;
        }
        if (value == 1) {
            return 9;
        }
        if (value < 0 && value > -0.33) {
            return 4;
        }
        if (value <= -0.33 && value > -0.66) {
            return 3;
        }
        if(value<=-0.66 && value > -1)
            return 2;
        if (value == -1) {
            return 1;
        } else {
            return 5;      //Stick in the middle
        }
    }

}
