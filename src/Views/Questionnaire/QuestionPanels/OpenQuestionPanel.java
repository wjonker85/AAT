package Views.Questionnaire.QuestionPanels;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcel on 3/25/14.
 */
public class OpenQuestionPanel extends AbstractQuestionPanel {

    JTextField textInput;

    public OpenQuestionPanel(boolean isRequired) {
        super();
        this.isRequired = isRequired;
        textInput = new JTextField(30);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.black);
        setForeground(Color.white);
        add(textInput);
        textInput.setBackground(Color.WHITE);
        textInput.setForeground(Color.BLACK);
        if (isRequired) {
            add(asterisks);
        }
    }

    @Override
    public String getValue() {
        return textInput.getText();
    }

    @Override
    public String getType() {
        return "open";
    }
}
