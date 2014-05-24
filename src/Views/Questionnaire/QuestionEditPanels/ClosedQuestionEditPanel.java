package Views.Questionnaire.QuestionEditPanels;

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.AbstractClosedQuestion;
import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.ClosedButtonQuestion;
import DataStructures.Questionnaire.ClosedComboQuestion;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by marcel on 3/25/14.
 * Edit for a closed type of question
 */
public class ClosedQuestionEditPanel<T extends AbstractQuestion> extends AbstractQuestionEditPanel {
    ArrayList<JTextField> allChoices;
    private ArrayList<String> currentChoices;
    private AbstractClosedQuestion original;


    public ClosedQuestionEditPanel(AbstractClosedQuestion original) {
        super();
        allChoices = new ArrayList<JTextField>();
        currentChoices = new ArrayList<String>();
        JPanel optionsPanel = new JPanel(new SpringLayout());
        this.add(new JLabel("Choices: "));
        this.add(optionsPanel);
        addRequiredFields();
        this.original = original;
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
        int noChoices = 15;
        for (int x = 0; x < noChoices; x++) {
            JLabel label = new JLabel("Option " + x + ": ");
            contentPanel.add(label);
            JTextField option = new JTextField();
            option.setPreferredSize(new Dimension(250, 25));
            option.setMinimumSize(new Dimension(250, 25));
            option.setMaximumSize(new Dimension(250, 25));
            contentPanel.add(option);
            allChoices.add(option);
        }
        contentPanel.revalidate();
        SpringUtilities.makeCompactGrid(contentPanel,
                noChoices, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
    }

    @Override
    public T getQuestion() {
        AbstractClosedQuestion abstractQuestion = null;
        if (original instanceof ClosedComboQuestion) {
            abstractQuestion = new ClosedComboQuestion();
        } else if (original instanceof ClosedButtonQuestion) {
            abstractQuestion = new ClosedButtonQuestion();
        }
        assert abstractQuestion != null;
        abstractQuestion.setKey(qLabel.getText());
        abstractQuestion.setQuestion(question.getText());
        abstractQuestion.setRequired(required.isSelected());
        currentChoices.clear();
        for (JTextField t : allChoices) {
            currentChoices.add(t.getText());
        }
        for (String s : currentChoices) {
            if (s.length() > 0) {
                abstractQuestion.addOptions(s);
            }
        }
        return (T) abstractQuestion;
    }

    @Override
    public boolean validated() {
        return true;
    }
}

