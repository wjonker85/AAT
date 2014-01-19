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
import AAT.Util.FileUtils;
import Model.AATModel;
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
import java.util.ArrayList;
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
        //TODO gebruikte images toevoegen. Alleen als betreffende test_id nog niet bestaat.
        if (newAAT.hasPractice()) {    //Add 1 tot the number of trials when there is a practice trial
            trials++;
        }
        this.dataFile = newAAT.getDataFile();
        if (dataFile.exists()) {
            loadFileData();
            checkAndFixOldData(dataFile,doc,newAAT);
            if(!hasTestID(newAAT.getTest_id())) {
                System.out.println("Test id not present in the data file, adding required test data to the data file");
                addTestData(newAAT.getTest_id(), "testje2");  //Add the current test_id and used image files to the data.xml file.
            }
        } else {
            createXMLDOC();
            System.out.println("New data file created, adding test data");
            addTestData(newAAT.getTest_id(), "testje");
        }
        getHighestID();
    }

    /**
     *
     * @param test_id the current test_id
     * @return whether this test id is added to the datafile.
     */
    private boolean hasTestID(int test_id)  {

        NodeList testList = doc.getElementsByTagName("test");
        if (testList.getLength() > 0) {
            for(int x = 0;x<testList.getLength();x++) {
                Element test = (Element) testList.item(x);
                int value = Integer.parseInt(test.getAttribute("test_id"));
                if(value == test_id) {             //Test id has been used before, so no need for adding the images to the data file.
                    return true;
                }
            }
        }

        return false;
    }


    private void addTestData(int test_id, String comment) {
        Element root = doc.getDocumentElement();


        Element test = doc.createElement("test");
        root.appendChild(test);
        test.setAttribute("test_id", String.valueOf(test_id));
        Element commentElement = doc.createElement("comment");
        Text commentValue = doc.createTextNode(comment);
        commentElement.appendChild(commentValue);
        test.appendChild(commentElement);

        //Adding the affective images
        addImageToData(test,newAAT.affectiveImages,AATImage.AFFECTIVE);
        addImageToData(test,newAAT.neutralImages,AATImage.NEUTRAL);
        FileUtils.writeDataToFile(dataFile, doc);

    }

    //Add the used images to the data file.
    private void addImageToData(Element testElement,ArrayList<File> images, int type)  {
        for (File file : images) {
            System.out.println("Adding file to data.xml: "+file.getName());
            Element image = doc.createElement("image");
            testElement.appendChild(image);
            Attr fileAttr = doc.createAttribute("file");
            fileAttr.setValue(file.getName());
            image.setAttributeNode(fileAttr);
            Attr typeAttr = doc.createAttribute("type");
            typeAttr.setValue(String.valueOf(type));
            image.setAttributeNode(typeAttr);
        }
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
        participant.setAttribute("test_id", String.valueOf(newParticipant.getTestID()));
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
        FileUtils.writeDataToFile(dataFile, doc);
    }

    /**
     * Upgrade the data.xml file to the new structure.
     *
     * @param doc Xml document
     */
    private void checkAndFixOldData(File dataFile, Document doc, AatObject AAT) {
        System.out.println("Checking whether this test has data from older versions and upgrade the data file when necessary.");
        Boolean upgrade = false;
        NodeList participantsList = doc.getElementsByTagName("participant");
        for (int x = 0; x < participantsList.getLength(); x++) {
            Element participant = (Element) participantsList.item(x);
            if (!participant.hasAttribute("test_id")) {
                System.out.println("Old version detected, first add test id 0 to the participants.");
                upgrade = true;
                participant.setAttribute("test_id", "-1");
            }
        }
        if (upgrade) {
            addTestData(doc, AAT, -1, "Old test data");
        }
    }


    private void addTestData(Document doc, AatObject AAT, int test_id, String comment) {
        System.out.println("Add required test data.");
        Element root = doc.getDocumentElement();
        Element test = doc.createElement("test");
        root.appendChild(test);
        test.setAttribute("test_id", String.valueOf(test_id));
        Element commentElement = doc.createElement("comment");
        Text commentValue = doc.createTextNode(comment);
        commentElement.appendChild(commentValue);
        test.appendChild(commentElement);

        //Adding the affective images
        addImageToData(doc, test, collectAllImages(doc,AATImage.AFFECTIVE), AATImage.AFFECTIVE);
        addImageToData(doc, test, collectAllImages(doc,AATImage.NEUTRAL), AATImage.NEUTRAL);
        System.out.println("Writing the changes to disk");
        FileUtils.writeDataToFile(dataFile, doc);

    }

    //Add the used images to the data file.
    private  void addImageToData(Document doc, Element testElement, ArrayList<File> images, int type) {
        for (File file : images) {
            System.out.println("Adding file to data.xml: " + file.getName());
            Element image = doc.createElement("image");
            testElement.appendChild(image);
            Attr fileAttr = doc.createAttribute("file");
            fileAttr.setValue(file.getName());
            image.setAttributeNode(fileAttr);
            Attr typeAttr = doc.createAttribute("type");
            typeAttr.setValue(String.valueOf(type));
            image.setAttributeNode(typeAttr);
        }
    }

    /**
     * Find all the image file names used by participants which do not have an test_id attributed to them.
     * This is done so old data files can be upgraded to new style datafiles.
     *
     * @param doc  XML document
     * @param type Image type, affective, neutral or practice.
     * @return
     */
    private  ArrayList<File> collectAllImages(Document doc, int type) {
        String imgType = "";
        ArrayList<File> result = new ArrayList<File>();
        ArrayList<String> unique = new ArrayList<String>();
        if (type == AATImage.AFFECTIVE) {
            System.out.println("Upgrade: Collecting affective images");
            imgType = "Affective";
        } else if (type == AATImage.NEUTRAL) {
            System.out.println("Upgrade: Collecting neutral images");
            imgType = "Neutral";
        } else if (type == AATImage.PRACTICE) {
            System.out.println("Upgrade: Collecting practice images");
            imgType = "practice";
        }

        NodeList participantsList = doc.getElementsByTagName("participant");

        for (int x = 0; x < participantsList.getLength(); x++) {
            Element participant = (Element) participantsList.item(x);
            if (participant.getAttribute("test_id").equalsIgnoreCase(("-1"))) {
                System.out.println("Collecting images for participant "+participant.getAttribute("id"));
                NodeList imageList = participant.getElementsByTagName("image");
                System.out.println("Found "+imageList.getLength()+" "+"images");
                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    NodeList nameList = image.getElementsByTagName("imageName");
                    Node imageNameNode = nameList.item(0).getFirstChild();
                    String imageName = imageNameNode.getNodeValue();

                    NodeList typeList = image.getElementsByTagName("type");
                    Node typeNode = typeList.item(0).getFirstChild();
                    String typeValue = typeNode.getNodeValue();
                    System.out.println("Found image "+imageName+" "+typeValue);
                    if (typeValue.equalsIgnoreCase(imgType)) {
                        if (!unique.contains(imageName)) {
                            result.add(new File(imageName));
                            unique.add(imageName);
                            System.out.println("Upgrading datafile: adding " + imageName);
                        }
                    }


                }
                //TODO ook practice.
            }

        }
        return result;
    } 
    


    public Document getDocument() {
        return doc;
    }
}


