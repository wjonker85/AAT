package IO;

import AAT.Util.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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


    public static void writeXMLImagesList(TableModel modelA, TableModel modelN, File aDir, File nDir) {
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
            source = new DOMSource(doc);
            result = new StreamResult(new File(nDir.getAbsolutePath() + File.separator + "included.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }


    /**Read all images available in the directory and add them to the included.xml files.
     * This was done so older tests which didn't have the included.xml files are made compatible with the
     * new structure. This reads all the files present and adds them to the included.xml files. This way adding or removing images
     * to the directories doesn't corrupt the test data.
     **/
    public static void writeXMLImagesList(File dir) {
        try {
            //   TableModel modelA = tableA.getModel();
            //  TableModel modelN = tableN.getModel();
            System.out.println("Writing included.xml file "+dir.getAbsoluteFile());
            ArrayList<File> images = FileUtils.getImages(dir);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements. Affective images
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);

            for (File file : images) {
                    System.out.println("Adding file: "+file.getName());
                    Element image = doc.createElement("image");
                    rootElement.appendChild(image);
                    Attr attr = doc.createAttribute("file");
                    attr.setValue(file.getName());
                    image.setAttributeNode(attr);
                }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
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