import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 5:09 PM
 * JoystickController die met behulp van de JInput library verbinding maakt met een aangesloten joystick. Twee functies zijn van belang
 * De waarden van de y-as en of de "Throttle" button ingedrukt wordt.
 * <p/>
 * Y-as geeft waarden van -3 tot 3 via 0 als middenpunt
 * TODO: Nog abstracte class maken die als parent dient voor de verschillende vormen van input. Nu alleen eerst de joystick even werkend
 * TODO: krijgen.
 * TODO: Op dit moment kan hij alleen de y-as aflezen en omzetten naar waarden tussen -3 en 3. Dit voor het stapsgewijs resizen van de plaatjes.
 * TODO: Meting van de reactietijd werkt nog niet en het aflezen van de "Trigger" Button
 * <p/>
 * TODO: Methode maken die de joystick detecteerd
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

        joyStick = findJoystick(cs);
        yAxis = joyStick.getComponent(Component.Identifier.Axis.Y);   //Y-as

       for(int x =0;x<joyStick.getComponents().length;x++) {
           System.out.println(joyStick.getComponents()[x].getIdentifier().getName());
           System.out.println(Component.Identifier.Button.TRIGGER.toString());
       }
        trigger = joyStick.getComponent(Component.Identifier.Button._0); //Trigger-button

        //  pollComponent(cs[0], trigger);


    }

    //Start the thread
    public void run() {
        pollComponent(joyStick, yAxis);
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

    private void pollComponent(Controller c, Component component) {
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


    //Zet de float waarde van de joystick om in Integer tussen -3 en 3
    private int convertValue(float value) {
        if (value > 0.25 && value < 0.5) {
            return 5;
        }
        if (value > 0.5 && value < 1) {
            return 6;
        }
        if (value == 1) {
            return 7;
        }
        if (value < -0.25 && value > -0.5) {
            return 3;
        }
        if (value < -0.5 && value > -1) {
            return 2;
        }
        if (value == -1) {
            return 1;
        } else {
            return 4;
        }
    }

}
