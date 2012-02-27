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

package Controller;

import Model.AATModel;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 5:09 PM
 * This joystick controller reads the y-axis from a joystick and listens to the Trigger button of that joystick
 * The class searches for a joystick with the help of the JInput library. This library connects this java program to the
 * Operating system (Windows or Linux). Because of different readouts on linux and windows the search and assignment of buttons is
 * somewhat different. Detection methods try to overcome this.
 * <p/>
 * When a participant moves the joystick it will return an integer value depending on how far the joystick is moved from its center.
 * The accuracy of this is based on the stipsize and error margin values in the config
 */
public class JoystickController extends Thread {

    private static final int DELAY = 5;  // ms  (polling interval)
    private int stepSizeDisplay;
    private int stepSizeData = 9;
    private AATModel model;
    private Component yAxis;
    private Component trigger;
    private Controller joyStick;
    private boolean stopThread = false;
    private float delta = 0.001f;


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
    }

    //Start the thread
    public void run() {
        stepSizeDisplay = model.getTest().getStepRate();
        pollController(joyStick);
    }

    //Ends the thread
    public void exit() {
        stopThread = true;
    }


    //Find the trigger button. Button may be called Trigger or Button 0.
    private Component getTrigger(Controller con) {
        Component trigger = con.getComponent(Component.Identifier.Button.TRIGGER);
        if (trigger != null) {
            return trigger;
        } else {
            trigger = con.getComponent(Component.Identifier.Button._0);
        }
        if (trigger != null) {
            return trigger;
        } else {
            System.out.println("No trigger button found");
            return null;
        }
    }

    //Search all attached controllers and returns the first joystick found
    private Controller findJoystick(Controller[] controllers) {
        for (int x = 0; x < controllers.length; x++) {
            if (controllers[x].getType().equals(Controller.Type.STICK)) {
                System.out.println("Controller.JoystickController found " + controllers[x].getName());
                return controllers[x];
            }
        }
        return null;
    }


    /* Will be run in a seperate Thread. Will poll the joystick with a given delay. Will poll the y-axis and the trigger button
    Will call methods in the model when there is a change.
    */
    private void pollController(Controller c) {
        float prevYValue = 0.0f;
        float yAxisValue;
        float prevTrigger = 0.0f;
        boolean pollIsValid;  // new

        //       int i = 1;   // used to format the output
        while (!stopThread) {
            try {
                Thread.sleep(DELAY);      // wait a while
            } catch (Exception ex) {
            }

            pollIsValid = c.poll(); // update the controller's components
            if (pollIsValid) {
                yAxisValue = yAxis.getPollData();
                if (yAxisValue != prevYValue) {  // value has changed
                    model.changeYaxis(convertValue2(yAxisValue,stepSizeData),convertValue2(yAxisValue,stepSizeDisplay));
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

    /*
    Converts the float value between -1 and 1 to an integer value.
     */
    public int convertValue2(float value,int NoSteps) {
        int returnValue =0;
        int middlePos = (NoSteps + 1) / 2;
        float steps = NoSteps - middlePos;
        float increment = (float) (1 / steps);
        if(value>0) {                                           //Correct for small joystick error, otherwise it will not reach the end
                                                                //with large stepsizes
        returnValue = (int) ((value / increment)+delta);
        }
        if (value<0){
            returnValue = (int) ((value / increment)-delta);
        }
        if (value==0) {
            returnValue = (int) (value / increment);
        }
        return middlePos + returnValue;
    }
}
