import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 1:15 PM
 * This is a panel that contains all the additional questions a researcher might be interested in.
*/
public class QuestionPanel extends JPanel {


    private JPanel questionsPanel;
    private Map<String, Component> questionsMap = new HashMap<String, Component>();

    public QuestionPanel(final AATModel model) {
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        questionsPanel = new JPanel(new SpringLayout());
        JPanel submitPanel = new JPanel();
        submitPanel.setBackground(Color.BLACK);
        submitPanel.setForeground(Color.WHITE);
        questionsPanel.setBackground(Color.black);
        questionsPanel.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.black);
        scrollPane.setForeground(Color.white);
        this.add(scrollPane);
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                model.addExtraQuestions(getResults());
                setDisabled();

            }
        });
        submitPanel.add(submitButton);
        this.add(submitPanel);
    }

    private void setDisabled() {
        this.setVisible(false);
        this.setEnabled(false);
    }

    public void displayQuestions(ArrayList<QuestionObject> questions) {
        for (QuestionObject questionObject : questions) {
            JLabel question = new JLabel(questionObject.getQuestion(), JLabel.TRAILING);
            question.setBackground(Color.black);
            question.setForeground(Color.WHITE);
            question.setFont(new Font("Roman", 30, 30));
            questionsPanel.add(question);
            if (questionObject.getOptions().size() > 1) {
                JComboBox<Object> answerOptions = new JComboBox<Object>(questionObject.getOptions().toArray());
                question.setLabelFor(answerOptions);
                answerOptions.setBackground(Color.WHITE);
                answerOptions.setForeground(Color.BLACK);
                questionsPanel.add(answerOptions);
                questionsMap.put(questionObject.getKey(), answerOptions);
            } else {
                JTextField textInput = new JTextField(10);
                question.setLabelFor(textInput);
                textInput.setBackground(Color.WHITE);
                textInput.setForeground(Color.BLACK);
                questionsPanel.add(textInput);
                questionsMap.put(questionObject.getKey(), textInput);
            }
            questionsPanel.setOpaque(true);
        }
        SpringUtilities.makeCompactGrid(questionsPanel,
                questions.size(), 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

    }

    private HashMap<String, String> getResults() {
        HashMap<String, String> results = new HashMap<String, String>();
        for (String key : questionsMap.keySet()) {
            Component c = questionsMap.get(key);
            if (c instanceof JTextField) {
                JTextField t = (JTextField) c;
                results.put(key, t.getText());
            }
            if (c instanceof JComboBox) {
                JComboBox jc = (JComboBox) c;
                results.put(key, jc.getSelectedItem().toString());
            }
        }
        return results;
    }
}
