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
 */
public class JoystickController {

    private static final int DELAY = 5;  // ms  (polling interval)
    private static final float EPSILON = 0.0001f;
    private AATModel model;

    public JoystickController(AATModel model) {
        this.model = model;
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment(); //Verbinding via het os met de joystick maken.

        Controller[] cs = ce.getControllers();      //Lijst met alle aangesloten game-controllers
        if (cs.length == 0) {           //foutmelding geven als er een joystick is gevonden.
            System.out.println("No controllers found");
            System.exit(0);
        }

        Component yAxis = cs[0].getComponent(Component.Identifier.Axis.Y);   //Y-as
        Component trigger = cs[0].getComponent(Component.Identifier.Button.TRIGGER); //Trigger-button
        pollComponent(cs[0], yAxis);
      //  pollComponent(cs[0], trigger);


    }

    private void pollComponent(Controller c, Component component) {
        float prevValue = 0.0f;
        float currValue;
        boolean pollIsValid;  // new

        //       int i = 1;   // used to format the output
        while (true) {
            try {
                Thread.sleep(DELAY);      // wait a while
            } catch (Exception ex) {
            }

            pollIsValid = c.poll(); // update the controller's components
            if (pollIsValid) {
                currValue = component.getPollData();
                if (currValue != prevValue) {  // value has changed
                //    if (Math.abs(currValue) > EPSILON) {
                        model.changeYaxis(convertValue(currValue));
                  //  }
                    prevValue = currValue;
                }
            } else
                System.out.println("Controller no longer valid");
        }
    }


    //Zet de float waarde van de joystick om in Integer tussen -3 en 3
    private int convertValue(float value) {
        if (value > 0.25 && value < 0.5) {
            return 1;
        }
        if (value > .5 && value < 0.75) {
            return 2;
        }
        if (value == 1) {
            return 3;
        }
        if (value < -0.25 && value > -0.5) {
            return -1;
        }
        if (value < -0.5 && value > -0.75) {
            return -2;
        }
        if (value == -1) {
            return -3;
        } else {
            return 0;
        }
    }

}
