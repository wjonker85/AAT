package Views.Components;

import AAT.Configuration.LanguageFileTemplate;
import IO.XMLWriter;
import net.atlanticbb.tantlinger.shef.HTMLEditorPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;

/**
 * Created by marcel on 2/16/14.
 * This is panel belonging to the configuration builder that shows server html editor components. These components can be used to change all the
 * texts that are shown to a participant during the taking of an AAT.
 */
public class HTMLEditPanel extends JPanel {

    private HTMLEditorPane editorStart, editorIntro, editorBreak, editorFinish;

    public HTMLEditPanel() {

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

    //Fill the html editors with template texts when a user has selected a new file.
    public void setTemplateText() {
        setEditorStartText(LanguageFileTemplate.getEditorStartText());
        setEditorIntroText(LanguageFileTemplate.getEditorIntroText());
        setEditorFinishText(LanguageFileTemplate.getEditorFinishText());
        setEditorBreakText(LanguageFileTemplate.getEditorBreakText());
    }

    public boolean setDocument(File file) {
        if (!file.exists()) {
            return false;
        }

        String intro, start, breakT, finish;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("languageFile");
            for (int x = 0; x < nList.getLength(); x++) {
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


    public void save(File file) {
        XMLWriter.writeLanguageFile(file, editorIntro.getText(), editorStart.getText(), editorBreak.getText(), editorFinish.getText());
    }
}

