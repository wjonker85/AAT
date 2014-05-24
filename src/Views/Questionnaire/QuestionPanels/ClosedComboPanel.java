package Views.Questionnaire.QuestionPanels;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcel on 3/25/14.
 */
public class ClosedComboPanel extends AbstractQuestionPanel {

    JComboBox answerOptions;

    public ClosedComboPanel(Object[] options, boolean isRequired) {
        super();
        this.isRequired = isRequired;
        Object[] newOptions = new Object[options.length + 1];
        newOptions[0] = "";
        for (int x = 0; x < options.length; x++) {
            newOptions[x + 1] = options[x];
        }
        answerOptions = new JComboBox(newOptions);
        // JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(answerOptions);
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        answerOptions.setBackground(Color.WHITE);
        answerOptions.setForeground(Color.BLACK);
        if (isRequired) {
            add(asterisks);
        }
    }

    @Override
    public String getValue() {
        for (int x = 1; x < answerOptions.getItemCount(); x++) {
            Object option = answerOptions.getItemAt(x);
            if (option.equals(answerOptions.getSelectedItem())) {
                return String.valueOf(x);
            }
        }
        return "";
    }

    @Override
    public String getType() {
        return "closed";
    }


}
