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

import AAT.Configuration.TestMetaData;
import DataStructures.AATImage;
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


    public static void exportQuestionnaire(TestMetaData metaData, File file, int minRTime, int maxRTime, int errorPerc, boolean includePractice) {

        Document doc = createCopiedDocument(metaData.getData());
        doc = testIDFilter(doc, String.valueOf(metaData.getExport_id()));
        NodeList questionList = doc.getElementsByTagName("question");
        if (questionList.getLength() > 0) {

            if (!includePractice) {
                doc = removePractice(doc);
            }
            HashMap<String, Integer> errors = errorPercentages(doc, metaData, minRTime, maxRTime);
            doc = removeParticipants(doc, errors, errorPerc);
            writeQuestionsToCSV(doc, file);
            MessageDialog.getInstance().showMessage();
        } else {
            System.out.println("There are no questions");
        }
    }

    public static void exportMeasurements(TestMetaData metaData, File file, int minRTime, int maxRTime, int errorPerc, boolean includePractice, boolean removeCenter) {
        Document doc = createCopiedDocument(metaData.getData());
        int export_id = metaData.getExport_id();
        System.out.println("Filter the document for test id " + export_id);
        doc = testIDFilter(doc, String.valueOf(export_id));
        if (!includePractice) {
            doc = removePractice(doc);
        }
        HashMap<String, Integer> errors = errorPercentages(doc, metaData, minRTime, maxRTime);
        doc = checkValues(doc, metaData.getCenterPos(), minRTime, maxRTime, removeCenter);
        doc = removeParticipants(doc, errors, errorPerc);
        writeMeasuresToCSV(doc, file, String.valueOf(export_id), includePractice, metaData);
        MessageDialog.getInstance().showMessage();
    }

    /**
     * Returns a hashmap containing all the different test versions available in the data file.
     * Each time the configuration for a test is altered, taking a new AAT will create a new revision
     * these revisions need to be exported seperately, because they can have different datastructures e.g. different number of images or trials
     *
     * @param  doc Data document
     * @return  Get all test revisions present in the data file
     */
    public static HashMap<String, String> getTestRevisions(Document doc) {
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
     * @param doc  Data document
     * @param test_id  test id value
     * @return  Document that only contains the test data specified for a given test id
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
     * @param centerPos    The center position of the joystick. Used for checking whether the reaction time belonging to an image is reliable.
     * @param minRTime     minimum allowed reaction time
     * @param maxRtime     maximum allowed reaction time
     * @param removeCenter Does it need to check for wrong center positions
     * @return the changed document
     */
    private static Document checkValues(Document doc, int centerPos, int minRTime, int maxRtime, boolean removeCenter) {
        NodeList participantList = doc.getElementsByTagName("participant");
        for (int i = 0; i < participantList.getLength(); i++) {
            Element participant = (Element) participantList.item(i);
            NodeList imageList = participant.getElementsByTagName("image");
            for (int x = 0; x < imageList.getLength(); x++) {
                Element image = (Element) imageList.item(x);
                NodeList rTimeList = image.getElementsByTagName("reactionTime");   //Check reactionTime
                Node rTimeNode = rTimeList.item(0).getFirstChild();
                int rTime = Integer.parseInt(rTimeNode.getNodeValue());
                //   System.out.println("Reaction time " + rTime);
                if (rTime > maxRtime || rTime < minRTime) {
                    rTimeNode.setNodeValue("");
                }
                if (removeCenter) {
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
     * @param metaData Use the meta data to collect the variables necessary for the exportation of data.
     * @param minRTime minimum allowed reaction time
     * @param maxRTime maximum allowed reaction time
     * @return hashmap containing id's and error percentages
     */
    private static HashMap<String, Integer> errorPercentages(Document doc, TestMetaData metaData, int minRTime, int maxRTime) {
        HashMap<String, Integer> errors = new HashMap<String, Integer>();
        NodeList participantsList = doc.getElementsByTagName("participant");
        for (int x = 0; x < participantsList.getLength(); x++) {
            Element element = (Element) participantsList.item(x);
            String id = element.getAttribute("id");
            errors.put(id, calculateErrorPercentages(element, metaData, minRTime, maxRTime));
        }
        return errors;
    }

    /**
     * @param element  Contains the current participant
     * @param metaData For the necessary test variables
     * @return total error percentage for a given participant
     */
    private static int calculateErrorPercentages(Element element, TestMetaData metaData, int minRTime, int maxRTime) {
        int errors = 0;
        int centerPos = metaData.getCenterPos();
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
            String pushTag = metaData.getPushTag();
            String pullTag = metaData.getPullTag();
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


    private static void writeMeasuresToCSV(Document doc, File file, String export_id, boolean includePractice, TestMetaData metaData) {
        HashMap<String, Integer> variableMap = createVariableMap(doc, export_id, includePractice, metaData.hasColoredBorders()); //create a map containing variable names and position
        try {
            writeDataToCSVFile(createDataTable(doc, metaData, variableMap, includePractice), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[][] createDataTable(Document doc, TestMetaData metaData, HashMap<String, Integer> variableMap, boolean includePractice) {
        int columns = variableMap.size() + 1;
        NodeList participantList = doc.getElementsByTagName("participant");

        int rows = participantList.getLength() + 1;
        System.out.println("Created table with " + rows + " rows and " + columns + " columns");
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
            System.out.println("No. trials " + trialList.getLength());
            int start = 0;
            if (!includePractice) {
                start = 1;
            }
            for (int n = start; n < trialList.getLength(); n++) {
                System.out.println("TRIAL " + n);
                Element trial = (Element) trialList.item(n);
                NodeList imageList = trial.getElementsByTagName("image");
                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    int z = n - start;             //Correct for possible practice
                    String imageName = z + "_" + createVariableName(image, metaData.getAfflabel(), metaData.getNeutLabel());
                    NodeList rTimeList = image.getElementsByTagName("reactionTime");
                    Node rTimeNode = rTimeList.item(0).getFirstChild();
                    String reactionTime = rTimeNode.getNodeValue();
                    System.out.println("Image " + imageName + " reaction time " + reactionTime);
                    int pos = variableMap.get(imageName);
                    pos++; //Shift 1 to the right
                    tableData[x + 1][pos] = reactionTime;
                }
            }
        }
        return tableData;
    }


    private static HashMap<String, Integer> createVariableMap(Document doc, String export_id, boolean includePractice, boolean borders) {
        HashMap<String, Integer> outputData = new HashMap<String, Integer>();
        NodeList testList = doc.getElementsByTagName("test");
        for (int x = 0; x < testList.getLength(); x++) {
            Element testElement = (Element) testList.item(x);
            String test_id = testElement.getAttribute("test_id");
            String pullTag = testElement.getAttribute("pullTag");
            String pushTag = testElement.getAttribute("pushTag");
            String labelA = testElement.getAttribute("labelA");
            String labelN = testElement.getAttribute("labelN");

            int trials = Integer.parseInt(testElement.getAttribute("trials"));

            if (test_id.equalsIgnoreCase(export_id)) {       //Add the two directions of each image to the variableMap
                int practiceCount = 0;
                if (includePractice) {
                    addPractice(outputData, testElement, pullTag, pushTag, borders);
                    practiceCount = outputData.size();
                }
                boolean hasPractice = false;
                if (outputData.size() > 0) {
                    hasPractice = true; //Practice images were added
                }
                if (borders) {               //When the test features auto-generated borders, add each variable twice for the pull and push category.
                    addToVariableMap(outputData, testElement, pullTag, labelA, labelN, practiceCount, trials, hasPractice);
                    addToVariableMap(outputData, testElement, pushTag, labelA, labelN, outputData.size(), trials, hasPractice);
                } else {
                    //Do not specify the tag, So all images are added only once. Their filenames already contain the pull and push tags.
                    addToVariableMap(outputData, testElement, "", labelA, labelN, practiceCount, trials, hasPractice);
                }
            }
        }
        return outputData;
    }


    private static void addPractice(HashMap<String, Integer> map, Element testElement, String pullTag, String pushTag, boolean borders) {
        NodeList imageList = testElement.getElementsByTagName("image");
        int count = 0;
        if (borders) {
            for (int i = 0; i < imageList.getLength(); i++) {
                Element image = (Element) imageList.item(i);
                String typeAttr = image.getAttribute("type");

                if (Integer.parseInt(typeAttr) == AATImage.PRACTICE) {
                    String variableName = 0 + "_" + createVariableName(image, pullTag, null, null);

                    map.put(variableName, count);
                    System.out.println("Adding variable " + variableName + " at position " + count);
                    count++;

                }
            }
            for (int i = 0; i < imageList.getLength(); i++) {                         //Do it twice for a so that push and pull will be grouped in the output.
                Element image = (Element) imageList.item(i);
                String typeAttr = image.getAttribute("type");

                if (Integer.parseInt(typeAttr) == AATImage.PRACTICE) {
                    String variableName = 0 + "_" + createVariableName(image, pushTag, null, null);

                    map.put(variableName, count);
                    System.out.println("Adding variable " + variableName + " at position " + count);
                    count++;
                }
            }
        } else {
            for (int i = 0; i < imageList.getLength(); i++) {
                Element image = (Element) imageList.item(i);
                String typeAttr = image.getAttribute("type");

                if (Integer.parseInt(typeAttr) == AATImage.PRACTICE) {
                    String variableName = 0 + "_" + createVariableName(image, "", null, null);
                    map.put(variableName, count);
                    System.out.println("Adding variable 2 " + variableName + " at position " + count);
                    count++;

                }
            }
        }
    }


    /**
     * Append variable name to the variableMap.
     *  @param testElement Measured image
     * @param tag push or pull
     * @param startCount first joystick position
     */
    private static void addToVariableMap(HashMap<String, Integer> map, Element testElement, String tag, String labelA, String labelN, int startCount, int trials, boolean hasPractice) {


        for (int y = 0; y < trials; y++) {
            NodeList imageList = testElement.getElementsByTagName("image");
            int z = 0;  //Seperate counter because of the possible removement of the practice images.
            int totalImages = countImages(imageList, false);
            for (int i = 0; i < imageList.getLength(); i++) {
                Element image = (Element) imageList.item(i);
                String typeAttr = image.getAttribute("type");

                if (Integer.parseInt(typeAttr) != AATImage.PRACTICE) {
                    int count = startCount + z + (y * totalImages);
                    String variableName;
                    if (hasPractice) {
                        int y2 = y + 1;
                        variableName = y2 + "_" + createVariableName(image, tag, labelA, labelN);
                    } else {
                        variableName = y + "_" + createVariableName(image, tag, labelA, labelN);
                    }
                    map.put(variableName, count);
                    z++;
                }
            }
        }
    }


    private static int countImages(NodeList imageList, boolean includePractice) {
        if (includePractice) {
            return imageList.getLength();
        } else {
            int count = 0;
            for (int x = 0; x < imageList.getLength(); x++) {
                Element image = (Element) imageList.item(x);
                String typeAttr = image.getAttribute("type");

                if (Integer.parseInt(typeAttr) == AATImage.PRACTICE) {
                    count++;
                }
            }
            return imageList.getLength() - count; //Corect for the practice images;
        }
    }


    /**
     * Construct the variable name, based on the information coming from the data.xml.
     * This should match the variable name that was created based on the metadata.
     *
     * @param image image name
     * @param labelA affective label
     * @param labelN neutral label
     * @return The name for the variable that will be placed in the output file
     */
    private static String createVariableName(Element image, String labelA, String labelN) {
        NodeList directionList = image.getElementsByTagName("direction");
        System.out.println("Directions " + directionList.getLength());
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
        String typeValue = "";
        if (type.getNodeValue().equalsIgnoreCase(labelA) || type.getNodeValue().equalsIgnoreCase(labelN)) {
            typeValue = "_" + type.getNodeValue();
        }

        return imageName + typeValue + dirValue;
    }


    private static String createVariableName(Element image, String direction, String labelA, String labelN) {
        String imageName = image.getAttribute("file");
        String typeAttr = image.getAttribute("type");
        String type = "";
        //    if (direction.length() > 0) {
        if (Integer.parseInt(typeAttr) == AATImage.AFFECTIVE) {
            type = labelA;
        }
        if (Integer.parseInt(typeAttr) == AATImage.NEUTRAL) {
            type = labelN;
        }

        //    if (imageName.toLowerCase().contains(direction.toLowerCase()) && direction.length() >0) {       //Images with pull or push tag in the file name
        //       return imageName + "_" + type;

        //  } else
        if (direction.length() == 0 && labelA == null) {         //practice image with the push or pull tag in the file name
            return imageName;
        } else if (direction.length() == 0) {
            return imageName + "_" + type;
        } else {
            //   System.out.println(imageName + "_" + type + "_"+direction);
            return imageName + "_" + type + "_" + direction;
        }
        //  } else {
        //    return imageName + "_" + type;
        // }
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


