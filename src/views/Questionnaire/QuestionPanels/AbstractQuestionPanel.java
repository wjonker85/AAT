package views.Questionnaire.QuestionPanels;

import AAT.Util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * Created by marcel on 3/16/14.
 * This class contains the functionality to display the different types of questions in the Questionnaire.
 */
public abstract class AbstractQuestionPanel extends JPanel {

    public JLabel asterisks;
    public boolean isRequired = true;

    public AbstractQuestionPanel() {
        asterisks = new JLabel("*", JLabel.TRAILING);
        asterisks.setFont(new Font("Roman", Font.BOLD, 20));
        asterisks.setForeground(Color.WHITE);
        setOpaque(false);
    }

    public abstract String getValue();

    public abstract String getType();

    public void setRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public void changeAsteriskColor(Color c) {
        asterisks.setForeground(c);
    }
}








