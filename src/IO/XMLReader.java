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

import DataStructures.Questionnaire.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 2/27/12
 * Time: 7:36 PM
 */
public class XMLReader {


    public static HashMap<String, String> getTranslations(File languageFile) {
        HashMap<String, String> translations = new HashMap<String, String>();
        Document doc = null;
        String[] options = {              //Keys defining the different texts
                "introduction",
                "start",
                "break",
                "finished"
        };
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(languageFile);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String option : options) {
            assert doc != null;
            translations.put(option, getValue(option, doc.getDocumentElement()));
        }
        return translations;
    }

    public static Questionnaire getQuestionnaire(File questionFile) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(questionFile);
            doc.getDocumentElement().normalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Questionnaire(readQuestions(doc), readIntroductionString(doc));
    }

    private static String readIntroductionString(Document doc) {
        NodeList questions = doc.getElementsByTagName("questionnaire");
        Element allQuestions = (Element) questions.item(0);
        NodeList introductionList = allQuestions.getElementsByTagName("introduction");
        Node introductionNode = introductionList.item(0);
        Node introductionText = introductionNode.getFirstChild();
        return introductionText.getNodeValue();
    }


    private static ArrayList<AbstractQuestion> readQuestions(Document doc) {
        ArrayList<AbstractQuestion> allQuestionsList = new ArrayList<AbstractQuestion>();
        Boolean required;
        String type;

        try {
            NodeList questions = doc.getElementsByTagName("questionnaire");
            Element allQuestions = (Element) questions.item(0);
            NodeList questionList = allQuestions.getElementsByTagName("question");


            for (int x = 0; x < questionList.getLength(); x++) {
                AbstractQuestion newQuestion = null;
                boolean elementFound = false;

                Node fstNode = questionList.item(x);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) fstNode;
                    type = element.getAttribute("type");
                    required = element.getAttribute("required").equalsIgnoreCase("true");

                    if (type.equalsIgnoreCase("closed_combo")) {
                        newQuestion = new ClosedComboQuestion();

                    } else if (type.equalsIgnoreCase("closed_button")) {
                        newQuestion = new ClosedButtonQuestion();
                    }
                    if (newQuestion != null) {
                        AbstractClosedQuestion cq = (AbstractClosedQuestion) newQuestion;
                        NodeList optionList = element.getElementsByTagName("option");

                        for (int i = 0; i < optionList.getLength(); i++) {
                            Node oNode = optionList.item(i);
                            if (oNode.getNodeType() == Node.ELEMENT_NODE) {
                                Node option = oNode.getChildNodes().item(0);
                                cq.addOptions(option.getNodeValue());
                            }
                        }
                        newQuestion = cq;
                        elementFound = true;
                    }

                    if (type.equalsIgnoreCase("open")) {
                        newQuestion = new OpenQuestion();
                        elementFound = true;
                    }

                    if (type.equalsIgnoreCase("textArea")) {
                        newQuestion = new OpenQuestion();
                        elementFound = true;
                    }

                    if (type.equalsIgnoreCase("likert"))
                        newQuestion = new LikertQuestion();
                    else if (type.equalsIgnoreCase("sem_diff")) {
                        newQuestion = new SemDiffQuestion();
                    }
                    if (newQuestion != null && !elementFound) {
                        AbstractScaleQuestion sq = (AbstractScaleQuestion) newQuestion;
                        sq.setLeft(getValue("left", element));
                        sq.setRight(getValue("right", element));
                        String size = getValue("size", element);
                        sq.setSize(Integer.parseInt(size));
                        newQuestion = sq;
                    }

                    assert newQuestion != null;
                    newQuestion.setQuestion(getValue("text", element));
                    newQuestion.setKey(getValue("key", element));
                    newQuestion.setRequired(required);
                }
                allQuestionsList.add(newQuestion);
            }
        } catch (Exception e) {
            //custom title, error icon
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Invalid Questionnaire file.",
                    "XML Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return allQuestionsList;
    }


    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodes.item(0);
        return node.getNodeValue();
    }


    public static ArrayList<String> getIncludedFiles(File dir) {
        if (dir != null) {
            ArrayList<String> files = new ArrayList<String>();

            try {
                File xmlFile = new File(dir.getAbsoluteFile() + File.separator + "included.xml");
                if (!xmlFile.exists()) {        //Create the included.xml files when they are not present. Done for backwards compatibility with older versions of the test.
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
                        files.add(file);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return files;
        } else return new ArrayList<String>();
    }

    //Same result as the above, but this time returns as an arraylist of Files
    public static ArrayList<File> getIncludedFilesF(File dir) {

        ArrayList<File> result = new ArrayList<File>();
        for (String s : getIncludedFiles(dir)) {
            String image = dir.getAbsolutePath() + File.separator + s;
            result.add(new File(image));
        }
        return result;
    }
}
