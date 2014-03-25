package views.Questionnaire.QuestionEditPanels;

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.*;
import views.Questionnaire.QuestionnaireModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by marcel on 3/25/14.
 */
public class ClosedQuestionEditPanel<T extends AbstractQuestion> extends AbstractQuestionEditPanel {
    private JTextField question, qLabel;
    private int noChoices = 15;
    private JCheckBox required;
    ArrayList<JTextField> allChoices;
    private ArrayList<String> currentChoices;
    private JPanel optionsPanel;
    private AbstractClosedQuestion original;


    public ClosedQuestionEditPanel(AbstractClosedQuestion original) {
        super();
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

    @Override
    public T getQuestion() {
        AbstractClosedQuestion abstractQuestion = null;
        if(original instanceof ClosedComboQuestion) {
            abstractQuestion = new ClosedComboQuestion();
        }
        else if(original instanceof ClosedButtonQuestion) {
            abstractQuestion = new ClosedButtonQuestion();
        }
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
        return false;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}

