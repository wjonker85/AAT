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
    private Map<String, Component> questionsMap = new HashMap<String, Component>();
    private JPanel mainPanel;


    public QuestionPanel(final AATModel model) {
        //  super(new BoxLayout(this, BoxLayout.Y_AXIS));
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.black);
        mainPanel.setForeground(Color.white);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        System.out.println("Show questions");
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
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.black);
        scrollPane.setForeground(Color.white);
        mainPanel.add(scrollPane);
        JButton submitButton = new JButton("Submit");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                model.addExtraQuestions(getResults());
                setVisible(false);
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
            question.setFont(new Font("Roman", Font.TRUETYPE_FONT, 16));
            JComponent answer = null;
            questionsPanel.add(question);
            if (questionObject.getType().equals("closed")) {
                //      JComboBox<Object> answerOptions = new JComboBox<Object>(questionObject.getOptions().toArray());
                JComboBox answerOptions = new JComboBox(questionObject.getOptions().toArray());
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panel.add(answerOptions);
                panel.setBackground(Color.BLACK);
                panel.setForeground(Color.WHITE);
                question.setLabelFor(answerOptions);
                answerOptions.setBackground(Color.WHITE);
                answerOptions.setForeground(Color.BLACK);
                questionsPanel.add(panel);
                questionsMap.put(questionObject.getKey(), answerOptions);
            }
            if (questionObject.getType().equals("open")) {
                JTextField textInput = new JTextField(10);
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panel.setBackground(Color.black);
                panel.setForeground(Color.white);
                panel.add(textInput);
                question.setLabelFor(textInput);
                textInput.setBackground(Color.WHITE);
                textInput.setForeground(Color.BLACK);
                questionsPanel.add(panel);
                questionsMap.put(questionObject.getKey(), textInput);
            }
            if (questionObject.getType().equals("likert")) {
                LikertPanel likertScale = new LikertPanel(questionObject.getSize(), questionObject.getLeftText(), questionObject.getRightText());
                question.setLabelFor(likertScale);
                questionsPanel.add(likertScale);
                questionsMap.put(questionObject.getKey(), likertScale);
            }
            questionsPanel.setOpaque(true);
        }
        SpringUtilities.makeCompactGrid(questionsPanel,
                questions.size(), 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        this.repaint();
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
            if (c instanceof LikertPanel) {
                LikertPanel l = (LikertPanel) c;
                results.put(key, l.getValue());
            }
        }
        return results;
    }

    class LikertPanel extends JPanel {

        private int size;
        private String left, right;
        private ButtonGroup likertScale;

        public LikertPanel(int size, String left, String right) {
            this.size = size;
            this.left = left;
            this.right = right;
            //   this.setLayout(new SpringLayout());
            this.setBackground(Color.black);
            this.setForeground(Color.white);

            JLabel leftLabel = new JLabel(left);
            leftLabel.setForeground(Color.WHITE);
            leftLabel.setBackground(Color.black);
            this.add(leftLabel);
            likertScale = new ButtonGroup();

            for (int x = 0; x < size; x++) {
                JRadioButton likertButton = new JRadioButton();
                likertButton.setBackground(Color.black);
                likertButton.setForeground(Color.white);
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
            return "0";
        }
    }

}
