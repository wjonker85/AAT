import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuestionFrame extends JFrame implements Observer{


        private JPanel mainPanel;
    private JPanel questionsPanel;
    private JPanel submitPanel;
    private JButton submitButton;
    private Map<String,Component> questionsMap = new HashMap<String, Component>();

    public QuestionFrame(final AATModel model) {
        mainPanel = new JPanel();
        submitPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel,BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        mainPanel.add(scrollPane);
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                model.addExtraQuestions(getResults());
            }
        });
        submitPanel.add(submitButton);
        mainPanel.add(submitPanel);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(false);
    }

    public void displayQuestions(ArrayList<QuestionObject> questions) {
        for(QuestionObject questionObject : questions) {
            JPanel questionPanel = new JPanel();
       questionPanel.setLayout(new BoxLayout(questionPanel,BoxLayout.X_AXIS));
        JLabel question = new JLabel(questionObject.getQuestion());
            questionPanel.add(question);
       if(questionObject.getOptions().size()>1) {
           JComboBox<String> answerOptions = new JComboBox(questionObject.getOptions().toArray());
           questionPanel.add(answerOptions);
           questionsMap.put(questionObject.getKey(), answerOptions);
       }
            else {
              JTextField textInput = new JTextField();
           questionPanel.add(textInput);
                questionsMap.put(questionObject.getKey(),textInput);
       }
        questionsPanel.add(questionPanel);
        }

    }

    public void display() {
        this.setVisible(true);
    }

    private HashMap<String,String> getResults() {
        HashMap<String,String> results = new HashMap<String, String>();
        for(String key: questionsMap.keySet()) {
            Component c = questionsMap.get(key);
            if(c instanceof JTextField) {
                JTextField t = (JTextField) c;
                results.put(key,t.getText());
            }
            if(c instanceof JComboBox) {
                JComboBox jc = (JComboBox) c;
                results.put(key,jc.getSelectedItem().toString());
            }
        }
        return results;
    }

    public void update(Observable observable, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
