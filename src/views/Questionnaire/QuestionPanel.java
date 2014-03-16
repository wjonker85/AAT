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

package views.Questionnaire;

import AAT.Util.SpringUtilities;
import DataStructures.QuestionData;
import DataStructures.Questionnaire;
import IO.XMLReader;
import IO.XMLWriter;
import Model.AATModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
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
    private Map<String, DisplayQuestion> questionsMap = new HashMap<String, DisplayQuestion>();

    private JScrollPane scrollPane;
    private JTextArea introductionPane;
    private Boolean editMode = false;
    private Questionnaire questionnaire;
    private int maxLabelSize = 0;

    public QuestionPanel(final AATModel model) {
        if (model == null) {
            editMode = true;
        }

        //  super(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel mainPanel = new JPanel();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.black);
        contentPanel.setForeground(Color.white);
        mainPanel.setBackground(Color.black);
        mainPanel.setForeground(Color.white);
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));    //TODO even naar kijken
        Dimension screen = getToolkit().getScreenSize();
        //  this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        this.setPreferredSize(new Dimension(screen.width / 2, screen.height));
        this.setMaximumSize(new Dimension(screen.width / 2, screen.height));

        if (editMode) {
            JPanel addButtonPanel = new JPanel();
            JButton button = new JButton("Add question");
            addButtonPanel.add(button);
            contentPanel.add(addButtonPanel);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    addQuestionAction();
                }
            });
        }

        questionsPanel = new JPanel(new SpringLayout());
        JPanel submitPanel = new JPanel();
        submitPanel.setBackground(Color.BLACK);
        submitPanel.setForeground(Color.WHITE);
        questionsPanel.setBackground(Color.black);
        questionsPanel.setForeground(Color.WHITE);
        JPanel questionnairePanel = new JPanel();
        questionnairePanel.setLayout(new BoxLayout(questionnairePanel, BoxLayout.Y_AXIS));
        questionnairePanel.setBackground(Color.black);
        questionnairePanel.setForeground(Color.white);
        introductionPane = new JTextArea();
        introductionPane.setBorder(BorderFactory.createLineBorder(Color.black, 10));
        //   introductionPane.setColumns(80);
        introductionPane.setLineWrap(true);
        introductionPane.setWrapStyleWord(true);
        introductionPane.setForeground(Color.white);
        introductionPane.setBackground(Color.black);
        introductionPane.setEditable(false);
        introductionPane.setFont(new Font("Roman", Font.PLAIN, 24));

        questionnairePanel.add(introductionPane);
        questionnairePanel.add(Box.createVerticalStrut(20)); //small margin
        questionnairePanel.add(questionsPanel);
        contentPanel.add(questionnairePanel);

        if (model != null) {
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
                }
            });
            buttonPanel.setBackground(Color.black);
            buttonPanel.setForeground(Color.white);
            buttonPanel.add(submitButton);
            contentPanel.add(buttonPanel);
        }

        scrollPane = new JScrollPane(contentPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setPreferredSize(new Dimension((int) (0.8 * screen.width), (int) (0.75 * screen.height)));
        scrollPane.setMinimumSize(new Dimension((int) (0.8 * screen.width), (int) (0.75 * screen.height)));
        scrollPane.setMaximumSize(new Dimension((int) (0.8 * screen.width), (int) (0.75 * screen.height)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.black);
        scrollPane.setForeground(Color.white);
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        InputMap im = vertical.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.put(KeyStroke.getKeyStroke("DOWN"), "positiveUnitIncrement");
        im.put(KeyStroke.getKeyStroke("UP"), "negativeUnitIncrement");
        mainPanel.add(scrollPane);
        this.add(mainPanel, new GridBagConstraints());
    }


    private void addQuestionAction() {
        if (questionnaire == null) {
            questionnaire = new Questionnaire(new ArrayList<QuestionData>(), "");
        }
        QuestionEditFrame editFrame = new QuestionEditFrame(null, questionnaire.getExtraQuestions().size() + 1, this);
        editFrame.setEnabled(true);
        editFrame.setVisible(true);
    }

    private boolean checkAnswered() {
        int count = 0;
        for (String key : questionsMap.keySet()) {
            DisplayQuestion q = questionsMap.get(key);
            if (q.getValue().equals("") && q.isRequired) {
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


    public void saveQuestionnaire(File file) {
        XMLWriter.writeXMLQuestionnaire(file, this.questionnaire);
    }


    public void displayQuestions(File file) {
        XMLReader reader = new XMLReader(new File(""));
        reader.addQuestionnaire(file);
        Questionnaire q = new Questionnaire(reader.getExtraQuestions(), reader.getQuestionnaireIntro());
        displayQuestions(q);
    }

    /**
     * Displays every question Object in the ArrayList<AAT.QuestionData> A question can be open, have a textField on the screen
     * or closed and have a combobox with answers
     *
     * @param questionnaire The optional Questionnaire received from the model
     */
    public void displayQuestions(Questionnaire questionnaire) {

        this.questionnaire = questionnaire;
        System.out.println("No questions " + questionnaire.getExtraQuestions().size());
        introductionPane.setText(questionnaire.getIntroduction());
        for (int x = 0; x < questionnaire.getExtraQuestions().size(); x++) {
            //for (QuestionData questionObject : Questionnaire.getExtraQuestions()) {
            QuestionData questionObject = questionnaire.getExtraQuestions().get(x);
            //   JLabel question = new JLabel(questionObject.getQuestion(), JLabel.TRAILING);
            Dimension screen = getToolkit().getScreenSize();
            MouseActionEditorPane question = new MouseActionEditorPane(editMode, questionObject, x, this);

            question.setEditable(false);
            question.setMaximumSize(new Dimension(screen.width / 3, screen.height));
            question.setMinimumSize(new Dimension(screen.width / 3, 20));
            question.setPreferredSize(new Dimension(screen.width / 3, 20));
            question.setContentType("text/html");

            //   question.setPreferredSize(new Dimension(screen.width/3,screen.height));
            HTMLEditorKit kit = new HTMLEditorKit();
            question.setEditorKit(kit);
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("body {color: white; font-family:times; margin: 0px; background-color: black;font : 11px monaco;}");
            Document doc = kit.createDefaultDocument();

            question.setDocument(doc);
            question.setText("<body>" + questionObject.getQuestion() + "</body>");
            question.setBackground(Color.black);
            question.setForeground(Color.WHITE);
            //    question.setFont(new Font("Roman", Font.PLAIN, 16));
            questionsPanel.add(question);
            if (questionObject.getType().equals("closed_combo")) {
                ClosedComboPanel answerOptions = new ClosedComboPanel(questionObject.getOptions().toArray(), questionObject.isRequired());
                //   question.setLabelFor(answerOptions);
                questionsPanel.add(answerOptions);
                questionsMap.put(questionObject.getKey(), answerOptions);
            }
            if (questionObject.getType().equals("closed_buttons")) {
                ClosedButtonsPanel closedButtons = new ClosedButtonsPanel(questionObject.getOptions().toArray(), questionObject.isRequired());
                //   question.setLabelFor(closedButtons);
                questionsPanel.add(closedButtons);
                questionsMap.put(questionObject.getKey(), closedButtons);
            }
            if (questionObject.getType().equals("open")) {
                OpenQuestionPanel textInput = new OpenQuestionPanel(questionObject.isRequired());
                //    question.setLabelFor(textInput);
                questionsPanel.add(textInput);
                questionsMap.put(questionObject.getKey(), textInput);
            }

            if (questionObject.getType().equals("textarea")) {
                TextAreaPanel textArea = new TextAreaPanel(questionObject.isRequired());
                //    question.setLabelFor(textArea);
                questionsPanel.add(textArea);
                questionsMap.put(questionObject.getKey(), textArea);
            }
            if (questionObject.getType().equals("likert")) {
                LikertPanel likertScale = new LikertPanel(questionObject.getSize(), questionObject.getLeftText(), questionObject.getRightText(), questionObject.isRequired());
                //    question.setLabelFor(likertScale);
                questionsPanel.add(likertScale);
                questionsMap.put(questionObject.getKey(), likertScale);
            }

            if (questionObject.getType().equals("sem_diff")) {
                SemDiffPanel semanticDifferential = new SemDiffPanel(questionObject.getSize(), questionObject.getLeftText(), questionObject.getRightText(), questionObject.isRequired());
                //   question.setLabelFor(semanticDifferential);
                questionsPanel.add(semanticDifferential);
                questionsMap.put(questionObject.getKey(), semanticDifferential);
            }
            questionsPanel.setOpaque(true);
        }
        SpringUtilities.makeCompactGrid(questionsPanel,
                questionnaire.getExtraQuestions().size(), 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        for (String key : questionsMap.keySet()) {
            DisplayQuestion q = questionsMap.get(key);
            q.changeLabelSize(maxLabelSize);
        }

        introductionPane.setSelectionStart(0);
        introductionPane.setSelectionEnd(0);
        this.revalidate();
        this.repaint();
    }


    public void changeQuestion(QuestionData newQuestion, int pos) {
        if (pos >= 0 && pos < questionnaire.getExtraQuestions().size()) {
            questionnaire.getExtraQuestions().set(pos, newQuestion);
        } else {
            System.out.println("Adding question at pos " + pos);
            questionnaire.getExtraQuestions().add(newQuestion);
        }
        questionsPanel.removeAll();
        questionsPanel.setLayout(new SpringLayout());
        questionsPanel.setBackground(Color.black);
        questionsPanel.setForeground(Color.WHITE);
        displayQuestions(questionnaire);
        questionsPanel.revalidate();
        repaint();
        this.revalidate();
        this.repaint();

    }


    /*
   All the components are kept in a hash map, This provides a flexible way to have an unlimited nr of components
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


    class MouseActionEditorPane extends JEditorPane implements MouseListener {

        Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
        Border redBorder = BorderFactory.createLineBorder(Color.RED, 5);
        private int pos;
        private QuestionData question;
        private QuestionPanel parent;

        public MouseActionEditorPane(boolean editMode, QuestionData question, int pos, QuestionPanel parent) {
            if (editMode) {
                this.pos = pos;
                this.parent = parent;
                this.question = question;
                addMouseListener(this);

            }
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if(SwingUtilities.isLeftMouseButton(mouseEvent)) {
            QuestionEditFrame qEdit = new QuestionEditFrame(question, pos, parent);
            qEdit.setEnabled(true);
            qEdit.setVisible(true);
            }
            else if(SwingUtilities.isRightMouseButton(mouseEvent)) {

            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            setBorder(redBorder);
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            setBorder(blackBorder);
        }

    }
}



