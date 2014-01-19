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

import DataStructures.QuestionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 2/27/12
 * Time: 7:36 PM
 */
public class XMLReader {

    private Map<String, String> testText = new HashMap<String, String>();
    private ArrayList<QuestionData> extraQuestions;
    private String introduction = ""; //introduction for the questionnaire;
    private Document doc;
    private Document questionnaire;

    String[] options = {              //Keys defining the different texts
            "introduction",
            "start",
            "break",
            "finished"
    };

    public XMLReader() {
        //just an empty constructor.
    }

    public XMLReader(File languageFile) {

        extraQuestions = new ArrayList<QuestionData>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(languageFile);
            doc.getDocumentElement().normalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
        readText();
    }

    public void addQuestionnaire(File questionFile) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            questionnaire = db.parse(questionFile);
            questionnaire.getDocumentElement().normalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
        readQuestions();
    }

    private void readText() {
        for (String option : options) {
            testText.put(option, getValue(option, doc.getDocumentElement()));
        }
    }


    private void readQuestions() {
        NodeList questions = questionnaire.getElementsByTagName("questionnaire");
        Element allQuestions = (Element) questions.item(0);
        NodeList introductionList = allQuestions.getElementsByTagName("introduction");
        Node introductionNode = introductionList.item(0);
        Node introductionText = introductionNode.getFirstChild();
        introduction = introductionText.getNodeValue();
        NodeList questionList = allQuestions.getElementsByTagName("question");
        QuestionData newQuestion = null;

        for (int x = 0; x < questionList.getLength(); x++) {
            Node fstNode = questionList.item(x);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) fstNode;
                String type = element.getAttribute("type");
                String required = element.getAttribute("required");

                newQuestion = new QuestionData(type);
                if (required.equalsIgnoreCase("false")) {
                    newQuestion.setRequired(false);
                }
                newQuestion.setQuestion(getValue("text", element));
                newQuestion.setKey(getValue("key", element));
                if (type.equals("closed_combo") || type.equals("closed_buttons")) {
                    NodeList optionList = element.getElementsByTagName("option");

                    for (int i = 0; i < optionList.getLength(); i++) {
                        Node oNode = optionList.item(i);
                        if (oNode.getNodeType() == Node.ELEMENT_NODE) {
                            Node option = oNode.getChildNodes().item(0);
                            newQuestion.addOptions(option.getNodeValue());
                        }
                    }
                }
                if (type.equals("open")) {
                    //
                }
                if (type.equals("likert") || type.equals("sem_diff")) {
                    newQuestion.setLeftText(getValue("left", element));
                    newQuestion.setRightText(getValue("right", element));
                    String size = getValue("size", element);
                    newQuestion.setSize(Integer.parseInt(size));
                }
            }
            extraQuestions.add(newQuestion);
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodes.item(0);
        return node.getNodeValue();
    }

    //Returns the requested value belonging to a key in the Hashmap
    public String getValue(String key) {
        return testText.get(key);
    }

    public String getQuestionnaireIntro() {
        return introduction;
    }

    public ArrayList<QuestionData> getExtraQuestions() {
        return extraQuestions;
    }



    public ArrayList<String> getIncludedFiles(File dir) {
        ArrayList<String> files = new ArrayList<String>();

        try {
            File xmlFile = new File(dir.getAbsoluteFile() + File.separator + "included.xml");
            if(!xmlFile.exists()) {        //Create the included.xml files when they are not present. Done for backwards compatibility with older versions of the test.
              System.out.println("Included.xml doesn't exist, creating one.");
                XMLWriter.writeXMLImagesList(dir);
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("image");
            for (int x = 0; x < nList.getLength(); x++) {
                Node fstNode = nList.item(x);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) fstNode;
                    String file = element.getAttribute("file");
                    System.out.println("Included "+file);
                    files.add(file);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return files;
    }

    //Same result as the above, but this time returns as an arraylist of Files
    public ArrayList<File> getIncludedFilesF(File dir) {

        ArrayList<File> result = new ArrayList<File>();
        for(String s : getIncludedFiles(dir)) {
                 String image = dir.getAbsolutePath()+File.separator+s;
                 System.out.println("Added image "+image);
                 result.add(new File(image));
        }
        return result;
    }
}
