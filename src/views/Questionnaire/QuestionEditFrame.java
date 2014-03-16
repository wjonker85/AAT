package views.Questionnaire;

import AAT.Util.SpringUtilities;
import DataStructures.QuestionData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marcel on 3/16/14.
 * Class containing question edit functionality.
 */

public class QuestionEditFrame extends JFrame {
    private QuestionData question;
    private String currentType;
    private String[] types = {"Likert Scale", "Semantic differential", "Closed question (combo-box)", "Closed question (buttons)", "Open question", "Open question with text area"};
    private HashMap<String, String> translations;
    private HashMap<String, String> translationsRev;
    private JComboBox typeCombo;
    private int pos;
    private JPanel editPanel;
    private QuestionEditor qEditor;
    private QuestionPanel parent;

    public QuestionEditFrame(QuestionData question, int pos, QuestionPanel parent) {
        this.pos = pos;
        this.question = question;
        this.parent = parent;
        translations = new HashMap<String, String>();
        translationsRev = new HashMap<String, String>();
        translations.put("Likert Scale", "likert");
        translations.put("Closed question (combo-box)", "closed_combo");
        translations.put("Open question", "open");
        translations.put("Closed question (buttons)", "closed_button");
        translations.put("Semantic differential", "sem_diff");
        translations.put("Open question with text area", "textArea");
        translationsRev.put("likert", "Likert Scale");
        translationsRev.put("closed_combo", "Closed question (combo-box)");
        translationsRev.put("open", "Open question");
        translationsRev.put("closed_button", "Closed question (buttons)");
        translationsRev.put("sem_diff", "Semantic differential");
        translationsRev.put("textArea", "Open question with text area");
        JPanel setTypePanel = new JPanel();
        editPanel = new JPanel();
        JLabel typeLabel = new JLabel("Type of question: ");
        setTypePanel.add(typeLabel);
        typeCombo = new JComboBox(types);
        if (question != null) {
            currentType = question.getType();
            System.out.println("Current type = " + currentType);
            changeQuestionEditor();
            revalidate();
            repaint();

        } else {
            typeCombo.setSelectedItem(translationsRev.get("open")); //Default to open question for new Question.
            currentType = "open";
            //   typeCombo.setSelectedItem(currentType);
            changeQuestionEditor();
            revalidate();
            repaint();
        }


        setTypePanel.add(typeCombo);
        JButton setTypeButton = new JButton("Set question type");
        setTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                currentType = translations.get(typeCombo.getSelectedItem().toString());
                changeQuestionEditor();

            }
        });
        setTypePanel.add(setTypeButton);


        //  this.setMinimumSize(new Dimension(400, 400));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(setTypePanel);
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (qEditor.validated() && qEditor.getQuestion().getKey().length() > 0 && qEditor.getQuestion().getQuestion().length() > 0) {
                    changeQuestion();
                    closeAction();
                } else if (qEditor.getQuestion().getKey().length() == 0) {
                    JOptionPane.showConfirmDialog(null, "Question label needs to be set.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (qEditor.getQuestion().getQuestion().length() == 0) {
                        JOptionPane.showConfirmDialog(null, "Question is empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });
        buttonPanel.add(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeAction();
            }
        });
        buttonPanel.add(cancelButton);
        mainPanel.add(editPanel);
        mainPanel.add(buttonPanel);
        this.getContentPane().add(mainPanel);
        this.pack();
    }

    private void closeAction() {
        this.dispose();
    }

    private void changeQuestionEditor() {
        editPanel.removeAll();

        if (currentType.equalsIgnoreCase("closed_combo") || currentType.equalsIgnoreCase("closed_button")) {
            ClosedPanel p = new ClosedPanel(question, currentType);
            editPanel.removeAll();
            editPanel.add(p);
            qEditor = p;
            revalidate();
        } else if (currentType.equalsIgnoreCase("open")) {
            OpenPanel p = new OpenPanel(question);
            editPanel.removeAll();
            editPanel.add(p);
            qEditor = p;
        } else if (currentType.equalsIgnoreCase("textArea")) {
            TextAreaQPanel p = new TextAreaQPanel(question);
            editPanel.add(p);
            qEditor = p;

        } else if (currentType.equalsIgnoreCase("sem_diff") || currentType.equalsIgnoreCase("likert")) {
            LikertDiffPanel p = new LikertDiffPanel(question, currentType);
            editPanel.add(p);
            qEditor = p;

        }
        revalidate();
        repaint();
        pack();
    }

    private void changeQuestion() {
        parent.changeQuestion(qEditor.getQuestion(), pos);
    }
}

    class ClosedPanel extends JPanel implements QuestionEditor {
        private JTextField question, qLabel, noChoicesInput;
        private JCheckBox required;
        private int noChoices = 15;
        ArrayList<JTextField> allChoices;
        private ArrayList<String> currentChoices;
        private String type;
        private JPanel optionsPanel;

        public ClosedPanel(QuestionData original, String type) {
            this.type = type;
            this.setLayout(new SpringLayout());
            this.add(new JLabel("Question: "));
            allChoices = new ArrayList<JTextField>();
            currentChoices = new ArrayList<String>();
            optionsPanel = new JPanel(new SpringLayout());
            question = new JTextField();
            question.setPreferredSize(new Dimension(250, 20));
            this.add(question);
            this.add(new JLabel("Choices: "));
            this.add(optionsPanel);
            this.add(new JLabel("Label for analysis: "));
            qLabel = new JTextField();
            this.add(qLabel);
            JLabel reqLabel = new JLabel("Required: ");
            this.add(reqLabel);
            required = new JCheckBox();
            required.setSelected(true);
            this.add(required);
            if (original != null) {
                question.setText(original.getQuestion());
                qLabel.setText(original.getQuestion());
                required.setSelected(original.isRequired());
                currentChoices = original.getOptions();
                setChoices(optionsPanel);
                addCurrentChoices();
            }
            SpringUtilities.makeCompactGrid(this,
                    4, 2, //rows, cols
                    6, 6,        //initX, initY
                    6, 6);       //xPad, yPad

            setChoices(optionsPanel);
            addCurrentChoices();
            revalidate();
            repaint();
        }

        //Fill with the current option values after a change or when filled for the first time and original question has values.
        private void addCurrentChoices() {
            int smallest = allChoices.size();
            if (smallest < currentChoices.size()) {
                smallest = currentChoices.size();
            }
            for (int x = 0; x < smallest; x++) {
                if (x < currentChoices.size() && x < allChoices.size()) {
                    allChoices.get(x).setText(currentChoices.get(x));
                }
            }
        }

        private void setChoices(JPanel contentPanel) {

            contentPanel.removeAll();
            allChoices.clear();
            for (int x = 0; x < noChoices; x++) {
                JLabel label = new JLabel("Option " + x + ": ");
                contentPanel.add(label);
                JTextField option = new JTextField();
                option.setPreferredSize(new Dimension(250, 20));
                option.setMinimumSize(new Dimension(250, 20));
                option.setMaximumSize(new Dimension(250, 20));
                contentPanel.add(option);
                allChoices.add(option);
            }
            contentPanel.revalidate();
            SpringUtilities.makeCompactGrid(contentPanel,
                    noChoices, 2, //rows, cols
                    6, 6,        //initX, initY
                    6, 6);       //xPad, yPad
        }

        private void changeChoicesAction() {
            //First save the current options
            currentChoices.clear();
            for (JTextField t : allChoices) {
                currentChoices.add(t.getText());
            }
            boolean succes = true;
            int newChoices = 0;
            try {
                newChoices = Integer.parseInt(noChoicesInput.getText());
            } catch (Exception e) {
                succes = false;
            }
            if (succes && newChoices > 0) {
                noChoices = newChoices;
            }
            setChoices(optionsPanel);
            addCurrentChoices();
        }

        @Override
        public QuestionData getQuestion() {
            QuestionData questionData = new QuestionData(type);
            System.out.println("Adding " + type);
            questionData.setKey(qLabel.getText());
            questionData.setQuestion(question.getText());
            questionData.setRequired(required.isSelected());
            currentChoices.clear();
            for (JTextField t : allChoices) {
                currentChoices.add(t.getText());
            }
            for (String s : currentChoices) {
                if (s.length() > 0) {
                    questionData.addOptions(s);
                }
            }
            return questionData;
        }

        @Override
        public boolean validated() {
            return true;
        }
    }


    class OpenPanel extends JPanel implements QuestionEditor {
        private JTextField question, qLabel;
        private JCheckBox required;

        public OpenPanel(QuestionData original) {
            this.setLayout(new SpringLayout());
            this.add(new JLabel("Question: "));
            question = new JTextField();
            question.setPreferredSize(new Dimension(250, 20));
            this.add(question);
            this.add(new JLabel("Label for analysis: "));
            qLabel = new JTextField();
            this.add(qLabel);
            JLabel reqLabel = new JLabel("Required: ");
            this.add(reqLabel);
            required = new JCheckBox();
            required.setSelected(true);
            this.add(required);
            if (original != null) {
                question.setText(original.getQuestion());
                qLabel.setText(original.getQuestion());
                required.setSelected(original.isRequired());
            }
            SpringUtilities.makeCompactGrid(this,
                    3, 2, //rows, cols
                    6, 6,        //initX, initY
                    6, 6);       //xPad, yPad


        }

        @Override
        public QuestionData getQuestion() {
            QuestionData questionData = new QuestionData("open");
            questionData.setKey(qLabel.getText());
            questionData.setQuestion(question.getText());
            questionData.setRequired(required.isSelected());
            return questionData;
        }

        @Override
        public boolean validated() {
            return true;
        }
    }

    /**
     * Class used for the likert scale and the semantic differential scale. Basis properties are the same for both.
     */
    class LikertDiffPanel extends JPanel implements QuestionEditor {
        private JTextField qLabel;
        private JTextField question, left, right, size;
        private String type;
        private JCheckBox required;

        public LikertDiffPanel(QuestionData original, String type) {
            this.type = type;

            this.setLayout(new SpringLayout());

            this.add(new JLabel("Question: "));

            question = new JTextField();
            question.setPreferredSize(new Dimension(250, 20));
            this.add(question);
            left = new JTextField();
            JLabel leftLabel = new JLabel("Left label: ");
            left.setPreferredSize(new Dimension(120, 20));
            this.add(leftLabel);
            this.add(left);
            right = new JTextField();
            JLabel rightLabel = new JLabel("Right label: ");
            right.setPreferredSize(new Dimension(120, 20));
            this.add(rightLabel);
            this.add(right);
            size = new JTextField();
            JLabel sizeLabel = new JLabel("No options: ");
            this.add(sizeLabel);
            this.add(size);
            size.setPreferredSize(new Dimension(40, 20));

            this.add(new JLabel("Label for analysis: "));
            qLabel = new JTextField();
            this.add(qLabel);
            JLabel reqLabel = new JLabel("Required: ");
            this.add(reqLabel);
            required = new JCheckBox();
            required.setSelected(true);
            this.add(required);
            if (original != null) {
                question.setText(original.getQuestion());
                left.setText(original.getLeftText());
                right.setText(original.getRightText());
                size.setText(String.valueOf(original.getSize()));
                qLabel.setText(original.getQuestion());
                required.setSelected(original.isRequired());
            }
            SpringUtilities.makeCompactGrid(this,
                    6, 2, //rows, cols
                    6, 6,        //initX, initY
                    6, 6);       //xPad, yPad

        }

        @Override
        public QuestionData getQuestion() {

            QuestionData questionData = new QuestionData(type);
            questionData.setKey(qLabel.getText());
            questionData.setQuestion(question.getText());
            questionData.setLeftText(left.getText());
            questionData.setRightText(right.getText());
            questionData.setSize(Integer.parseInt(size.getText()));
            questionData.setRequired(required.isSelected());
            return questionData;
        }

        @Override
        public boolean validated() {
            try {
                int x = Integer.parseInt(size.getText());
                if (x > 0) {
                    return true;
                } else return false;
            } catch (Exception e) {
                return false;
            }
        }
    }


    class TextAreaQPanel extends JPanel implements QuestionEditor {
        private JTextField qLabel;
        private JTextField question;
        private JCheckBox required;

        public TextAreaQPanel(QuestionData original) {
            this.setLayout(new SpringLayout());
            this.add(new JLabel("Question: "));
            question = new JTextField();
            question.setPreferredSize(new Dimension(250, 20));
            this.add(question);
            this.add(new JLabel("Label for analysis: "));
            qLabel = new JTextField();
            this.add(qLabel);
            JLabel reqLabel = new JLabel("Required: ");
            this.add(reqLabel);
            required = new JCheckBox();
            required.setSelected(true);
            this.add(required);
            if (original != null) {
                question.setText(original.getQuestion());
                qLabel.setText(original.getQuestion());
                required.setSelected(original.isRequired());
            }
            SpringUtilities.makeCompactGrid(this,
                    3, 2, //rows, cols
                    6, 6,        //initX, initY
                    6, 6);       //xPad, yPad

        }

        @Override
        public QuestionData getQuestion() {
            QuestionData questionData = new QuestionData("textArea");
            questionData.setKey(qLabel.getText());
            questionData.setQuestion(question.getText());
            questionData.setRequired(required.isSelected());
            return questionData;
        }

        @Override
        public boolean validated() {
            return true;
        }
    }

interface QuestionEditor {
    public QuestionData getQuestion();

    public boolean validated();
}



