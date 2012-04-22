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

package IO;

import Model.AATModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 4:24 PM
 * This class is used to export all the registered data in a form that can be used to do the data analysis.
 */
public class DataExporter {


    public static void exportQuestionnaire(AATModel model, File file, int minRTime, int maxRTime, int errorPerc) {

    }

    public static void exportMeasurements(AATModel model, File file, int minRTime, int maxRTime, int errorPerc) {
        HashMap<String, Integer> errors = errorPercentages(model, minRTime, maxRTime);
    }

    private static HashMap<String, Integer> errorPercentages(AATModel model, int minRTime, int maxRTime) {
        HashMap<String, Integer> errors = new HashMap<String, Integer>();
        NodeList participantsList = model.getTestData().getDocument().getElementsByTagName("participant");
        for (int x = 0; x < participantsList.getLength(); x++) {
            Element element = (Element) participantsList.item(x);
            String id = element.getAttribute("id");
            errors.put(id, errorPercentage(element, model, minRTime, maxRTime));
        }
        return errors;
    }

    /**
     * @param element Contains the current participant
     * @param model   For the necessary test variables
     * @return total error count for a given participant
     */
    private static int errorPercentage(Element element, AATModel model, int minRTime, int maxRTime) {
        int errors = 0;
        int centerPos = model.getTest().centerPos();
        NodeList imageList = element.getElementsByTagName("image");
        String id = element.getAttribute("id");

        for (int x = 0; x < imageList.getLength(); x++) {
            Element image = (Element) imageList.item(x);
            NodeList firstPosList = image.getElementsByTagName("firstPos");
            Node firstPos = firstPosList.item(0).getFirstChild();
            int fPos = Integer.parseInt(firstPos.getNodeValue());
            NodeList directionList = image.getElementsByTagName("direction");
            Node direction = directionList.item(0).getFirstChild();
            String imgDirection = direction.getNodeValue();
            String pushTag = model.getTest().getPushTag();
            String pullTag = model.getTest().getPullTag();
            if (imgDirection.equalsIgnoreCase(pushTag)) {   //push image
                if (fPos != centerPos - 1) {  //correct first position for push image
                    errors++;
                }
            }
            if (imgDirection.equalsIgnoreCase(pullTag)) {  //pull Image
                if (fPos != centerPos + 1) {       //correct first position for pull image
                    errors++;
                }
            }
            NodeList rTimeList = image.getElementsByTagName("reactionTime");
            Node rTimeNode = rTimeList.item(0).getFirstChild();
            int rTime = Integer.parseInt(rTimeNode.getNodeValue());
            if (rTime < minRTime || rTime > maxRTime) {
                errors++;
            }
        }
        int totalImages = imageList.getLength();
        float percentage = ((float) errors / (float) totalImages) * 100f;
        System.out.println("Total = " + totalImages + " Participant with id " + id + " has " + errors + " errors. Is " + percentage + " percent");
        return (int) percentage;
    }
}
