package Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import AAT.QuestionObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 2/27/12
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLReader {

    private Map<String, String> testText = new HashMap<String, String>();
    private ArrayList<QuestionObject> extraQuestions;
    private Document doc;

    private File languageFile;
    String[] options = {              //Keys defining the different texts
            "introduction",
            "start",
            "break",
            "finished"
    };

    private ArrayList<String> questionKeys;

    public XMLReader(File languageFile) {
        this.languageFile = languageFile;
        extraQuestions = new ArrayList<QuestionObject>();
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
        for (int x = 0; x < options.length; x++) {
            testText.put(options[x], getValue(options[x], doc.getDocumentElement()));
        }
    }

    private void readQuestions() {
        NodeList questions = doc.getElementsByTagName("extra_questions");
        Element allQuestions = (Element) questions.item(0);
        NodeList questionList = allQuestions.getElementsByTagName("question");
        QuestionObject newQuestion = null;

        System.out.println("No questions " + questionList.getLength());
        for (int x = 0; x < questionList.getLength(); x++) {
            Node fstNode = questionList.item(x);

            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) fstNode;
                String type = element.getAttribute("type");
                System.out.println("Type " + type);
                newQuestion = new QuestionObject(type);
                newQuestion.setQuestion(getValue("text", element));
                newQuestion.setKey(getValue("key", element));
                if (type.equals("closed")) {
                    //    Element text = (Element) element.getElementsByTagName("text");
                    System.out.println("Blaa " + getValue("text", element));

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
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }

    //Returns the requested value belonging to a key in the Hashmap
    public String getValue(String key) {
        return testText.get(key);
    }


    public ArrayList<QuestionObject> getExtraQuestions() {
        return extraQuestions;
    }
}
