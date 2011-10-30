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
 * A
 */
public class QuestionPanel extends JPanel {


    //  private JPanel mainPanel;
    private JPanel questionsPanel;
    private JPanel submitPanel;
    private JButton submitButton;
    private AATModel model;
    private Map<String, Component> questionsMap = new HashMap<String, Component>();

    public QuestionPanel(final AATModel model) {
        this.setBackground(Color.black);
        this.setForeground(Color.white);
         this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.model = model;
        submitPanel = new JPanel();
        submitPanel.setBackground(Color.BLACK);
        submitPanel.setForeground(Color.WHITE);
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBackground(Color.black);
        questionsPanel.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.black);
        scrollPane.setForeground(Color.white);
        this.add(scrollPane);
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                model.addExtraQuestions(getResults());
                setDisabled();

            }
        });
        submitPanel.add(submitButton);
        this.add(submitPanel);
        // setContentPane(mainPanel);
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //  pack();
        //  setVisible(false);
    }

    private void setDisabled() {
        this.setVisible(false);
        this.setEnabled(false);
    }

    public void displayQuestions(ArrayList<QuestionObject> questions) {
        for (QuestionObject questionObject : questions) {
            JPanel questionPanel = new JPanel();
         //   questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.X_AXIS));
            questionPanel.setBackground(Color.black);
            questionPanel.setForeground(Color.WHITE);
            JLabel question = new JLabel(questionObject.getQuestion());
            question.setBackground(Color.black);
            question.setForeground(Color.WHITE);
            question.setFont(new Font("Roman",30,30));
            questionPanel.add(question);
            if (questionObject.getOptions().size() > 1) {
                JComboBox<String> answerOptions = new JComboBox(questionObject.getOptions().toArray());
                answerOptions.setBackground(Color.WHITE);
                answerOptions.setForeground(Color.BLACK);
                questionPanel.add(answerOptions);
                questionsMap.put(questionObject.getKey(), answerOptions);
            } else {
                JTextField textInput = new JTextField(10);
                textInput.setBackground(Color.WHITE);
                textInput.setForeground(Color.BLACK);
                questionPanel.add(textInput);
                questionsMap.put(questionObject.getKey(), textInput);
            }
            questionsPanel.add(questionPanel);
        }

    }

    public void display() {
        this.setVisible(true);
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
