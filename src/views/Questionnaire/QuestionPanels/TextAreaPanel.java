package views.Questionnaire.QuestionPanels;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcel on 3/25/14.
 */
public class TextAreaPanel extends AbstractQuestionPanel {

    JTextArea textArea;

    public TextAreaPanel(boolean isRequired) {
        super();
        this.isRequired = isRequired;
        textArea = new JTextArea(10, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane2 = new JScrollPane(textArea);
        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(scrollPane2);
        setBackground(Color.black);
        setForeground(Color.white);
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.BLACK);
        if (isRequired) {
            add(asterisks);
        }
    }


    @Override
    public String getValue() {
        return textArea.getText();
    }

    @Override
    public String getType() {
        return "textArea";
    }
}
