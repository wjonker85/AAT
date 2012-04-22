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
    private Document doc;

    String[] options = {              //Keys defining the different texts
            "introduction",
            "start",
            "break",
            "finished"
    };

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
        readQuestions();
    }

    private void readText() {
        for (String option : options) {
            testText.put(option, getValue(option, doc.getDocumentElement()));
        }
    }

    private void readQuestions() {
        NodeList questions = doc.getElementsByTagName("extra_questions");
        Element allQuestions = (Element) questions.item(0);
        NodeList questionList = allQuestions.getElementsByTagName("question");
        QuestionData newQuestion = null;

        System.out.println("No questions " + questionList.getLength());
        for (int x = 0; x < questionList.getLength(); x++) {
            Node fstNode = questionList.item(x);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) fstNode;
                String type = element.getAttribute("type");
                System.out.println("Type " + type);
                newQuestion = new QuestionData(type);
                newQuestion.setQuestion(getValue("text", element));
                newQuestion.setKey(getValue("key", element));
                if (type.equals("closed")) {
                    //    Element text = (Element) element.getElementsByTagName("text");


                    NodeList optionList = element.getElementsByTagName("option");
                    System.out.println("No options " + optionList.getLength());
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
                if (type.equals("likert")) {
                    newQuestion.setLeftText(getValue("left", element));
                    newQuestion.setRightText(getValue("right", element));
                    String size = getValue("size", element);
                    newQuestion.setSize(Integer.parseInt(size));
                    System.out.println("Left text " + newQuestion.getLeftText());
                }

                System.out.println(newQuestion.getKey() + " " + newQuestion.getQuestion());
                //   System.out.println(element.getAttribute("type"));
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


    public ArrayList<QuestionData> getExtraQuestions() {
        return extraQuestions;
    }
}
