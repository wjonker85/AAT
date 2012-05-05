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

package DataStructures;

import AAT.AatObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 4/21/12
 * Time: 2:11 PM
 */


public class TestData {

    private Document doc;
    private int trials;
    private File dataFile;
    private AatObject newAAT;

    public TestData(AatObject newAAT) {
        this.newAAT = newAAT;
        this.trials = newAAT.getRepeat();
        if (newAAT.hasPractice()) {    //Add 1 tot the number of trials when there is a practice trial
            trials++;
        }
        this.dataFile = newAAT.getDataFile();
        if (dataFile.exists()) {
            loadFileData();
        } else {
            createXMLDOC();
        }
        getHighestID();
    }

    private void loadFileData() {
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            doc = docBuilder.parse(dataFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getHighestID() {

        NodeList participantsList = doc.getElementsByTagName("participant");
        if (participantsList.getLength() > 0) {
            Element lastParticipant = (Element) participantsList.item(participantsList.getLength() - 1);
            // Node id = lastParticipant.getAttributes().getNamedItem("id");
            System.out.println("Highest id = " + lastParticipant.getAttribute("id"));
            return Integer.parseInt(lastParticipant.getAttribute("id"));
        } else {       //No data present, so start with 0
            return 0;
        }
    }

    private void createXMLDOC() {
        try {
            /////////////////////////////
            //Creating an empty XML Document

            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            doc = docBuilder.newDocument();

            ////////////////////////
            //Creating the XML tree
            //create the root element and add it to the document
            Element root = doc.createElement("data_Set");
            doc.appendChild(root);

            //create a comment and put it in the root element
            Comment comment = doc.createComment("All the test data");
            root.appendChild(comment);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This method adds all the data gathered about a participant that has taken the test to the Document. This includes
     * the answers to the questionnaire and all the measurements
     *
     * @param newParticipant The participant data
     */

    public void addParticipant(ParticipantData newParticipant) {

        Element root = doc.getDocumentElement();

        Element participant = doc.createElement("participant");
        HashMap<String, String> questionnaire = newParticipant.getQuestionnaire();
        participant.setAttribute("id", String.valueOf(newParticipant.getId()));
        // child.setAttribute("name", "value");
        root.appendChild(participant);
        if (questionnaire != null) {
            for (String key : questionnaire.keySet()) {
                Element question = doc.createElement("question");
                Element keyElement = doc.createElement("key");
                Text keyValue = doc.createTextNode(key);
                question.appendChild(keyElement);
                keyElement.appendChild(keyValue);
                Element answerElement = doc.createElement("answer");
                Text answerValue = doc.createTextNode(questionnaire.get(key));
                question.appendChild(answerElement);
                answerElement.appendChild(answerValue);
                participant.appendChild(question);
            }
        }
        for (int x = 0; x < trials; x++) {
            Element trial = doc.createElement("trial");
            trial.setAttribute("no", String.valueOf(x));
            participant.appendChild(trial);

            for (ImageMeasureData imageData : newParticipant.getMeasurements(x)) {
                Element image = doc.createElement("image");
                Element imageName = doc.createElement("imageName");
                Text imageNameStr = doc.createTextNode(imageData.getImageName());
                imageName.appendChild(imageNameStr);
                image.appendChild(imageName);
                Element direction = doc.createElement("direction");
                String dirValue = newAAT.getPullTag();
                if (imageData.getDirection() == AATImage.PUSH) {
                    dirValue = newAAT.getPushTag();
                }
                Text imgDirection = doc.createTextNode(dirValue);
                direction.appendChild(imgDirection);
                image.appendChild(direction);

                Element type = doc.createElement("type");
                Text imgType = doc.createTextNode(newAAT.getType(imageData.getType()));
                type.appendChild(imgType);
                image.appendChild(type);

                Element time = doc.createElement("reactionTime");
                Text rTime = doc.createTextNode(String.valueOf(imageData.getReactionTime()));
                time.appendChild(rTime);
                image.appendChild(time);

                Element firstPos = doc.createElement("firstPos");
                Text firstValue = doc.createTextNode(String.valueOf(imageData.getFirstPosition()));
                firstPos.appendChild(firstValue);
                image.appendChild(firstPos);

                trial.appendChild(image);
            }
        }
        writeDataToFile(dataFile);
    }

    public void writeDataToFile(File file) {
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

    public Document getDocument() {
        return doc;
    }
}

