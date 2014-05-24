package Views.Questionnaire.QuestionPanels;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * Created by marcel on 3/25/14.
 */
public class SemDiffPanel extends AbstractQuestionPanel {

    private ButtonGroup semDiffScale;
    private int size;


    public SemDiffPanel(int size, String left, int leftLabelWidth, String right, boolean isRequired) {
        super();
        this.isRequired = isRequired;
        this.size = size;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, size, 20, 0));
        buttonPanel.setForeground(Color.white);
        buttonPanel.setBackground(Color.black);
        JLabel leftLabel = new JLabel(left, JLabel.RIGHT);
        leftLabel.setForeground(Color.WHITE);
        leftLabel.setPreferredSize(new Dimension(leftLabelWidth, 20));
        leftLabel.setBackground(Color.black);

        this.add(leftLabel);
        this.add(Box.createHorizontalStrut(20));//20px margin
        semDiffScale = new ButtonGroup();

        for (int x = 0; x < size; x++) {
            int step = size / 2;
            JLabel label = new JLabel(String.valueOf(x - step), JLabel.CENTER);
            label.setForeground(Color.white);
            buttonPanel.add(label);

        }

        for (int x = 0; x < size; x++) {
            JRadioButton likertButton = new JRadioButton();
            likertButton.setBackground(Color.black);
            likertButton.setForeground(Color.white);
            buttonPanel.add(likertButton);
            semDiffScale.add(likertButton);
        }


        this.add(buttonPanel);
        this.add(Box.createHorizontalStrut(20));//20px margin
        JLabel rightLabel = new JLabel(right, JLabel.LEFT);
        rightLabel.setForeground(Color.white);
        rightLabel.setBackground(Color.black);
        this.add(rightLabel);

        if (isRequired) {
            add(asterisks);
        }
    }

    public String getValue() {
        int x = 0;
        int step = size / 2;
        for (Enumeration e = semDiffScale.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if (b.isSelected()) {

                return String.valueOf(x - step);
            }
            x++;
        }
        return "";
    }

    @Override
    public String getType() {
        return "sem_diff";
    }
}