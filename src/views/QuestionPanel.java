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

import AAT.QuestionObject;
import Model.AATModel;

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
 * This is a panel that contains all the additional questions a researcher might be interested in.  These
 * questions come from the language file that is specified in the configuration. These questions are displayed for the
 * actual test is started. But only if there are any.
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
                setVisible(false);
                //   setDisabled();

            }
        });
        submitPanel.add(submitButton);
        this.add(submitPanel);
    }

    /**
     * Displays every question Object in the ArrayList<AAT.QuestionObject> A question can be open, have a textField on the screen
     * or closed and have a combobox with answers
     *
     * @param questions The optional question received from the model
     */
    public void displayQuestions(ArrayList<QuestionObject> questions) {
        for (QuestionObject questionObject : questions) {
            JLabel question = new JLabel(questionObject.getQuestion(), JLabel.TRAILING);
            question.setBackground(Color.black);
            question.setForeground(Color.WHITE);
            question.setFont(new Font("Roman", 30, 30));
            questionsPanel.add(question);
            if (questionObject.getOptions().size() > 1) {
                //      JComboBox<Object> answerOptions = new JComboBox<Object>(questionObject.getOptions().toArray());
                JComboBox answerOptions = new JComboBox(questionObject.getOptions().toArray());
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


    /*
   All the components are kept in a hash map, This provides a flexibal way to have an unlimited nr of components
   and keep track of them
    */
    private HashMap<String, String> getResults() {
        HashMap<String, String> results = new HashMap<String, String>();
        for (String key : questionsMap.keySet()) {
            Component c = questionsMap.get(key);
            if (c instanceof JTextField) {
                JTextField t = (JTextField) c;
                String input = t.getText();
                if (t.getText().equals("")) {
                    input = "N/A";  //Replace the empty input with "N/A"
                }
                results.put(key, input);
            }
            if (c instanceof JComboBox) {
                JComboBox jc = (JComboBox) c;
                results.put(key, jc.getSelectedItem().toString());
            }
        }
        return results;
    }
}
