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
import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.OpenQuestion;
import DataStructures.Questionnaire.Questionnaire;
import IO.XMLReader;
import IO.XMLWriter;
import Model.AATModel;
import views.Questionnaire.QuestionPanels.AbstractQuestionPanel;

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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 1:15 PM
 * This is a panel that contains all the additional questions a researcher might be interested in.  These
 * questions come from the language file that is specified in the configuration. These questions are displayed for the
 * actual test is started. But only if there are any.
 * This panel can also be used for editing questions.
 */
public class DisplayQuestionnairePanel extends JPanel implements Observer {


    private JPanel questionsPanel;
    private Map<String, AbstractQuestionPanel> questionsMap = new HashMap<String, AbstractQuestionPanel>();
    private QuestionnaireModel questionnaireModel;

    private JScrollPane scrollPane;
    private JTextArea introductionPane;
    private Boolean editMode = false;
    private Questionnaire questionnaire;
    private int maxLabelSize = 0;
    private JScrollBar vertical;
    private QuestionEditFrame currentEditor = null;
    private Map<String,MouseActionEditorPane> qPanes;

    public DisplayQuestionnairePanel(final AATModel model, Dimension resolution) {
        if (model == null) {
            editMode = true;
        }
        JPanel mainPanel = new JPanel();
        JPanel contentPanel = new JPanel();
        qPanes = new HashMap<String, MouseActionEditorPane>();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.black);
        contentPanel.setForeground(Color.white);
        mainPanel.setBackground(Color.black);
        mainPanel.setForeground(Color.white);
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));    //TODO even naar kijken
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.black);
        this.setForeground(Color.white);

        if (editMode) {
            JPanel addButtonPanel = new JPanel();
            JButton button = new JButton("Add question");
            addButtonPanel.add(button);
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
        }

        if(!editMode) {
        scrollPane = new JScrollPane(contentPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
        else {
            scrollPane = new JScrollPane(contentPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        vertical = scrollPane.getVerticalScrollBar();

        if (!editMode) {
            scrollPane.setPreferredSize(resolution);
            scrollPane.setMinimumSize(resolution);
            scrollPane.setMaximumSize(resolution);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setBackground(Color.black);
            scrollPane.setForeground(Color.white);

            InputMap im = vertical.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            im.put(KeyStroke.getKeyStroke("DOWN"), "positiveUnitIncrement");
            im.put(KeyStroke.getKeyStroke("UP"), "negativeUnitIncrement");
            this.add(scrollPane, new GridBagConstraints());
        } else {
            scrollPane.setPreferredSize(resolution);
            scrollPane.setMinimumSize(resolution);
            scrollPane.setMaximumSize(resolution);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setBackground(Color.black);
            scrollPane.setForeground(Color.white);

            this.add(contentPanel, new GridBagConstraints());
        }
    }


    public void addQuestionAction() {
        if (questionnaire == null) {
            questionnaire = new Questionnaire(new ArrayList<AbstractQuestion>(), "");
        }
        OpenQuestion openQuestion = new OpenQuestion();
        if (currentEditor == null) {
            questionnaireModel = new QuestionnaireModel(openQuestion, questionnaire.getExtraQuestions().size() + 1);
            questionnaireModel.addObserver(this);
            currentEditor = questionnaireModel.getEditFrame();
            currentEditor.setEnabled(true);
            currentEditor.setVisible(true);
        } else {
            JOptionPane.showConfirmDialog(null, "There is already another question editor open, please close that one first.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkAnswered() {
        int count = 0;
        for (String key : questionsMap.keySet()) {
            AbstractQuestionPanel q = questionsMap.get(key);
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
        Questionnaire q = XMLReader.getQuestionnaire(file);
        displayQuestions(q);
    }

    /**
     * Displays every question Object in the ArrayList<AAT.AbstractQuestion> A question can be open, have a textField on the screen
     * or closed and have a combobox with answers
     *
     * @param questionnaire The optional Questionnaire received from the model
     */
    public void displayQuestions(Questionnaire questionnaire) {

        this.questionnaire = questionnaire;
        questionsMap = new HashMap<String, AbstractQuestionPanel>();
        qPanes = new HashMap<String, MouseActionEditorPane>();
        System.out.println("No questions " + questionnaire.getExtraQuestions().size());
        introductionPane.setText(questionnaire.getIntroduction());
        for (int x = 0; x < questionnaire.getExtraQuestions().size(); x++) {
            AbstractQuestion questionObject = questionnaire.getExtraQuestions().get(x);
            MouseActionEditorPane question = new MouseActionEditorPane(editMode, questionObject, x, this);
            questionsPanel.add(question);
            qPanes.put(questionObject.getKey(), question);
            AbstractQuestionPanel newQuestionPanel = questionObject.Accept(new DisplayQuestionnaireVisitor());
            questionsPanel.add(newQuestionPanel);
            questionsMap.put(questionObject.getKey(), newQuestionPanel);
            questionsPanel.setOpaque(true);
        }
        SpringUtilities.makeCompactGrid(questionsPanel,
                questionnaire.getExtraQuestions().size(), 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        for (String key : questionsMap.keySet()) {
            AbstractQuestionPanel q = questionsMap.get(key);
            q.changeLabelSize(maxLabelSize);
        }
        if (!editMode) {
            introductionPane.setSelectionStart(0);
            introductionPane.setSelectionEnd(0);
        }
    }

    /*
   All the components are kept in a hash map, This provides a flexible way to have an unlimited nr of components
   and keep track of them
    */
    private HashMap<String, String> getResults() {
        HashMap<String, String> results = new HashMap<String, String>();
        for (String key : questionsMap.keySet()) {
            AbstractQuestionPanel c = questionsMap.get(key);
            String input = c.getValue();
            if (input.equals("")) {
                input = "N/A";  //Replace the empty input with "N/A"
            }
            results.put(key, input);
        }
        return results;
    }

    public void changeQuestionPos(int posFrom, int newPos)  {
        System.out.println("Change pos from "+posFrom+" to "+newPos);
        AbstractQuestion q = questionnaire.getExtraQuestions().get(posFrom);
        if(posFrom<newPos && (newPos+1 <questionnaire.getExtraQuestions().size())) {
            questionnaire.getExtraQuestions().add(newPos+1,q);
            questionnaire.getExtraQuestions().remove(posFrom);
        }
        else if(posFrom > newPos && (newPos>=0 && posFrom+1 < questionnaire.getExtraQuestions().size())) {
            questionnaire.getExtraQuestions().add(newPos,q);
            questionnaire.getExtraQuestions().remove(posFrom+1);
        }
        questionnaireModel = null;
        currentEditor = null;
        questionsPanel.removeAll();
        displayQuestions(questionnaire);
        qPanes.get(q.getKey()).setFocus();
    }

    public void removeQuestion(int pos) {
        questionnaire.getExtraQuestions().remove(pos);
        questionnaireModel = null;
        currentEditor = null;
        questionsPanel.removeAll();
        displayQuestions(questionnaire);
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println("Update");
        if (o.toString().equals("submit")) {

            System.out.println("Submit");
            //  questionnaire.getExtraQuestions().remove(questionnaireModel.getPos());
            if (questionnaireModel.getPos() < questionnaire.getExtraQuestions().size()) {
                questionnaire.getExtraQuestions().set(questionnaireModel.getPos(), questionnaireModel.getNewQuestion());
            } else {
                questionnaire.getExtraQuestions().add(questionnaireModel.getNewQuestion());
            }
            currentEditor.dispose();
            questionnaireModel.deleteObservers();
            questionnaireModel = null;
            currentEditor = null;
            questionsPanel.removeAll();
            displayQuestions(questionnaire);
            qPanes.get(questionnaire.getExtraQuestions().get(questionnaireModel.getPos()).getKey()).setFocus();
        //    revalidate();
        //    repaint();
        }

        if (o.toString().equals("type changed")) {
            currentEditor.dispose();
            currentEditor = null;
            currentEditor = questionnaireModel.getEditFrame();
            currentEditor.setEnabled(true);
            currentEditor.setVisible(true);
        }
    }

    class MouseActionEditorPane extends JPanel implements MouseListener {

        Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
        Border redBorder = BorderFactory.createLineBorder(Color.RED, 5);
        Border greenBorder = BorderFactory.createLineBorder(Color.green,5);
        private JPanel buttonPanel;
        JEditorPane questionPane;

        public MouseActionEditorPane(boolean editMode,final AbstractQuestion question, final int pos, final DisplayQuestionnairePanel parent) {
            JLayeredPane layers = new JLayeredPane();
            this.setBackground(Color.BLACK);
            this.setForeground(Color.white);
            questionPane = new JEditorPane();
            questionPane.setContentType("text/html");
            Dimension screen = getToolkit().getScreenSize();
            HTMLEditorKit kit = new HTMLEditorKit();
            questionPane.setEditorKit(kit);
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("p {color: white; font-family:times; margin: 0px; background-color: black;font : 11px monaco;}");
            questionPane.setDocument(kit.createDefaultDocument());
            this.setMaximumSize(new Dimension(screen.width / 3, screen.height));
            this.setMinimumSize(new Dimension(screen.width / 3, 20));
            this.setPreferredSize(new Dimension(screen.width / 3, 20));
            layers.setMaximumSize(new Dimension(screen.width / 3, screen.height));
            layers.setMinimumSize(new Dimension(screen.width / 3, 20));
            layers.setPreferredSize(new Dimension(screen.width / 3, 20));
            questionPane.setBackground(Color.BLACK);
            questionPane.setForeground(Color.white);
            questionPane.setEditable(false);
            questionPane.setMaximumSize(new Dimension(screen.width, screen.height));
            questionPane.setText("<p>" + question.getQuestion() + "</p>");
            questionPane.setEnabled(true);
            questionPane.setVisible(true);
            layers.add(questionPane, JLayeredPane.DEFAULT_LAYER);
            questionPane.setBounds(5,0,(screen.width/3)-10,20);
            questionPane.addMouseListener(this);
            buttonPanel = new JPanel();

            JButton editButton = new JButton("Edit");
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                        questionnaireModel = new QuestionnaireModel(question, pos);
                        questionnaireModel.addObserver(parent);
                        currentEditor = questionnaireModel.getEditFrame();
                        currentEditor.setEnabled(true);
                        currentEditor.setVisible(true);
                }
            });

            JButton upButton = new JButton("Up");
            upButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                      parent.changeQuestionPos(pos,pos-1);
                }
            });

            JButton downButton = new JButton("Down");
            downButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    parent.changeQuestionPos(pos,pos+1);
                }
            });

            JButton delButton = new JButton("Del");
            delButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    parent.removeQuestion(pos);
                }
            });

            buttonPanel.add(editButton);
            buttonPanel.add(upButton);
            buttonPanel.add(downButton);
            buttonPanel.add(delButton);
            buttonPanel.setEnabled(true);
            buttonPanel.setVisible(false);
            layers.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
            buttonPanel.setBounds((screen.width/3)-300,0,300,20);
            buttonPanel.addMouseListener(this);
            editButton.addMouseListener(this);     //Add the mouselisteners to the buttons for the colored border.
            upButton.addMouseListener(this);
            downButton.addMouseListener(this);
            delButton.addMouseListener(this);

            if (editMode) {
                addMouseListener(this);
            }
            this.add(layers);
        }

   //     @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            }

        public void setFocus() {
            questionPane.setSelectionStart(0);
            questionPane.setSelectionEnd(0);
            setBorder(greenBorder);
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
            buttonPanel.setEnabled(true);
            buttonPanel.setVisible(true);
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            setBorder(blackBorder);
            buttonPanel.setVisible(false);
        }

    }
}



