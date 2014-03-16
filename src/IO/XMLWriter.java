package IO;

import AAT.Util.FileUtils;
import DataStructures.QuestionData;
import DataStructures.Questionnaire;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by marcel on 1/19/14.
 */
public class XMLWriter {


    public static void writeXMLImagesList(TableModel modelA, TableModel modelN, TableModel modelP, File aDir, File nDir, File pDir) {
        try {
            //   TableModel modelA = tableA.getModel();
            //  TableModel modelN = tableN.getModel();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);


            for (int x = 0; x < modelA.getRowCount(); x++) {   //Retrieve the data from the table and add the checked images to the included xml file.

                String file = modelA.getValueAt(x, 0).toString();
                Boolean add = Boolean.parseBoolean(modelA.getValueAt(x, 1).toString());
                if (add) {
                    Element image = doc.createElement("image");
                    rootElement.appendChild(image);
                    Attr attr = doc.createAttribute("file");
                    attr.setValue(file);
                    image.setAttributeNode(attr);
                }
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(aDir.getAbsolutePath() + File.separator + "included.xml"));

            transformer.transform(source, result);

            // root elements
            doc = docBuilder.newDocument();
            rootElement = doc.createElement("root");
            doc.appendChild(rootElement);


            for (int x = 0; x < modelN.getRowCount(); x++) {   //Retrieve the data from the table and add the checked images to the included xml file.

                String file = modelN.getValueAt(x, 0).toString();
                Boolean add = Boolean.parseBoolean(modelN.getValueAt(x, 1).toString());
                if (add) {
                    Element image = doc.createElement("image");
                    rootElement.appendChild(image);
                    Attr attr = doc.createAttribute("file");
                    attr.setValue(file);
                    image.setAttributeNode(attr);
                }
            }

            // write the content into xml file
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            source = new DOMSource(doc);
            result = new StreamResult(new File(nDir.getAbsolutePath() + File.separator + "included.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            // root elements
            if (modelP.getRowCount() > 0) {
                doc = docBuilder.newDocument();
                rootElement = doc.createElement("root");
                doc.appendChild(rootElement);

                for (int x = 0; x < modelP.getRowCount(); x++) {   //Retrieve the data from the table and add the checked images to the included xml file.

                    String file = modelP.getValueAt(x, 0).toString();
                    Boolean add = Boolean.parseBoolean(modelP.getValueAt(x, 1).toString());
                    if (add) {
                        System.out.println("Adding practice image " + file);
                        Element image = doc.createElement("image");
                        rootElement.appendChild(image);
                        Attr attr = doc.createAttribute("file");
                        attr.setValue(file);
                        image.setAttributeNode(attr);
                    }
                }

                // write the content into xml file
                transformerFactory = TransformerFactory.newInstance();
                transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                source = new DOMSource(doc);
                result = new StreamResult(new File(pDir.getAbsolutePath() + File.separator + "included.xml"));

                // Output to console for testing
                // StreamResult result = new StreamResult(System.out);

                transformer.transform(source, result);
            }

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }


    public static void writeXMLQuestionnaire(File file, Questionnaire questionnaire) {
        System.out.println("test");
        try {
            //   TableModel modelA = tableA.getModel();
            //  TableModel modelN = tableN.getModel();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Questionnaire");
            doc.appendChild(rootElement);

            //Add the introduction.
            Element introduction = doc.createElement("introduction");
            Text introductionStr = doc.createTextNode(questionnaire.getIntroduction());
            introduction.appendChild(introductionStr);
            rootElement.appendChild(introduction);

            for (QuestionData question : questionnaire.getExtraQuestions()) {
                Element questionNode = doc.createElement("question");
                questionNode.setAttribute("type",question.getType());
                questionNode.setAttribute(("required"),String.valueOf(question.isRequired()));
                rootElement.appendChild(questionNode);
                Element textNode = doc.createElement("text");
                Text textNodeStr = doc.createTextNode(question.getQuestion());
                textNode.appendChild(textNodeStr);
                questionNode.appendChild(textNode);

                if (question.getType().equalsIgnoreCase("likert")) {
                    System.out.println("likert");
                    Element sizeNode = doc.createElement("size");
                    Text sizeNodeTxt = doc.createTextNode(String.valueOf(question.getSize()));
                    sizeNode.appendChild(sizeNodeTxt);
                    questionNode.appendChild(sizeNode);

                    Element leftNode = doc.createElement("left");
                    Text leftNodeTxt = doc.createTextNode(question.getLeftText());
                    leftNode.appendChild(leftNodeTxt);
                    questionNode.appendChild(leftNode);

                    Element rightNode = doc.createElement("right");
                    Text rightNodeTxt = doc.createTextNode(question.getRightText());
                    rightNode.appendChild(rightNodeTxt);
                    questionNode.appendChild(rightNode);
                } else if (question.getType().equalsIgnoreCase("closed_combo")) {
                    for(String option : question.getOptions()) {
                        Element optionNode = doc.createElement("option");
                        Text optionNodeTxt = doc.createTextNode(option);
                        optionNode.appendChild(optionNodeTxt);
                        questionNode.appendChild(optionNode);
                    }
                } else if (question.getType().equalsIgnoreCase("open")) {
                    System.out.println("open");

                } else if (question.getType().equalsIgnoreCase("closed_button")) {
                    System.out.println("closed_button");
                    for(String option : question.getOptions()) {
                        Element optionNode = doc.createElement("option");
                        Text optionNodeTxt = doc.createTextNode(option);
                        optionNode.appendChild(optionNodeTxt);
                    }

                } else if (question.getType().equalsIgnoreCase("textArea")) {
                    System.out.println("textArea");

                } else if (question.getType().equalsIgnoreCase("sem_diff")) {
                    System.out.println("sem_diff");
                    Element sizeNode = doc.createElement("size");
                    Text sizeNodeTxt = doc.createTextNode(String.valueOf(question.getSize()));
                    sizeNode.appendChild(sizeNodeTxt);
                    questionNode.appendChild(sizeNode);

                    Element leftNode = doc.createElement("left");
                    Text leftNodeTxt = doc.createTextNode(question.getLeftText());
                    leftNode.appendChild(leftNodeTxt);
                    questionNode.appendChild(leftNode);

                    Element rightNode = doc.createElement("right");
                    Text rightNodeTxt = doc.createTextNode(question.getRightText());
                    rightNode.appendChild(rightNodeTxt);
                    questionNode.appendChild(rightNode);
                }

                Element keyNode = doc.createElement("key");
                Text keyNodeStr = doc.createTextNode(question.getKey());
                keyNode.appendChild(keyNodeStr);
                questionNode.appendChild(keyNode);
            }
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    /**
     * Read all images available in the directory and add them to the included.xml files.
     * This was done so older tests which didn't have the included.xml files are made compatible with the
     * new structure. This reads all the files present and adds them to the included.xml files. This way adding or removing images
     * to the directories doesn't corrupt the test data.
     */
    public static void writeXMLImagesList(File dir) {
        try {
            //   TableModel modelA = tableA.getModel();
            //  TableModel modelN = tableN.getModel();
            System.out.println("Writing included.xml file " + dir.getAbsoluteFile());
            ArrayList<File> images = FileUtils.getImages(dir);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements. Affective images
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            for (File file : images) {
                System.out.println("Adding file: " + file.getName());
                Element image = doc.createElement("image");
                rootElement.appendChild(image);
                Attr attr = doc.createAttribute("file");
                attr.setValue(file.getName());
                image.setAttributeNode(attr);
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(dir.getAbsolutePath() + File.separator + "included.xml"));
            transformer.transform(source, result);


            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }


}
