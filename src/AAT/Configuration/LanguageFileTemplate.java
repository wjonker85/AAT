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

package AAT.Configuration;

/**
 * Created by marcel on 5/2/14.
 * Class that contains static methods containing default texts that are put onto the html editor components of the configuration builder
 * when a new file is selected.
 */
public class LanguageFileTemplate {

    public static String getEditorIntroText() {
        return "Do not forget:\n" +
                "Pull the plates with a <font color=\"00A4E7\">BLUE</font> border towards you, push the plates with a <font color = \"#F5FE02\">YELLOW</font> edge away from you. <br>" +
                "<br>" +
                "Click with your index finger to start the test!";

    }

    public static String getEditorStartText() {
        return "This was the last exercise.<br>" +
                "<br>" +
                "During the test there is a break.<br>" +
                "<br>" +
                "Do not forget:<br>" +
                "<br>" +
                "<font color=\"00A4E7\">BLUE</font> edge, pull the joystick towards you, <font color = \"#F5FE02\">YELLOW</font> edge: Push the joystick away from you.\n" +
                "<br>" +
                "Still have questions? Ask them now or start the test!";

    }

    public static String getEditorBreakText() {
        return "You can have now an one minute break if you want.<br>" +
                "<br>" +
                "Do not forget:<br>" +
                "<br>" +
                "<font color=\"00A4E7\">BLUE</font> border: pull the joystick towards you, <font color = \"#F5FE02\">YELLOW</font> edge: Push the joystick away from you.";

    }

    public static String getEditorFinishText() {
        return "The task is now finished. Thank you!";
    }
}
