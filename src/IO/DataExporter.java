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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 4:24 PM
 * This class is used to export all the registered data in a form that can be used to do the data analysis.
 */
public class DataExporter {


    public static void exportQuestionnaire(AATModel model, File file, int minRTime, int maxRTime, int errorPerc, boolean includePractice) {
        Document doc = createCopiedDocument(model.getTestData().getDocument());
        if (!includePractice) {
            doc = removePractice(doc);
        }
        HashMap<String, Integer> errors = errorPercentages(doc, model, minRTime, maxRTime);
        doc = removeParticipants(doc, errors, errorPerc);
    }

    public static void exportMeasurements(AATModel model, File file, int minRTime, int maxRTime, int errorPerc, boolean includePractice, boolean removeCenter) {
        Document doc = createCopiedDocument(model.getTestData().getDocument());
        if (!includePractice) {
            doc = removePractice(doc);
        }
        HashMap<String, Integer> errors = errorPercentages(doc, model, minRTime, maxRTime);
        doc = checkValues(doc, model, minRTime, maxRTime, removeCenter);
        doc = removeParticipants(doc, errors, errorPerc);
        writeDataToFile(new File("testje.xml"), doc);
        writeMeasuresToCSV(doc, file);
    }

    /**
     * Create a copy of the original Dom document. This way, the document can be changed without changing the original.
     *
     * @param originalDocument
     * @return
     */
    private static Document createCopiedDocument(Document originalDocument) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document copiedDocument = null;
        try {
            db = dbf.newDocumentBuilder();
            Node originalRoot = originalDocument.getDocumentElement();
            copiedDocument = db.newDocument();
            Node copiedRoot = copiedDocument.importNode(originalRoot, true);
            copiedDocument.appendChild(copiedRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return copiedDocument;
    }

    /**
     * Remove participants from the document when their error percentage is too high
     *
     * @param doc     The document
     * @param errors  Hashmap containing the errorpercentages per participant
     * @param maxPerc Maximum allowed error percentage
     * @return the changed document
     */
    private static Document removeParticipants(Document doc, HashMap<String, Integer> errors, int maxPerc) {
        for (String id : errors.keySet()) {
            int ePerc = errors.get(id);
            if (ePerc > maxPerc) {
                doc = removeIDfromList(doc, id);
            }
        }
        return doc;
    }

    /**
     * Removes an participant with a certain id from the document
     *
     * @param doc The document
     * @param id  id to be removed
     * @return the changed document
     */
    private static Document removeIDfromList(Document doc, String id) {
        NodeList participantsList = doc.getElementsByTagName("participant");
        for (int x = 0; x < participantsList.getLength(); x++) {
            Element participant = (Element) participantsList.item(x);
            String idValue = participant.getAttribute("id");
            if (idValue.equalsIgnoreCase(id)) {
                System.out.println(participant.getAttribute("id") + " will be removed");
                participant.getParentNode().removeChild(participant);
                doc.normalize();
                return doc;          //No need to go further
            }
        }
        return doc;
    }

    /**
     * Check all the values in the document
     *
     * @param doc          The document
     * @param model        The model
     * @param minRTime     minimum allowed reaction time
     * @param maxRtime     maximum allowed reaction time
     * @param removeCenter Does it need to check for wrong center positions
     * @return the changed document
     */
    private static Document checkValues(Document doc, AATModel model, int minRTime, int maxRtime, boolean removeCenter) {
        NodeList imageList = doc.getElementsByTagName("image");
        for (int x = 0; x < imageList.getLength(); x++) {
            Element image = (Element) imageList.item(x);
            NodeList rTimeList = image.getElementsByTagName("reactionTime");   //Check reactionTime
            Node rTimeNode = rTimeList.item(0).getFirstChild();
            int rTime = Integer.parseInt(rTimeNode.getNodeValue());
            if (rTime > maxRtime || rTime < minRTime) {
                System.out.println("Changed reaction time " + rTime);
                rTimeNode.setNodeValue("N/A");
            }
            if (removeCenter) {
                int centerPos = model.getTest().centerPos();
                NodeList firstPosList = image.getElementsByTagName("firstPos");
                Node firstPos = firstPosList.item(0).getFirstChild();
                int fPos = Integer.parseInt(firstPos.getNodeValue());

                if (fPos < centerPos - 1 || fPos > centerPos + 1) {      //Only center +1 or -1 are correct values
                    rTimeNode.setNodeValue("N/A");
                }
            }
        }
        return doc;
    }

    /**
     * Removes all the practice trials from the document
     *
     * @param doc The current document
     * @return The changed document
     */
    private static Document removePractice(Document doc) {
        NodeList imageList = doc.getElementsByTagName("image");
        System.out.println("Data has a total of " + imageList.getLength() + " images");
        for (int x = imageList.getLength() - 1; x >= 0; x--) {
            Element image = (Element) imageList.item(x);
            NodeList typeList = image.getElementsByTagName("type");
            Node typeNode = typeList.item(0).getFirstChild();
            String type = typeNode.getNodeValue();
            Element trial = (Element) image.getParentNode();
            System.out.println("trial " + trial.getAttribute("no"));
            if (type.equalsIgnoreCase("practice")) {

                // image.getParentNode().removeChild(image);
                trial.getParentNode().removeChild(trial);
                doc.normalize();    //TODO kijken of dit nodig is.
                //  return doc;
            }
        }

        NodeList imageList2 = doc.getElementsByTagName("image");
        System.out.println("Data has a total of " + imageList2.getLength() + " images");
        return doc;
    }

    /**
     * Create an hashmap to collect the error percentages for all the participants
     *
     * @param doc      The document
     * @param model    Use the model to get some user set values, pushtag, pulltag and joystick center position
     * @param minRTime minimum allowed reaction time
     * @param maxRTime maximum allowed reaction time
     * @return hashmap containing id's and error percentages
     */
    private static HashMap<String, Integer> errorPercentages(Document doc, AATModel model, int minRTime, int maxRTime) {
        HashMap<String, Integer> errors = new HashMap<String, Integer>();
        NodeList participantsList = doc.getElementsByTagName("participant");
        for (int x = 0; x < participantsList.getLength(); x++) {
            Element element = (Element) participantsList.item(x);
            String id = element.getAttribute("id");
            errors.put(id, calculateErrorPercentages(element, model, minRTime, maxRTime));
        }
        return errors;
    }

    /**
     * @param element Contains the current participant
     * @param model   For the necessary test variables
     * @return total error percentage for a given participant
     */
    private static int calculateErrorPercentages(Element element, AATModel model, int minRTime, int maxRTime) {
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

    private static void writeDataToFile(File file, Document doc) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeMeasuresToCSV(Document doc, File file) {
        HashMap<String, Integer> variableMap = createVariableMap(doc); //create a map containing variable names and position
        for (String key : variableMap.keySet()) {    //just for debugging
            System.out.println(key + "on position " + variableMap.get(key));
        }
        try {
            writeDataToCSVFile(createDataTable(doc, variableMap), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[][] createDataTable(Document doc, HashMap<String, Integer> variableMap) {
        int columns = variableMap.size() + 1;
        NodeList participantList = doc.getElementsByTagName("participant");
        int rows = participantList.getLength() + 1;
        String[][] tableData = new String[rows][columns];
        //create header
        tableData[0][0] = "id";
        for (String key : variableMap.keySet()) {    //create first row
            int pos = variableMap.get(key);
            pos++; //shift 1 to the right for the id column;
            tableData[0][pos] = key;
        }

        for (int x = 0; x < participantList.getLength(); x++) {
            Element participant = (Element) participantList.item(x);
            tableData[x + 1][0] = participant.getAttribute("id");
            NodeList trialList = participant.getElementsByTagName("trial");
            for (int n = 0; n < trialList.getLength(); n++) {
                Element trial = (Element) trialList.item(n);
                NodeList imageList = trial.getElementsByTagName("image");
                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    String imageName = n + "_" + createVariableName(image);
                    NodeList rTimeList = image.getElementsByTagName("reactionTime");
                    Node rTimeNode = rTimeList.item(0).getFirstChild();
                    String reactionTime = rTimeNode.getNodeValue();
                    System.out.println("New imageName " + imageName + "reactionTime " + reactionTime);
                    if (!variableMap.containsKey(imageName)) {
                        System.out.println("Staat niet in de lijst " + imageName);
                    }
                    int pos = variableMap.get(imageName);
                    pos++; //Shift 1 to the right
                    tableData[x + 1][pos] = reactionTime;
                    System.out.println("Position " + pos);
                }
            }
        }
        testData(tableData);
        return tableData;
    }

    private static void testData(String[][] data) {
        for (int x = 0; x < data.length; x++) {
            for (int i = 0; i < data[x].length; i++) {
                System.out.println(x + " " + i + " " + data[x][i]);
            }
        }
    }

    private static HashMap<String, Integer> createVariableMap(Document doc) {
        HashMap<String, Integer> outputData = new HashMap<String, Integer>();
        NodeList participantsList = doc.getElementsByTagName("participant");
        Element firstParticipant = (Element) participantsList.item(0);

        NodeList trialList = firstParticipant.getElementsByTagName("trial");
        for (int x = 0; x < trialList.getLength(); x++) {         //First collect the variable names
            Element trial = (Element) trialList.item(x);
            System.out.println("Trial no " + trial.getAttribute("no"));
            NodeList imageList = trial.getElementsByTagName("image");
            for (int i = 0; i < imageList.getLength(); i++) {
                Element image = (Element) imageList.item(i);
                int count = i + (x * imageList.getLength());
                String variableName = x + "_" + createVariableName(image);
                outputData.put(variableName, count);
                System.out.println(variableName + " " + count);
            }
        }
        NodeList imageList = firstParticipant.getElementsByTagName("image");
        System.out.println("First participant " + firstParticipant.getAttribute("id") + " has " + imageList.getLength() + " images");
        return outputData;
    }

    private static String createVariableName(Element image) {
        NodeList directionList = image.getElementsByTagName("direction");
        Node direction = directionList.item(0).getFirstChild();
        NodeList typeList = image.getElementsByTagName("type");
        Node type = typeList.item(0).getFirstChild();
        NodeList nameList = image.getElementsByTagName("imageName");
        Node imageNameNode = nameList.item(0).getFirstChild();
        String imageName = imageNameNode.getNodeValue();
        String dirValue = "_" + direction.getNodeValue();
        if (imageName.contains(direction.getNodeValue())) {
            System.out.println("Vervangen " + imageName);
            dirValue = "";
        }
        System.out.println("Direction " + direction.getNodeValue() + " type " + type.getNodeValue());
        String variableName = imageName + "_" + type.getNodeValue() + dirValue;
        return variableName;
    }

    private static void writeDataToCSVFile(String[][] data, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);
        for (int x = 0; x < data.length; x++) {
            for (int i = 0; i < data[x].length; i++) {
                pw.print(data[x][i]);
                if (i != data[x].length - 1) {
                    pw.print(",");
                }
            }
            pw.println();
        }
        pw.flush();
        pw.close();
        fw.close();
    }
}
