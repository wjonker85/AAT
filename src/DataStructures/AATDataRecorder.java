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

import AAT.AbstractAAT;
import AAT.Util.FileUtils;
import Model.AATModel;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import views.TestUpgrade.LabelInputFrame;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 4/21/12
 * Time: 2:11 PM
 * <p/>
 * This class can parse a data.xml file to check for already present data and metadata.When not present, e.g. it is the first time a test is taken, metadata
 * is added to the data.xml file.
 * It is possible that this data was stored with a previous version of the AAT. Older versions did not store metadata. To make older
 * versions compatible an upgrade is necessary. This upgrade is done automatically by gathering the required metadata and adding it to the data.xml file.
 */


public class AATDataRecorder {

    private Document doc;
    private int trials;
    private File dataFile;
    private AbstractAAT newAAT;
    public String affLabelOldData;
    public String neutLabelOldData;
    public String pullTagOldData;
    public String pushTagOldData;
    private String affLabel, neutLabel;
    private AATModel model;
    private boolean externalData;

    public AATDataRecorder(AbstractAAT newAAT, AATModel model) {
        externalData = false;
        this.model = model;
        this.newAAT = newAAT;
        this.trials = newAAT.getTestConfiguration().getTrials();

        affLabel = newAAT.getTestConfiguration().getAffectiveDir().getName();
        if(affLabel.contains(File.separator)) {
            affLabel = affLabel.substring(affLabel.lastIndexOf(File.separator));      //Only the last directory name for the affective category.
        }
        neutLabel = newAAT.getTestConfiguration().getNeutralDir().getName();
        if(neutLabel.contains(File.separator)) {
            neutLabel = neutLabel.substring(neutLabel.lastIndexOf(File.separator));
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateString = dateFormat.format(date);

        if (newAAT.getTestConfiguration().getHasPractice()) {    //Add 1 tot the number of trials when there is a practice trial
            trials++;
        }
        this.dataFile = newAAT.getTestConfiguration().getDataFile();
        if (dataFile.exists()) {
            System.out.println("No trials is set to " + trials);
            loadFileData();
            checkAndFixOldData(doc);
            if (!hasTestID(newAAT.getTestConfiguration().getTestID())) {
                System.out.println("Test id not present in the data file, adding required test data to the data file");
                addTestMetaData(newAAT.getTestConfiguration().getTestID(), trials, newAAT.centerPos(), newAAT.getTestConfiguration().getPullTag(), newAAT.getTestConfiguration().getPushTag(), affLabel, neutLabel, "Created on " + dateString,newAAT.getTestConfiguration().getColoredBorders()); //Add the current test_id and used image files to the data.xml file.
            }
        } else {
            createXMLDOC();
            System.out.println("New data file created, adding test data");
            addTestMetaData(newAAT.getTestConfiguration().getTestID(),trials, newAAT.centerPos(), newAAT.getTestConfiguration().getPullTag(), newAAT.getTestConfiguration().getPushTag(), affLabel, neutLabel, "Created on " + dateString,newAAT.getTestConfiguration().getColoredBorders());
        }
      //  model.setDataLoaded();
    }

    /**
     * This constructor can be used to create an instance of the AATDataRecorder object based on a data xml file. This is different from the other constructor
     * which uses the data belonging to a current loaded AAT.
     * @param dataFile
     * @param model
     */
    public AATDataRecorder(File dataFile, AATModel model) {
        externalData = true;
        this.dataFile = dataFile;
        this.model = model;
        if (dataFile.exists()) {
            loadFileData();
            checkAndFixOldData(doc);
        }
    }

    /**
     * Return the metadata belonging to a certain test id value.
     *
     * @param test_id
     * @return
     */
    public TestMetaData getTestMetaData(int test_id) {
        System.out.println("Fetching metadata for id " + test_id);
        String labelA, labelN, pullTag, pushTag;
        int centerPos, trials;
        TestMetaData metaData = null;
        NodeList testList = doc.getElementsByTagName("test");
        boolean borders = true;
        if (testList.getLength() > 0) {
            for (int x = 0; x < testList.getLength(); x++) {
                Element test = (Element) testList.item(x);
                int value = Integer.parseInt(test.getAttribute("test_id"));
                if (value == test_id) {             //Test id has been used before, so no need for adding the images to the data file.
                    labelA = test.getAttribute("labelA");
                    labelN = test.getAttribute("labelN");
                    pushTag = test.getAttribute("pushTag");
                    pullTag = test.getAttribute("pullTag");
                    centerPos = Integer.parseInt(test.getAttribute("centerPos"));
                    trials = Integer.parseInt(test.getAttribute("trials"));
                    borders = Boolean.parseBoolean(test.getAttribute("generatedBorders"));

                    metaData = new TestMetaData(test_id, trials, doc, labelA, labelN, pushTag, pullTag, centerPos,borders);

                }
            }
        }
        ArrayList<File> affectiveImages = collectAllImages(doc, AATImage.AFFECTIVE, test_id);
        ArrayList<File> neutralImages = collectAllImages(doc, AATImage.NEUTRAL, test_id);
        ArrayList<File> practiceImages = collectAllImages(doc, AATImage.PRACTICE, test_id);
        metaData.setAffectiveImages(affectiveImages);
        metaData.setNeutralImages(neutralImages);
        metaData.setPracticeImages(practiceImages);
        return metaData;
    }

    /**
     * @param test_id the current test_id
     * @return whether this test id is added to the datafile.
     */
    private boolean hasTestID(int test_id) {

        NodeList testList = doc.getElementsByTagName("test");
        if (testList.getLength() > 0) {
            for (int x = 0; x < testList.getLength(); x++) {
                Element test = (Element) testList.item(x);
                int value = Integer.parseInt(test.getAttribute("test_id"));
                if (value == test_id) {             //Test id has been used before, so no need for adding the images to the data file.
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the highest available test_id value
     * @return
     */
    public int getHighestTestID() {
        int id = -1;
        NodeList testList = doc.getElementsByTagName("test");
        if (testList.getLength() > 0) {
            for (int x = 0; x < testList.getLength(); x++) {
                Element test = (Element) testList.item(x);
                int value = Integer.parseInt(test.getAttribute("test_id"));
                if (value > id) {             //Test id has been used before, so no need for adding the images to the data file.
                    id = value;
                }
            }
        }
        return id;

    }


    /**
     * Add meta data to the test data.xml file. This makes the stored test data independent of the test configuration.
     *
     * @param test_id
     * @param trials
     * @param pullTag
     * @param pushTag
     * @param affLabel
     * @param neutLabel
     * @param comment
     */
    private void addTestMetaData(int test_id, int trials, int centerPos, String pullTag, String pushTag, String affLabel, String neutLabel, String comment, boolean borders) {
        Element root = doc.getDocumentElement();

        System.out.println("Adding " + trials + " trials");
        Element test = doc.createElement("test");
        root.appendChild(test);
        test.setAttribute("test_id", String.valueOf(test_id));
        test.setAttribute("trials", String.valueOf(trials));
        test.setAttribute("pullTag", pullTag);
        test.setAttribute("pushTag", pushTag);
        test.setAttribute("labelA", affLabel);
        test.setAttribute("labelN", neutLabel);
        test.setAttribute("centerPos", String.valueOf(centerPos));
        test.setAttribute("generatedBorders",String.valueOf(borders));
        Element commentElement = doc.createElement("comment");
        Text commentValue = doc.createTextNode(comment);
        commentElement.appendChild(commentValue);
        test.appendChild(commentElement);

        //Adding the affective images
        addImageToData(test, newAAT.getAffectiveImages(), AATImage.AFFECTIVE);
        addImageToData(test, newAAT.getNeutralImages(), AATImage.NEUTRAL);

        if (newAAT.getPracticeImages().size() > 0) {
            addImageToData(test, newAAT.getPracticeImages(), AATImage.PRACTICE);
        } else if (newAAT.getTestConfiguration().getHasPractice()) {      //Add the practice images.
            ArrayList<File> practiceImages = new ArrayList<File>();
            for (int x = 0; x < newAAT.getTestConfiguration().getPracticeRepeat() * 2; x++) {
                //Create a list with the used practice images.
                String image = "practice_" + x;
                practiceImages.add(new File(image));
            }
            addImageToData(test, practiceImages, AATImage.PRACTICE);
        }
        FileUtils.writeDataToFile(dataFile, doc);

    }

    //Add the used images to the data file.
    private void addImageToData(Element testElement, ArrayList<File> images, int type) {
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
     * Load a data file and parse the xml to a Document
     */
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


    /**
     * Find the highest participant ID
     *
     * @return
     */
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

    /**
     * Continue the data upgrade process when the correct labels are identified.
     *
     * @param labelA
     * @param labelN
     */
    public void continueUpgrade(String labelA, String labelN, String pullTag, String pushTag) {
        this.affLabelOldData = labelA;
        this.neutLabelOldData = labelN;
        this.pullTagOldData = pullTag;
        this.pushTagOldData = pushTag;
        FixOldData();
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
     * the answers to the Questionnaire and all the measurements
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
                String dirValue = newAAT.getTestConfiguration().getPullTag();
                if (imageData.getDirection() == AATImage.PUSH) {
                    dirValue = newAAT.getTestConfiguration().getPushTag();
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
    private void checkAndFixOldData(Document doc) {
        System.out.println("Checking whether this test has data from older versions and upgrade the data file when necessary.");
        Boolean upgrade = false;

        NodeList participantsList = doc.getElementsByTagName("participant");
        for (int x = 0; x < participantsList.getLength(); x++) {
            Element participant = (Element) participantsList.item(x);
            if (!participant.hasAttribute("test_id")) {
           //     System.out.println("Old version detected, first add test id 0 to the participants.");
                upgrade = true;
                participant.setAttribute("test_id", "-1");
            }
        }
        if (upgrade) {
            LabelInputFrame labelFrame = new LabelInputFrame(getLabels(doc),getDirLabels(), this);
            labelFrame.display();
        }
        else if(externalData) {
            model.setExportDocument(doc);
            model.setExportAATDataRecorder(this);
            model.setExport_idNoNotify(getHighestTestID());
            model.setDataLoadedExport();
        }

    }

    /**
     * Find the labels used for the pull and push images. Collects the tags used from the data file and adds them to an array.
     * Assumes that the push reaction is faster than the pull reaction.
     * @return
     */
    private String[] getDirLabels() {
       String[] result = new String[2];
        HashMap<String, imageObject> tagsMap = findTags(doc);
        //First find the two most used tags.
        imageObject largest = new imageObject();
        imageObject second = new imageObject();
        int count = 0;
        for (String tag : tagsMap.keySet()) {
            if (tagsMap.get(tag).count > count) {
                largest = tagsMap.get(tag);
                count = largest.count;
            }
        }
        tagsMap.remove(largest.tag);
        System.out.println("Largest is set to " + largest.tag);
        double largestAvg = (double) largest.time / (double) largest.count;

        count = 0;
        for (String tag : tagsMap.keySet()) {
            if (tagsMap.get(tag).count > count) {
                second = tagsMap.get(tag);
                count = largest.count;
            }
        }
        System.out.println("Second is set to " + second.tag);
        double secondAvg = (double) second.time / (double) second.count;

        System.out.println("Largest " + largestAvg + " Second " + secondAvg);
        String pushTag = largest.tag;
        String pullTag = second.tag;
        if (largestAvg > secondAvg) {
            pushTag = second.tag;
            pullTag = largest.tag;
        }
        result[0] = pushTag;
        result[1] = pullTag;
        return result;
    }

    /**
     * Upgrade the data file to the new structure. This means finding and adding metadata to the test.
     */
    private void FixOldData() {
        NodeList participantsList = doc.getElementsByTagName("participant");
        System.out.println("Pull tag set to: " + pullTagOldData + " Push tag set to: " + pushTagOldData);
        addUpgradeTestMetaData(doc, -1, getCenterPos(doc), pullTagOldData, pushTagOldData, getNoTrials(doc, -1), "Backed up data containing " + participantsList.getLength() + " participants.",oldTestHasColoredBorders());
       if(externalData) {        //When this class is used for loading a separate data file, then notify the observers that the upgrade is ready.
        model.setDataLoadedExport();
       }
    }


    /**
     * Check whether the old test uses auto-generated colored borders or a different type of pull and push stimulus
     * @return true for auto generated colored borders in the old test data.
     */
    private boolean oldTestHasColoredBorders() {
        NodeList participants = doc.getElementsByTagName("participant");
        int pullCount = 0;
        int pushCount = 0;

        for (int y = 0; y < participants.getLength(); y++) {
            Element participant = (Element) participants.item(y);
            String test_id = participant.getAttribute("test_id");
            if (test_id.equalsIgnoreCase("-1")) {       //Find the labels used by the backed up data.
                NodeList imageList = participant.getElementsByTagName("image");

                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    NodeList imgNameList = image.getElementsByTagName("imageName");
                    Node imgNameNode = imgNameList.item(0).getFirstChild();
                    String imgName = imgNameNode.getNodeValue();
                    if(imgName.contains(pullTagOldData)) {
                        pushCount++;
                    }
                    if(imgName.contains(pushTagOldData)) {
                        pullCount++;
                    }

                }
            }
           if(pushCount >0 && pullCount >0) {      //both tags are found in the image file names, so assuming colored borders is not used
                return false;
           }
        }

            return true;        //When direction tags are not found, the test must have self generated colored borders.
    }
    /**
     * Find the joysticks center position.
     * @param doc
     * @return
     */
    private int getCenterPos(Document doc) {
        int centerPos = 5;
        NodeList participants = doc.getElementsByTagName("participant");
        int sum = 0;
        int count = 0;

        for (int y = 0; y < participants.getLength(); y++) {
            Element participant = (Element) participants.item(y);
            String test_id = participant.getAttribute("test_id");
            if (test_id.equalsIgnoreCase("-1")) {       //Find the labels used by the backed up data.
                NodeList imageList = participant.getElementsByTagName("image");

                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    NodeList posList = image.getElementsByTagName("firstPos");
                    Node posNode = posList.item(0).getFirstChild();
                    String pos = posNode.getNodeValue();
                    System.out.println("Pos " + pos + " sum " + sum);
                    sum += Integer.parseInt(pos);
                    count++;
                }
            }
        }
        float f = (float) sum / (float) count;
        centerPos = (int) Math.ceil(f);
        if (centerPos % 2 == 0) {      //Round to the nearest odd integer. Center pos is always an odd number.
            centerPos = (int) Math.floor(f);
        }
        System.out.println("Sum " + sum + " Count " + count + " avg " + f + " center " + centerPos);
        //  centerPos = Math.round(f);
        return centerPos;
    }

    /**
     * Find the labels in the old data and returns the two most used ones.
     *
     * @param doc
     * @return
     */
    private String[] getLabels(Document doc) {
        HashMap<String, Integer> result;
        result = new HashMap<String, Integer>();
        String[] labelArray = new String[2];
        NodeList participants = doc.getElementsByTagName("participant");

        for (int y = 0; y < participants.getLength(); y++) {
            Element participant = (Element) participants.item(y);
            String test_id = participant.getAttribute("test_id");
            if (test_id.equalsIgnoreCase("-1")) {       //Find the labels used by the backed up data.
                NodeList imageList = participant.getElementsByTagName("image");
                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    NodeList labelList = image.getElementsByTagName("type");
                  //  System.out.println("Types " + labelList.getLength());
                    Node labelNode = labelList.item(0).getFirstChild();
                    String label = labelNode.getNodeValue();
                    if (!label.equalsIgnoreCase("practice")) {       //practice is ignored.
                        if (!result.containsKey(label)) {
                            result.put(label, 1);
                        } else {
                            int count = result.get(label);
                            count++;
                            result.remove(label);   //remove label and add it again with the increased counter;
                            result.put(label, count);
                        }
                    }
                }
            }
        }
        if (result.size() > 2) {   //More than the required two labels found. Return the two most used ones.
            String largestLabel = "";
            int largestCount = 0;
            for (String s : result.keySet()) {
                int c = result.get(s);
                if (c > largestCount) {
                    largestLabel = s;
                }
            }
            labelArray[0] = largestLabel;
            result.remove(largestLabel);
            largestCount = 0; //reset this counter;
            for (String s : result.keySet()) {
                int c = result.get(s);
                if (c > largestCount) {
                    largestLabel = s;
                }
            }
            labelArray[1] = largestLabel;


        } else {
            int x = 0;
            for (String s : result.keySet()) {
                labelArray[x] = s;
                x++;
            }
        }
        System.out.println("The labels found are " + labelArray[0] + " and " + labelArray[1]);
        return labelArray;
    }


    private HashMap<String, imageObject> findTags(Document doc) {
        HashMap<String, imageObject> result = new HashMap<String, imageObject>();

        NodeList participants = doc.getElementsByTagName("participant");

        for (int y = 0; y < participants.getLength(); y++) {
            Element participant = (Element) participants.item(y);
            String test_id = participant.getAttribute("test_id");
            if (test_id.equalsIgnoreCase("-1")) {
                NodeList imageList = participant.getElementsByTagName("image");
                System.out.println("No. images " + imageList.getLength());
                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    NodeList directions = image.getElementsByTagName("direction");
                    System.out.println("Directions " + directions.getLength());
                    Node direction = directions.item(0).getFirstChild();
                    String tag = direction.getNodeValue();
                    System.out.println("tag " + tag);
                    NodeList rTimes = image.getElementsByTagName("reactionTime");
                    Node rtime = rTimes.item(0).getFirstChild();
                    int time = Integer.parseInt(rtime.getNodeValue());
                    System.out.println("tag " + tag + " " + time);
                    if (!result.containsKey(tag)) {
                        result.put(tag, new imageObject(tag, time, 1));
                    } else {
                        result.get(tag).increaseCount();
                        result.get(tag).addReactionTime(time);
                    }
                }

            }
        }
        return result;
    }

    private class imageObject {

        public imageObject() {
        }

        public imageObject(String tag, int time, int count) {
            this.tag = tag;
            this.time = time;
            this.count = count;
        }

        public void increaseCount() {
            count++;
        }

        public void addReactionTime(int time) {
            this.time = this.time + time;
        }

        public int time;
        public String tag;
        public int count;
    }

    /**
     * This adds the meta data for the old test data. This is based on the data that was gathered from the old data like the push/pull tags and
     * the category labels. This is necessary to upgrade old data files to the new structure. Mainly to rescue tests that have corrupt data and to save
     * already gathered data.
     *
     * @param doc
     * @param test_id
     * @param pullTag
     * @param pushTag
     * @param trials
     * @param comment
     */
    private void addUpgradeTestMetaData(Document doc, int test_id, int centerPos, String pullTag, String pushTag, int trials, String comment, boolean borders) {
        System.out.println("Add required test data.");
        Element root = doc.getDocumentElement();
        Element test = doc.createElement("test");
        root.appendChild(test);
        test.setAttribute("test_id", String.valueOf(test_id));
        test.setAttribute("trials", String.valueOf(trials));
        test.setAttribute("pullTag", pullTag);
        test.setAttribute("pushTag", pushTag);
        test.setAttribute("labelA", affLabelOldData);
        test.setAttribute("labelN", neutLabelOldData);
        test.setAttribute("centerPos", String.valueOf(centerPos));
        test.setAttribute("generatedBorders",String.valueOf(borders));

        Element commentElement = doc.createElement("comment");
        Text commentValue = doc.createTextNode(comment);
        commentElement.appendChild(commentValue);
        test.appendChild(commentElement);

        //Adding the affective images
        addImageToData(doc, test, collectAllImages(doc, AATImage.AFFECTIVE, -1), AATImage.AFFECTIVE);
        addImageToData(doc, test, collectAllImages(doc, AATImage.NEUTRAL, -1), AATImage.NEUTRAL);
        ArrayList<File> practiceList = collectAllImages(doc, AATImage.PRACTICE, -1);
        if (practiceList.size() > 0) {
            addImageToData(doc, test, practiceList, AATImage.PRACTICE);
        }
        //    if(AAT.)
        System.out.println("Writing the changes to disk");
        FileUtils.writeDataToFile(dataFile, doc);

    }

    //Add the used images to the data file.
    private void addImageToData(Document doc, Element testElement, ArrayList<File> images, int type) {
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
    private ArrayList<File> collectAllImages(Document doc, int type, int test_id) {
        String imgType = "";
        ArrayList<File> result = new ArrayList<File>();
        ArrayList<String> unique = new ArrayList<String>();
        if (type == AATImage.AFFECTIVE) {
            System.out.println("Upgrade: Collecting affective images");
            imgType = affLabelOldData;
        } else if (type == AATImage.NEUTRAL) {
            System.out.println("Upgrade: Collecting neutral images");
            imgType = neutLabelOldData;
        } else if (type == AATImage.PRACTICE) {
            System.out.println("Upgrade: Collecting practice images");
            imgType = "practice";
        }

        NodeList participantsList = doc.getElementsByTagName("participant");

        for (int x = 0; x < participantsList.getLength(); x++) {
            Element participant = (Element) participantsList.item(x);
            if (participant.getAttribute("test_id").equalsIgnoreCase((String.valueOf(test_id)))) {
            //    System.out.println("Collecting images for participant " + participant.getAttribute("id"));
                NodeList imageList = participant.getElementsByTagName("image");
            //    System.out.println("Found " + imageList.getLength() + " " + "images");
                for (int i = 0; i < imageList.getLength(); i++) {
                    Element image = (Element) imageList.item(i);
                    NodeList nameList = image.getElementsByTagName("imageName");
                    Node imageNameNode = nameList.item(0).getFirstChild();
                    String imageName = imageNameNode.getNodeValue();

                    NodeList typeList = image.getElementsByTagName("type");
                    Node typeNode = typeList.item(0).getFirstChild();
                    String typeValue = typeNode.getNodeValue();
                //    System.out.println("Found image " + imageName + " " + typeValue);
                    if (typeValue.equalsIgnoreCase(imgType)) {
                        if (!unique.contains(imageName)) {
                            result.add(new File(imageName));
                            unique.add(imageName);
                      //      System.out.println("Upgrading datafile: adding " + imageName);
                        }
                    }


                }
                //TODO ook practice.
            }

        }
        return result;
    }

    private int getNoTrials(Document doc, int test_id) {
        int max = 0;
        NodeList participantsList = doc.getElementsByTagName("participant");

        for (int x = 0; x < participantsList.getLength(); x++) {
            Element participant = (Element) participantsList.item(x);
            if (participant.getAttribute("test_id").equalsIgnoreCase((String.valueOf(test_id)))) {
                NodeList trialList = participant.getElementsByTagName("trial");
                if (trialList.getLength() > max) {
                    max = trialList.getLength();
                }
            }
        }
        ArrayList<File> practiceList = collectAllImages(doc, AATImage.PRACTICE, -1);
        if(practiceList.size()>0) {
            max = max -1; //Remove 1 trial because the first was a practice trial.
        }
        return max;
    }

    public Document getDocument() {
        return doc;
    }
}


