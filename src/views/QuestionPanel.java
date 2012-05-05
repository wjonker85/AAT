/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package views;

import DataStructures.QuestionData;
import DataStructures.Questionnaire;
import Model.AATModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 1:15 PM
 * This is a panel that contains all the additional questions a researcher might be interested in.  These
 * questions come from the language file that is specified in the configuration. These questions are displayed for the
 * actual test is started. But only if there are any.
 */
public class QuestionPanel extends JPanel {


    private JPanel questionsPanel;
    private Map<String, DisplayQuestion> questionsMap = new HashMap<String, DisplayQuestion>();


    public QuestionPanel(final AATModel model) {
        //  super(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setForeground(Color.white);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        Dimension screen = getToolkit().getScreenSize();
        //  this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        this.setPreferredSize(new Dimension(screen.width / 2, screen.height));
        this.setMaximumSize(new Dimension(screen.width / 2, screen.height));


        questionsPanel = new JPanel(new SpringLayout());
        JPanel submitPanel = new JPanel();
        submitPanel.setBackground(Color.BLACK);
        submitPanel.setForeground(Color.WHITE);
        questionsPanel.setBackground(Color.black);
        questionsPanel.setForeground(Color.WHITE);
        JPanel questionnairePanel = new JPanel();
        questionnairePanel.setLayout(new BoxLayout(questionnairePanel, BoxLayout.Y_AXIS));
        JTextPane introductionPane = new JTextPane();
        introductionPane.setForeground(Color.white);
        introductionPane.setBackground(Color.black);
        introductionPane.setEditable(false);
        introductionPane.setFont(new Font("Roman", Font.PLAIN, 24));
        introductionPane.setText(model.getTest().getQuestionnaire().getIntroduction() + "\n\n");
        questionnairePanel.add(introductionPane);
        questionnairePanel.add(questionsPanel);
        JScrollPane scrollPane = new JScrollPane(questionnairePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.black);
        scrollPane.setForeground(Color.white);
        mainPanel.add(scrollPane);
        //   questionsPanel.add(introductionPane);

        JButton submitButton = new JButton("Submit");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (checkAnswered()) {
                    model.addExtraQuestions(getResults());
                    setVisible(false);
                } else {
                    repaint();
                }
                //   setDisabled();

            }
        });
        buttonPanel.setBackground(Color.black);
        buttonPanel.setForeground(Color.white);
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel);
        this.add(mainPanel, new GridBagConstraints());
        //   repaint();
    }


    private boolean checkAnswered() {
        int count = 0;
        for (String key : questionsMap.keySet()) {
            DisplayQuestion q = questionsMap.get(key);
            if (q.getValue().equals("") && q.isRequired) {
                System.out.println("Nog niet beantwoord");
                q.changeAsteriskColor(Color.red);
                count++;
            } else {
                q.changeAsteriskColor(Color.white);  //Change red ones back to white when already answered.
            }

        }
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * Displays every question Object in the ArrayList<AAT.QuestionData> A question can be open, have a textField on the screen
     * or closed and have a combobox with answers
     *
     * @param questionnaire The optional questionnaire received from the model
     */
    public void displayQuestions(Questionnaire questionnaire) {
        for (QuestionData questionObject : questionnaire.getExtraQuestions()) {

            JLabel question = new JLabel(questionObject.getQuestion(), JLabel.TRAILING);
            question.setBackground(Color.black);
            question.setForeground(Color.WHITE);
            question.setFont(new Font("Roman", Font.PLAIN, 16));
            questionsPanel.add(question);
            if (questionObject.getType().equals("closed")) {
                ClosedQuestionPanel answerOptions = new ClosedQuestionPanel(questionObject.getOptions().toArray(), questionObject.isRequired());
                question.setLabelFor(answerOptions);
                questionsPanel.add(answerOptions);
                questionsMap.put(questionObject.getKey(), answerOptions);
            }
            if (questionObject.getType().equals("open")) {
                OpenQuestionPanel textInput = new OpenQuestionPanel(questionObject.isRequired());
                question.setLabelFor(textInput);
                questionsPanel.add(textInput);
                questionsMap.put(questionObject.getKey(), textInput);
            }

            if (questionObject.getType().equals("textarea")) {
                TextAreaPanel textArea = new TextAreaPanel(questionObject.isRequired());
                question.setLabelFor(textArea);
                questionsPanel.add(textArea);
                questionsMap.put(questionObject.getKey(), textArea);
            }
            if (questionObject.getType().equals("likert")) {
                LikertPanel likertScale = new LikertPanel(questionObject.getSize(), questionObject.getLeftText(), questionObject.getRightText(), questionObject.isRequired());
                question.setLabelFor(likertScale);
                questionsPanel.add(likertScale);
                questionsMap.put(questionObject.getKey(), likertScale);
            }

            if (questionObject.getType().equals("sem_diff")) {
                SemDiffPanel semanticDifferential = new SemDiffPanel(questionObject.getSize(), questionObject.getLeftText(), questionObject.getRightText(), questionObject.isRequired());
                question.setLabelFor(semanticDifferential);
                questionsPanel.add(semanticDifferential);
                questionsMap.put(questionObject.getKey(), semanticDifferential);
            }
            questionsPanel.setOpaque(true);
        }
        SpringUtilities.makeCompactGrid(questionsPanel,
                questionnaire.getExtraQuestions().size(), 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        this.repaint();
    }

    private int calculateLabelWidth(JLabel label) {
        Font labelFont = label.getFont();
        String labelText = label.getText();

        int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
        System.out.println(stringWidth);
        return stringWidth;
    }

    /*
   All the components are kept in a hash map, This provides a flexibal way to have an unlimited nr of components
   and keep track of them
    */
    private HashMap<String, String> getResults() {
        HashMap<String, String> results = new HashMap<String, String>();
        for (String key : questionsMap.keySet()) {
            DisplayQuestion c = questionsMap.get(key);
            String input = c.getValue();
            if (input.equals("")) {
                input = "N/A";  //Replace the empty input with "N/A"
            }
            results.put(key, input);
        }
        return results;
    }


    class SemDiffPanel extends DisplayQuestion {

        private ButtonGroup semDiffScale;
        private int size;

        public SemDiffPanel(int size, String left, String right, boolean isRequired) {
            super();
            this.isRequired = isRequired;
            this.size = size;
            this.setBackground(Color.black);
            this.setForeground(Color.white);

            JLabel leftLabel = new JLabel(left);
            leftLabel.setForeground(Color.WHITE);
            leftLabel.setBackground(Color.black);
            this.add(Box.createHorizontalStrut(300));
            this.add(leftLabel);
            semDiffScale = new ButtonGroup();

            for (int x = 0; x < size; x++) {
                JRadioButton likertButton = new JRadioButton();
                likertButton.setBackground(Color.black);
                likertButton.setForeground(Color.white);
                int step = size / 2;
                JLabel label = new JLabel(String.valueOf(x - step));
                label.setForeground(Color.white);
                this.add(label);
                this.add(likertButton);
                semDiffScale.add(likertButton);
            }

            JLabel rightLabel = new JLabel(right, JLabel.TRAILING);
            rightLabel.setForeground(Color.white);
            rightLabel.setBackground(Color.black);
            this.add(rightLabel);
            //  SpringUtilities.makeCompactGrid(questionsPanel,
            //        1, 3, //rows, cols
            //      6, 6,        //initX, initY
            //    6, 6);       //xPad, yPad
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
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            //  JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            add(scrollPane);
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

    class OpenQuestionPanel extends DisplayQuestion {

        JTextField textInput;

        public OpenQuestionPanel(boolean isRequired) {
            super();
            this.isRequired = isRequired;
            textInput = new JTextField(10);
            setLayout(new FlowLayout(FlowLayout.LEFT));
            //  JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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


    class ClosedQuestionPanel extends DisplayQuestion {

        JComboBox answerOptions;

        public ClosedQuestionPanel(Object[] options, boolean isRequired) {
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
            return answerOptions.getSelectedItem().toString();
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

            JLabel leftLabel = new JLabel(left);
            leftLabel.setForeground(Color.WHITE);
            leftLabel.setBackground(Color.black);
            this.add(leftLabel);
            likertScale = new ButtonGroup();
            System.out.println(leftLabel.getSize());
            for (int x = 0; x < size; x++) {
                JRadioButton likertButton = new JRadioButton();
                likertButton.setBackground(Color.black);
                likertButton.setForeground(Color.white);
                JLabel label = new JLabel(String.valueOf(x + 1));
                label.setForeground(Color.white);
                this.add(label);
                this.add(likertButton);
                likertScale.add(likertButton);
            }

            JLabel rightLabel = new JLabel(right, JLabel.TRAILING);
            rightLabel.setForeground(Color.white);
            rightLabel.setBackground(Color.black);
            this.add(rightLabel);
            //  SpringUtilities.makeCompactGrid(questionsPanel,
            //        1, 3, //rows, cols
            //      6, 6,        //initX, initY
            //    6, 6);       //xPad, yPad
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
}

abstract class DisplayQuestion extends JPanel {

    public JLabel asterisks;
    public boolean isRequired = true;

    public DisplayQuestion() {
        asterisks = new JLabel("*", JLabel.TRAILING);
        asterisks.setFont(new Font("Roman", Font.BOLD, 20));
        asterisks.setForeground(Color.WHITE);
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
