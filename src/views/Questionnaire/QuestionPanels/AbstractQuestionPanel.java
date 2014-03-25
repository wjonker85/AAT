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
    private int maxLabelSize = 0;
    public JLabel leftLabel;

    public AbstractQuestionPanel() {
        leftLabel = new JLabel("");
        asterisks = new JLabel("*", JLabel.TRAILING);
        asterisks.setFont(new Font("Roman", Font.BOLD, 20));
        asterisks.setForeground(Color.WHITE);
    }

    public abstract String getValue();

    public abstract String getType();

    public void changeLabelSize(int width) {
        leftLabel.setPreferredSize(new Dimension(width, 20));
        repaint();
    }

    public void calculateLabelWidth(JLabel label) {
        Font labelFont = label.getFont();
        String labelText = label.getText();

        int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
        if (stringWidth > maxLabelSize) {
            maxLabelSize = stringWidth;
        }
    }

    public void setRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public void changeAsteriskColor(Color c) {
        asterisks.setForeground(c);
    }
}








