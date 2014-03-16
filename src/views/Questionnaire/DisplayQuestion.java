package views.Questionnaire;

import AAT.Util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * Created by marcel on 3/16/14.
 * This class contains the functionality to display the different types of questions in the Questionnaire.
 */
public abstract class DisplayQuestion extends JPanel {

    public JLabel asterisks;
    public boolean isRequired = true;
    private int maxLabelSize = 0;
    public JLabel leftLabel;

    public DisplayQuestion() {
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

class SemDiffPanel extends DisplayQuestion {

    private ButtonGroup semDiffScale;
    private int size;


    public SemDiffPanel(int size, String left, String right, boolean isRequired) {
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
        leftLabel = new JLabel(left, JLabel.RIGHT);
        leftLabel.setForeground(Color.WHITE);
        leftLabel.setBackground(Color.black);

        this.add(leftLabel);
        calculateLabelWidth(leftLabel);
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

class TextAreaPanel extends DisplayQuestion {

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

    @Override
    public void changeLabelSize(int width) {

    }


}

class ClosedButtonsPanel extends DisplayQuestion {

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

class OpenQuestionPanel extends DisplayQuestion {

    JTextField textInput;

    public OpenQuestionPanel(boolean isRequired) {
        super();
        this.isRequired = isRequired;
        textInput = new JTextField(10);
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

    @Override
    public void changeLabelSize(int width) {
    }
}


class ClosedComboPanel extends DisplayQuestion {

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

class LikertPanel extends DisplayQuestion {

    private ButtonGroup likertScale;

    public LikertPanel(int size, String left, String right, boolean isRequired) {
        super();
        this.isRequired = isRequired;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(Color.black);
        this.setForeground(Color.white);

        leftLabel = new JLabel(left, JLabel.RIGHT);
        leftLabel.setForeground(Color.WHITE);
        leftLabel.setBackground(Color.black);
        this.add(leftLabel);
        this.add(Box.createHorizontalStrut(20));//20px margin
        calculateLabelWidth(leftLabel);
        likertScale = new ButtonGroup();
        JPanel buttonPanel = new JPanel(new GridLayout(2, size, 20, 0));
        buttonPanel.setForeground(Color.white);
        buttonPanel.setBackground(Color.black);
        for (int x = 0; x < size; x++) {
            JLabel label = new JLabel(String.valueOf(x + 1), JLabel.CENTER);
            label.setForeground(Color.white);
            buttonPanel.add(label);
        }

        for (int x = 0; x < size; x++) {
            JRadioButton likertButton = new JRadioButton();
            likertButton.setBackground(Color.black);
            likertButton.setForeground(Color.white);

            buttonPanel.add(likertButton);
            likertScale.add(likertButton);
        }
        this.add(buttonPanel);
        this.add(Box.createHorizontalStrut(20));//20px margin
        JLabel rightLabel = new JLabel(right, JLabel.LEFT);
        //    rightLabel.setPreferredSize(new Dimension(200,30));
        rightLabel.setForeground(Color.white);
        rightLabel.setBackground(Color.black);
        this.add(rightLabel);

        if (isRequired) {
            add(asterisks);
        }
    }

    public String getValue() {
        int x = 1;
        for (Enumeration e = likertScale.getElements(); e.hasMoreElements(); ) {
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
        return "likert";
    }

}

