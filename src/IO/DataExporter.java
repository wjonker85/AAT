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

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
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
        doc = testIDFilter(doc, String.valueOf(model.getExport_id()));
        NodeList questionList = doc.getElementsByTagName("question");
        if (questionList.getLength() > 0) {

            if (!includePractice) {
                doc = removePractice(doc);
            }
            HashMap<String, Integer> errors = errorPercentages(doc, model, minRTime, maxRTime);
            doc = removeParticipants(doc, errors, errorPerc);
            writeQuestionsToCSV(doc, file);
            MessageDialog.getInstance().showMessage();
        } else {
            System.out.println("There are no questions");
        }
    }

    public static void exportMeasurements(AATModel model, File file, int minRTime, int maxRTime, int errorPerc, boolean includePractice, boolean removeCenter) {
        Document doc = createCopiedDocument(model.getTestData().getDocument());
        System.out.println("Filter the document for test id " + model.getExport_id());
        doc = testIDFilter(doc, String.valueOf(model.getExport_id()));
        if (!includePractice) {
            doc = removePractice(doc);
        }
        HashMap<String, Integer> errors = errorPercentages(doc, model, minRTime, maxRTime);
        doc = checkValues(doc, model, minRTime, maxRTime, removeCenter);
        doc = removeParticipants(doc, errors, errorPerc);
        writeMeasuresToCSV(doc, file, String.valueOf(model.getExport_id()));
        MessageDialog.getInstance().showMessage();
    }

    public static void exportMeasurementsAnova(AATModel model, File file, int minRTime, int maxRTime, int errorPerc, boolean includePractice, boolean removeCenter) {
        Document doc = createCopiedDocument(model.getTestData().getDocument());
        doc = testIDFilter(doc, String.valueOf(model.getExport_id()));
        if (!includePractice) {
            doc = removePractice(doc);
        }
        HashMap<String, Integer> errors = errorPercentages(doc, model, minRTime, maxRTime);
        doc = checkValues(doc, model, minRTime, maxRTime, removeCenter);
        doc = removeParticipants(doc, errors, errorPerc);
        writeMeasuresAnovaToCSV(doc, file);
        MessageDialog.getInstance().showMessage();
    }

    /**
     * Returns a hashmap containing all the different test versions available in the data file.
     *
     * @param model
     * @return
     */
    public static HashMap<String, String> getTestRevisions(AATModel model) {
        Document doc = model.getTestData().getDocument();
        HashMap<String, String> result = new HashMap<String, String>();
        NodeList testList = doc.getElementsByTagName("test");
        for (int x = 0; x < testList.getLength(); x++) {
            Element test = (Element) testList.item(x);
            String idValue = test.getAttribute("test_id");

            NodeList commentList = test.getElementsByTagName("comment");

            Node commentNode = commentList.item(0).getFirstChild();
            String comment = commentNode.getNodeValue();
            result.put(idValue, comment);
        }

        return result;
    }


    /**
     * Create a copy of the original Dom document. This way, the document can be changed without changing the original.
     *
     * @param originalDocument The original document which will be copied
     * @return altered document
     */
    private static Document createCopiedDocument(Document originalDocument) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
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
                System.out.println("Id " + participant.getAttribute("id") + " will be removed");
                MessageDialog.getInstance().addLine("Id " + participant.getAttribute("id") + " will be removed");
                participant.getParentNode().removeChild(participant);
                doc.normalize();
                return doc;          //No need to go further
            }
        }
        return doc;
    }

    /**
     * Filter the participant data based on test id.
     *
     * @param doc
     * @param test_id
     * @return
     */
    private static Document testIDFilter(Document doc, String test_id) {
        NodeList participantsList = doc.getElementsByTagName("participant");
        for (int x = 0; x < participantsList.getLength(); x++) {
            Element participant = (Element) participantsList.item(x);
            String idValue = participant.getAttribute("test_id");
            System.out.println("Removing participant " + participant.getAttribute("id"));
            if (!idValue.equalsIgnoreCase(test_id)) {
                //  System.out.println("Id " + participant.getAttribute("id") + " will be removed");
                //    MessageDialog.getInstance().addLine("Id " + participant.getAttribute("id") + " will be removed");
                participant.getParentNode().removeChild(participant);
                //   doc.normalize();
                //   return doc;          //No need to go further
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
        NodeList participantList = doc.getElementsByTagName("participant");
        for (int i = 0; i < participantList.getLength(); i++) {
            Element participant = (Element) participantList.item(i);
            NodeList imageList = participant.getElementsByTagName("image");
            for (int x = 0; x < imageList.getLength(); x++) {
                Element image = (Element) imageList.item(x);
                NodeList rTimeList = image.getElementsByTagName("reactionTime");   //Check reactionTime
                Node rTimeNode = rTimeList.item(0).getFirstChild();
                int rTime = Integer.parseInt(rTimeNode.getNodeValue());
                System.out.println("Reaction time " + rTime);
                if (rTime > maxRtime || rTime < minRTime) {
                    rTimeNode.setNodeValue("");
                }
                if (removeCenter) {
                    int centerPos = model.getTest().centerPos();
                    NodeList firstPosList = image.getElementsByTagName("firstPos");
                    Node firstPos = firstPosList.item(0).getFirstChild();
                    int fPos = Integer.parseInt(firstPos.getNodeValue());

                    if (fPos < centerPos - 1 || fPos > centerPos + 1) {      //Only center +1 or -1 are correct values
                        rTimeNode.setNodeValue("");
                    }
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
        NodeList typeList = doc.getElementsByTagName("type");
        System.out.println("No images " + typeList.getLength());
        try {
            for (int x = typeList.getLength() - 1; x >= 0; x--) {
                Element typeElement = (Element) typeList.item(x);
                String typeValue = typeElement.getFirstChild().getNodeValue();
                System.out.println("Type " + typeValue);
                if (typeValue.equalsIgnoreCase("practice")) {
                    Element image = (Element) typeElement.getParentNode();
                    image.getParentNode().removeChild(image);
                }
                //  NodeList typeList = image.getElementsByTagName("type");
                //    System.out.println("Searching practice image at "+x);
                //    Node typeNode = typeList.item(0).getFirstChild();
                //     String type = typeNode.getNodeValue();
                //   System.out.println("Found "+type);
                //     Element trial = (Element) image.getParentNode();

                //    if (type.equalsIgnoreCase("practice")) {
                //         System.out.println("Removing image at "+x);
                //       trial.removeChild(image);
                // image.getParentNode().removeChild(image);
                //  trial.getParentNode().removeChild(trial);
                //  doc.normalize();    //TODO kijken of dit nodig is.
                //  return doc;
                //   }
            }
            NodeList typeList2 = doc.getElementsByTagName("type");
            System.out.println("No images after" + typeList2.getLength());
        } catch (Exception e) {
            System.out.println("No practice images found");
            return doc;
        }
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
        MessageDialog.getInstance().addLine("Total = " + totalImages + " Participant with id " + id + " has " + errors + " errors. Is " + percentage + " percent");
        return (int) percentage;
    }

    private static void writeQuestionsToCSV(Document doc, File file) {
        try {
            NodeList participantsList = doc.getElementsByTagName("participant");
            Element firstParticipant = (Element) participantsList.item(0);
            NodeList firstQuestions = firstParticipant.getElementsByTagName("question");
            int noQuestions = firstQuestions.getLength();
            String[][] data = new String[participantsList.getLength() + 1][noQuestions + 1]; //Add extra row for header and extra column for id
            data[0][0] = "id";
            for (int n = 0; n < firstQuestions.getLength(); n++) {
                Element question = (Element) firstQuestions.item(n);
                NodeList keyList = question.getElementsByTagName("key");
                String key = keyList.item(0).getFirstChild().getNodeValue();
                data[0][n + 1] = key;
            }


            for (int x = 0; x < participantsList.getLength(); x++) {
                Element participant = (Element) participantsList.item(x);
                NodeList questionsList = participant.getElementsByTagName("question");
                data[x + 1][0] = participant.getAttribute("id");
                for (int i = 0; i < questionsList.getLength(); i++) {
                    Element question = (Element) questionsList.item(i);
                    NodeList answerList = question.getElementsByTagName("answer");
                    String answer = answerList.item(0).getFirstChild().getNodeValue();
                    data[x + 1][i + 1] = answer;
                }
            }


            writeDataToCSVFile(data, file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Problem exporting measures, possible that all the measures contain too much mistakes",
                    "Configuration error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Problem exporting measures, possible that all the measures contain too much mistakes");
        }
    }


    private static void writeMeasuresToCSV(Document doc, File file, String export_id) {
        HashMap<String, Integer> variableMap = createVariableMap(doc, export_id); //create a map containing variable names and position
        try {
            writeDataToCSVFile(createDataTable(doc, variableMap), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeMeasuresAnovaToCSV(Document doc, File file) {
        HashMap<String, Integer> variableMap = createVariableMapAnova(doc); //create a map containing variable names and position
        System.out.println("Variablemap " + variableMap.size());
        try {
            writeDataToCSVFile(createDataTableAnova(doc, variableMap), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[][] createDataTableAnova(Document doc, HashMap<String, Integer> variableMap) {
        int columns = variableMap.size() + 3; //data +3 extra variables
        NodeList participantList = doc.getElementsByTagName("participant");
        int rows = participantList.getLength() + 1;  //Total rows + header
        String[][] tableData = new String[rows][columns];
        tableData[0][0] = "id";
        tableData[0][columns - 2] = "Type";
        tableData[0][columns - 1] = "Direction";

        for (String key : variableMap.keySet()) {    //create first row
            int pos = variableMap.get(key);
            pos++; //shift 1 to the right for the id column;
            System.out.println(key + " " + pos);
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
                    NodeList nameList = image.getElementsByTagName("imageName");
                    Node imageNameNode = nameList.item(0).getFirstChild();
                    String imageName = imageNameNode.getNodeValue();
                    String variableName = n + "_" + imageName; //Trial nr + imageName
                    NodeList rTimeList = image.getElementsByTagName("reactionTime");
                    Node rTimeNode = rTimeList.item(0).getFirstChild();
                    String reactionTime = rTimeNode.getNodeValue();
                    int pos = variableMap.get(variableName);
                    pos++; //Shift 1 to the right
                    tableData[x + 1][pos] = reactionTime;
                    NodeList typeList = image.getElementsByTagName("type");
                    Node type = typeList.item(0).getFirstChild();
                    NodeList directionList = image.getElementsByTagName("direction");
                    Node direction = directionList.item(0).getFirstChild();
                    tableData[x + 1][columns - 2] = type.getNodeValue();
                    tableData[x + 1][columns - 1] = direction.getNodeValue();
                }
            }
        }
        return tableData;
    }

    private static String[][] createDataTable(Document doc, HashMap<String, Integer> variableMap) {
        int columns = variableMap.size() + 3; //TODO was +1
        NodeList participantList = doc.getElementsByTagName("participant");

        int rows = participantList.getLength() + 1;
        System.out.println("Created table with "+rows+ " rows and "+columns+" columns");
        String[][] tableData = new String[rows][columns];
        for (int x = 0; x < tableData.length; x++) {        //Fill the array with empty strings;
            for (int y = 0; y < tableData[0].length; y++) {
                tableData[x][y] = "";
            }
        }
        //create header
        tableData[0][0] = "id";
        for (String key : variableMap.keySet()) {    //create first row
            int pos = variableMap.get(key);
            System.out.println("key pos " + key + " " + pos + " " + columns);
            pos++; //shift 1 to the right for the id column;
            tableData[0][pos] = key;

        }

        for (int x = 0; x < participantList.getLength(); x++) {
            Element participant = (Element) participantList.item(x);
            tableData[x + 1][0] = participant.getAttribute("id");
            NodeList trialList = participant.getElementsByTagName("trial");
            System.out.println("No. trials "+trialList.getLength());
            for (int n = 0; n < trialList.getLength(); n++) {
                Element trial = (Element) trialList.item(n);
                NodeList imageList = trial.getElementsByTagName("image");
                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    String imageName = n + "_" + createVariableName(image);
                    NodeList rTimeList = image.getElementsByTagName("reactionTime");
                    Node rTimeNode = rTimeList.item(0).getFirstChild();
                    String reactionTime = rTimeNode.getNodeValue();
                    System.out.println("Image " + imageName);
                    int pos = variableMap.get(imageName);
                    System.out.println("TEST " + pos);
                    pos++; //Shift 1 to the right
                    tableData[x + 1][pos] = reactionTime;
                }
            }
        }
        return tableData;
    }

    /**
     * Create a differen variableMap, this one only contains the image names
     *
     * @param doc
     * @return
     */
    private static HashMap<String, Integer> createVariableMapAnova(Document doc) {
        HashMap<String, Integer> outputData = new HashMap<String, Integer>();
        try {
            NodeList participantsList = doc.getElementsByTagName("participant");
            Element firstParticipant = (Element) participantsList.item(0);
            NodeList trialList = firstParticipant.getElementsByTagName("trial");
            for (int x = 0; x < trialList.getLength(); x++) {
                Element trial = (Element) trialList.item(x);
                NodeList imageList = trial.getElementsByTagName("image");

                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    int count = i + (x * (imageList.getLength() / 2)); //Half the size push and pull not counted

                    NodeList nameList = image.getElementsByTagName("imageName");
                    Node imageNameNode = nameList.item(0).getFirstChild();
                    String imageName = imageNameNode.getNodeValue();
                    String variableName = x + "_" + imageName; //Trial nr + imageName
                    if (!outputData.containsKey(variableName)) {
                        outputData.put(variableName, count);
                    }

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Problem exporting measures, possible that all the measures contain too much mistakes",
                    "Configuration error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Problem exporting measures, possible that all the measures contain too much mistakes");
        }
        return correctValues(outputData);

    }

    private static HashMap<String, Integer> createVariableMap(Document doc, String export_id) {
        HashMap<String, Integer> outputData = new HashMap<String, Integer>();
        try {
            int y = 0;    //Count the number of valid participants.
            NodeList testList = doc.getElementsByTagName("test");
            for (int x = 0; x < testList.getLength(); x++) {
                Element testElement = (Element) testList.item(x);
                String test_id = testElement.getAttribute("test_id");
                if (test_id.equalsIgnoreCase(export_id)) {
                    NodeList imageList = doc.getElementsByTagName("image");
                    for (int i = 0; i < imageList.getLength(); i++) {
                        Element image = (Element) imageList.item(i);
                        int count = i + (y * imageList.getLength());
                        String variableName = y + "_" + createVariableName(image);
                        System.out.println("Adding variable "+variableName+" at position "+count);
                        outputData.put(variableName, count);
                    }
                    y++;
                }
            }
            //         Element firstParticipant = (Element) participantsList.item(0);

            //       NodeList trialList = firstParticipant.getElementsByTagName("trial");
            //     Element firstTrial = (Element) trialList.item(0);
            //    NodeList imageList = firstTrial.getElementsByTagName("image");
            //    Element firstImage = (Element) imageList.item(0);
            //    NodeList typeList = firstImage.getElementsByTagName("type");
            //   Node type = typeList.item(0).getFirstChild();
            //   if(type.getNodeValue().equalsIgnoreCase("practice"))    { //use next trial in list
            //       firstTrial = (Element) trialList.item(1);
            //       imageList = firstTrial.getElementsByTagName("image");
            //   }
            //     for (int x = 0; x < trialList.getLength(); x++) {         //First collect the variable names
            //         Element trial = (Element) trialList.item(x);
            //         NodeList imageList = trial.getElementsByTagName("image");
            //         for (int i = 0; i < imageList.getLength(); i++) {
            //             Element image = (Element) imageList.item(i);
            //             int count = i + (x * imageList.getLength());
            //             String variableName = x + "_" + createVariableName(image);
            //             outputData.put(variableName, count);
            //         }
            //   }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Problem exporting measures, possible that all the measures contain too much mistakes",
                    "Configuration error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Problem exporting measures, possible that all the measures contain too much mistakes");
        }
        return outputData;
    }

    private static HashMap<String, Integer> correctValues(HashMap<String, Integer> map) {
        HashMap<String, Integer> newMap = new HashMap<String, Integer>();
        int x = 0;
        for (String key : map.keySet()) {
            newMap.put(key, x);
            x++;
        }
        return newMap;
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
            dirValue = "";
        }
        return imageName + "_" + type.getNodeValue() + dirValue;
    }

    private static void writeDataToCSVFile(String[][] data, File file) throws IOException {
        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);
        int x;
        for (x = 0; x < data.length; x++) {
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


//About dialog
class MessageDialog extends JDialog {

    private String text = "";
    private static MessageDialog instance = null;
    private JEditorPane textPane;

    public MessageDialog(JFrame parent) {
        super(parent, "Export output", true);
        System.out.println("Show dialog");
        System.out.println(text);
        textPane = new JEditorPane();
        textPane.setEditable(false);
        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
                if (type == HyperlinkEvent.EventType.ACTIVATED) {
                    final URL url = hyperlinkEvent.getURL();
                    try {
                        URI uri = new URI(url.toString());
                        Desktop.getDesktop().mail(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //     textPane.setText(text);
        JScrollPane scrollPane = new JScrollPane(textPane);
        Box b = Box.createVerticalBox();
        b.add(Box.createGlue());
        b.add(scrollPane);
        getContentPane().add(b, "Center");

        JPanel p2 = new JPanel();
        JButton ok = new JButton("Ok");
        p2.add(ok);
        getContentPane().add(p2, "South");

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        setPreferredSize(new Dimension(500, 400));
        setMinimumSize(new Dimension(500, 400));
    }

    public void addLine(String line) {
        text = text + line + "\n";
    }

    public void showMessage() {
        textPane.setText(text);
        this.setVisible(true);
    }

    public static MessageDialog getInstance() {
        if (instance == null) {
            instance = new MessageDialog(null);
        }
        return instance;
    }
}


