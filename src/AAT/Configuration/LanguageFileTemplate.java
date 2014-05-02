package AAT.Configuration;

/**
 * Created by marcel on 5/2/14.
 * Class that contains static methods containing default texts that are put onto the html editor components of the configuration builder
 * when a new file is selected.
 */
public class LanguageFileTemplate {

    public static String getEditorIntroText() {
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

    public static String getEditorStartText() {
       return  "Do not forget:\n" +
               "Pull the plates with a <font color=\"00A4E7\">BLUE</font> border towards you, push the plates with a <font color = \"#F5FE02\">YELLOW</font> edge away from you. <br>" +
               "<br>" +
               "Click with your index finger to start the test!";

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
