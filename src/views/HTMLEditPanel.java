package views;

import net.atlanticbb.tantlinger.shef.HTMLEditorPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;

/**
 * Created by marcel on 2/16/14.
 */
public class HTMLEditPanel extends JPanel {

    private HTMLEditorPane editorStart, editorIntro, editorBreak, editorFinish;
    private File fileName = new File("");

    public HTMLEditPanel() {

        //  JPanel content = new JPanel();
        // content.setPreferredSize(new Dimension(600,600));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        editorStart = new HTMLEditorPane();
        editorStart.setPreferredSize(new Dimension(800, 400));
        editorStart.setMaximumSize(new Dimension(800, 400));
        editorStart.setBackground(Color.decode("#eeece9"));
        editorIntro = new HTMLEditorPane();
        editorIntro.setPreferredSize(new Dimension(800, 400));
        editorIntro.setMaximumSize(new Dimension(800, 400));
        editorIntro.setBackground(Color.decode("#eeece9"));
        editorBreak = new HTMLEditorPane();
        editorBreak.setPreferredSize(new Dimension(800, 400));
        editorBreak.setMaximumSize(new Dimension(800, 400));
        editorBreak.setBackground(Color.decode("#eeece9"));
        editorFinish = new HTMLEditorPane();
        editorFinish.setPreferredSize(new Dimension(800, 400));
        editorFinish.setMaximumSize(new Dimension(800, 400));
        editorFinish.setBackground(Color.decode("#eeece9"));
        //    JLabel intro = new JLabel("<html><br><center>Introduction Text (Shown before practice, only when practice is enabled).</center><br></html>");
        //    intro.setLayout(new BorderLayout());
        JLabel intro = new JLabel("Introduction Text (Shown before practice, only when practice is enabled).");
        intro.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font f = intro.getFont();
        intro.setFont(new Font(f.getName(), Font.PLAIN, 18));
        this.add(Box.createVerticalStrut(20));
        this.add(intro, LEFT_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(editorIntro);
        this.add(Box.createVerticalStrut(20));
        JLabel start = new JLabel("Start Text (Shown after practice and before the real test starts).");
        Font f2 = start.getFont();
        start.setFont(new Font(f2.getName(), Font.PLAIN, 18));
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(start);
        this.add(Box.createVerticalStrut(20));
        this.add(editorStart);
        JLabel breakL = new JLabel("Break text (Only showed when a break is configured).");
        Font f3 = breakL.getFont();
        breakL.setFont(new Font(f3.getName(), Font.PLAIN, 18));
        breakL.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(breakL);
        this.add(Box.createVerticalStrut(20));
        this.add(editorBreak);
        JLabel finish = new JLabel("Break text (Only showed when a break is configured).");
        Font f4 = finish.getFont();
        finish.setFont(new Font(f4.getName(), Font.PLAIN, 18));
        finish.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createVerticalStrut(20));
        this.add(finish);
        this.add(Box.createVerticalStrut(20));
        this.add(editorFinish);
        editorIntro.setEnabled(false);
        editorBreak.setEnabled(false);
        editorStart.setEnabled(false);
        editorFinish.setEnabled(false);
        //  this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //   this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        //      System.out.println(content.getWidth());

        //   this.add(content);


    }

    private void setEditorIntroText(String text) {
        editorIntro.setText(text);
        editorIntro.setEnabled(true);
        repaint();

    }

    private void setEditorStartText(String text) {
        editorStart.setText(text);
        editorStart.setEnabled(true);
        repaint();

    }

    private void setEditorBreakText(String text) {
        editorBreak.setText(text);
        editorBreak.setEnabled(true);
        repaint();

    }

    private void setEditorFinishText(String text) {
        editorFinish.setText(text);
        editorFinish.setEnabled(true);
        repaint();

    }

    public void disableAll() {
        editorIntro.setEnabled(false);
        editorBreak.setEnabled(false);
        editorStart.setEnabled(false);
        editorFinish.setEnabled(false);
        repaint();
    }


    public boolean setDocument(File file) {
        this.fileName = file;
        String intro, start, breakT, finish;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("languageFile");
            for (int x = 0; x < nList.getLength(); x++) {
                //      introduction
                //            start
                //          break
                //                finished
                Element lang = (Element) nList.item(x);
                NodeList introL = lang.getElementsByTagName("introduction");
                NodeList startL = lang.getElementsByTagName("start");
                NodeList breakL = lang.getElementsByTagName("break");
                NodeList finishL = lang.getElementsByTagName("finished");
                Node introN = introL.item(0).getChildNodes().item(0);
                intro = introN.getNodeValue();
                intro = intro.replaceAll("(\r\n|\n)", "<br />");
                this.setEditorIntroText(intro);
                Node startN = startL.item(0).getChildNodes().item(0);
                start = startN.getNodeValue();
                start = start.replaceAll("(\r\n|\n)", "<br />");
                this.setEditorStartText(start);
                Node breakN = breakL.item(0).getChildNodes().item(0);
                breakT = breakN.getNodeValue();
                breakT = breakT.replaceAll("(\r\n|\n)", "<br />");
                this.setEditorBreakText(breakT);
                Node finishN = finishL.item(0).getChildNodes().item(0);
                finish = finishN.getNodeValue();
                finish = finish.replaceAll("(\r\n|\n)", "<br />");
                this.setEditorFinishText(finish);


            }


            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void save() {

        writeToFile();

    }

    private void writeToFile() {

        try {
            //   TableModel modelA = tableA.getModel();
            //  TableModel modelN = tableN.getModel();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("languageFile");
            doc.appendChild(rootElement);

            Element intro = doc.createElement("introduction");
            rootElement.appendChild(intro);
            intro.appendChild(doc.createCDATASection(editorIntro.getText()));
            Element start = doc.createElement("start");
            rootElement.appendChild(start);
            start.appendChild(doc.createCDATASection(editorStart.getText()));
            Element breakE = doc.createElement("break");
            rootElement.appendChild(breakE);
            breakE.appendChild(doc.createCDATASection(editorBreak.getText()));
            Element finished = doc.createElement("finished");
            rootElement.appendChild(finished);
            finished.appendChild(doc.createCDATASection(editorFinish.getText()));

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fileName);

            transformer.transform(source, result);
            System.out.println("Saved to " + fileName.getAbsoluteFile());
            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);


        } catch (Exception e) {

        }

    }
}

