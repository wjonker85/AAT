package views.Questionnaire.QuestionPanels;

import AAT.Util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * Created by marcel on 3/25/14.
 */

public class ClosedButtonsPanel extends AbstractQuestionPanel {

    private ButtonGroup closedButtons;

    public ClosedButtonsPanel(Object[] options, boolean isRequired) {
        super();
        this.isRequired = isRequired;
        closedButtons = new ButtonGroup();
        JPanel answerPanel = new JPanel(new SpringLayout());
        answerPanel.setBackground(Color.black);
        answerPanel.setForeground(Color.white);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.black);
        setForeground(Color.white);
        for (int x = 0; x < options.length; x++) {
            JRadioButton button = new JRadioButton();
            button.setBackground(Color.black);
            button.setForeground(Color.white);
            closedButtons.add(button);
            JLabel answerOption = new JLabel(options[x].toString(), JLabel.LEFT);
            answerOption.setBackground(Color.black);
            answerOption.setForeground(Color.white);
            answerOption.setLabelFor(button);
            answerPanel.add(button);
            answerPanel.add(answerOption);
        }
        this.add(answerPanel);
        SpringUtilities.makeCompactGrid(answerPanel,
                options.length, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        if (isRequired) {
            add(asterisks);
        }
    }

    @Override
    public String getValue() {
        int x = 1;
        for (Enumeration e = closedButtons.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if (b.isSelected()) {
                return String.valueOf(x);
            }
            x++;

        }
        return "";
    }

    @Override
    public String getType() {
        return "closed_button";
    }

    @Override
    public void changeLabelSize(int width) {

    }
}
