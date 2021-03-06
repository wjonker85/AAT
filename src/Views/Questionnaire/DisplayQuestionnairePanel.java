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

package Views.Questionnaire;

import AAT.Util.SpringUtilities;
import DataStructures.Questionnaire.AbstractQuestion;
import DataStructures.Questionnaire.AbstractScaleQuestion;
import DataStructures.Questionnaire.OpenQuestion;
import DataStructures.Questionnaire.Questionnaire;
import IO.XMLReader;
import IO.XMLWriter;
import Model.AATModel;
import Views.Questionnaire.QuestionPanels.AbstractQuestionPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.StyledDocument;
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


    public int lastEditedPos = -1;
    public Dimension resolution;
    private JPanel questionsPanel;
    private Map<String, AbstractQuestionPanel> questionsMap = new HashMap<String, AbstractQuestionPanel>();
    private JScrollPane scrollPane1;
    private QuestionnaireModel questionnaireModel;
    private IntroductionEditorPanel introductionPanel;
    private Boolean editMode = false;
    private Questionnaire questionnaire;
    private QuestionEditFrame currentEditor = null;
    private Map<String, MouseActionEditorPane> qPanes;

    public DisplayQuestionnairePanel(final AATModel model, Dimension resolution) {
        if (model == null) {
            editMode = true;
        }
        this.resolution = resolution;
        qPanes = new HashMap<String, MouseActionEditorPane>();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.black);
        contentPanel.setForeground(Color.white);
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.black);
        this.setForeground(Color.white);

        questionsPanel = new JPanel(new SpringLayout());
        JPanel submitPanel = new JPanel();
        submitPanel.setBackground(Color.BLACK);
        submitPanel.setForeground(Color.WHITE);
        questionsPanel.setBackground(Color.black);
        questionsPanel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 30;
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        introductionPanel = new IntroductionEditorPanel();
        contentPanel.add(introductionPanel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipadx = 30;
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(questionsPanel);
        if (!editMode) {


            scrollPane1 = new JScrollPane(contentPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            JScrollBar vertical = scrollPane1.getVerticalScrollBar();
            scrollPane1.setPreferredSize(resolution);
            scrollPane1.setMinimumSize(resolution);
            scrollPane1.setMaximumSize(resolution);
            scrollPane1.setBorder(BorderFactory.createEmptyBorder());
            scrollPane1.setBackground(Color.black);
            scrollPane1.setForeground(Color.white);
            scrollPane1.getVerticalScrollBar().setUnitIncrement(16);

            InputMap im = vertical.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            im.put(KeyStroke.getKeyStroke("DOWN"), "positiveUnitIncrement");
            im.put(KeyStroke.getKeyStroke("UP"), "negativeUnitIncrement");

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.black);
            buttonPanel.setForeground(Color.white);

            JButton submitButton = new JButton("Submit");

            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    if (checkAnswered()) {
                        if (model != null) {
                            model.collectQuestionnaireAnswers(getResults());
                        }
                        setVisible(false);
                    } else {
                        repaint();
                    }
                }
            });
            buttonPanel.add(submitButton);
            contentPanel.add(buttonPanel);
            this.add(scrollPane1, new GridBagConstraints());
        } else {
            this.add(contentPanel, new GridBagConstraints());
        }
//        introductionPanel.setFocus();
       //    revalidate();
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
            JOptionPane.showMessageDialog(null, "There is already another question editor open, please close that one first.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void insertQuestionAt(int pos) {
        OpenQuestion openQuestion = new OpenQuestion();
        if (currentEditor == null) {
            questionnaireModel = new QuestionnaireModel(openQuestion, pos);
            questionnaireModel.addObserver(this);
            currentEditor = questionnaireModel.getEditFrame();
            currentEditor.setEnabled(true);
            currentEditor.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "There is already another question editor open, please close that one first.", "Error", JOptionPane.ERROR_MESSAGE);
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
        return count <= 0;
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
        int maxLabelWidth = getMaxLabelSize();
        introductionPanel.setText(questionnaire.getIntroduction());

        for (int x = 0; x < questionnaire.getExtraQuestions().size(); x++) {
            AbstractQuestion questionObject = questionnaire.getExtraQuestions().get(x);
            MouseActionEditorPane question = new MouseActionEditorPane(editMode, questionObject, x, this);
            if(editMode) {
                if (lastEditedPos == x) {
                    question.setLastEdited(true);
                } else {
                    question.setLastEdited(false);
                }
                qPanes.put(questionObject.getKey(), question);
            }
            questionsPanel.add(question);
            AbstractQuestionPanel newQuestionPanel = questionObject.Accept(new DisplayQuestionnaireVisitor(maxLabelWidth));
            questionsPanel.add(newQuestionPanel);
            questionsMap.put(questionObject.getKey(), newQuestionPanel);
            questionsPanel.setOpaque(false);
            questionsPanel.setBorder(null);
        }
    //    introductionPanel.setFocus();
        SpringUtilities.makeCompactGrid(questionsPanel,
                questionnaire.getExtraQuestions().size(), 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad


        revalidate();
        if (!editMode) {

            //    scrollPane1.getViewport().setViewPosition(new java.awt.Point(0, 0));
            //     ((JComponent)scrollPane1.getViewport().getComponentAt( 0, 0 ) ).scrollRectToVisible( new Rectangle() );
            introductionPanel.setFocus();
            //     introductionPanel.setText(questionnaire.getIntroduction());

            // revalidate();
        }
    }


    private int getMaxLabelSize() {
        int maxWidth = 0;
        for (AbstractQuestion q : questionnaire.getExtraQuestions()) {
            if (q instanceof AbstractScaleQuestion) {
                String l = ((AbstractScaleQuestion) q).getLeft();
                JLabel label = new JLabel(l);
                Font labelFont = label.getFont();
                String labelText = label.getText();
                int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
                if (stringWidth > maxWidth) {
                    maxWidth = stringWidth;
                }

            }
        }
        return maxWidth;
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

    public void changeQuestionPos(int posFrom, int newPos) {
        AbstractQuestion q = questionnaire.getExtraQuestions().get(posFrom);
        if (posFrom < newPos && (newPos < questionnaire.getExtraQuestions().size())) {
            questionnaire.getExtraQuestions().add(newPos + 1, q);
            questionnaire.getExtraQuestions().remove(posFrom);
            lastEditedPos = newPos;
        } else if (posFrom > newPos && (newPos >= 0)) {
            questionnaire.getExtraQuestions().add(newPos, q);
            questionnaire.getExtraQuestions().remove(posFrom + 1);
            lastEditedPos = newPos;
        } else {
            lastEditedPos = posFrom;
        }

        if (lastEditedPos < 0) {
            lastEditedPos = 0;
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

    public void ChangeIntroduction(String intro) {
        questionnaire.setIntroduction(intro);
        questionnaireModel = null;
        currentEditor = null;
        questionsPanel.removeAll();
        displayQuestions(questionnaire);
        introductionPanel.requestFocus();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o.toString().equals("submit") || o.toString().equals("insert")) {
            if (questionnaireModel.getPos() < questionnaire.getExtraQuestions().size()) {
                if (o.toString().equals("submit")) {
                    questionnaire.getExtraQuestions().set(questionnaireModel.getPos(), questionnaireModel.getNewQuestion());
                } else {
                    questionnaire.getExtraQuestions().add(questionnaireModel.getPos(), questionnaireModel.getNewQuestion());
                }
            } else {
                questionnaire.getExtraQuestions().add(questionnaireModel.getNewQuestion());
            }
            lastEditedPos = questionnaireModel.getPos();
            currentEditor.dispose();
            questionnaireModel.deleteObservers();
            questionnaireModel = null;
            currentEditor = null;
            questionsPanel.removeAll();
            displayQuestions(questionnaire);

        } else if (o.toString().equals("type changed")) {
            currentEditor.dispose();
            currentEditor = null;
            currentEditor = questionnaireModel.getEditFrame();
            currentEditor.setEnabled(true);
            currentEditor.setVisible(true);
        }
    }

    public DisplayQuestionnairePanel getInstance() {
        return this;
    }


    class IntroductionEditorPanel extends JPanel implements MouseListener {
        Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
        Border blackBorder = BorderFactory.createLineBorder(Color.black, 0);
        private JEditorPane editor;
        private JPanel buttonPanel;

        public IntroductionEditorPanel() {
            super();
            this.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.ipadx = 30;
            gbc.ipady = 10;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.weightx = 0.0;
            this.setBackground(Color.black);
            this.setBorder(null);
            this.setBackground(Color.black);
            editor = new JEditorPane();
            editor.setBackground(Color.black);
            editor.setForeground(Color.white);
            this.setBackground(Color.BLACK);
            editor.setContentType("text/html");
            HTMLEditorKit kit = new HTMLEditorKit();
            StyledDocument doc = (StyledDocument) kit.createDefaultDocument();
            editor.setDocument(doc);
            this.setMinimumSize(new Dimension((int) (0.9 * resolution.width), 50));
            this.setMaximumSize(new Dimension((int) (0.9 * resolution.width), resolution.height));
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("h2 {color: white; font-family:times; margin: 0px; background-color: black;font : 24px monaco;}");
            styleSheet.addRule("body {color:white; font-family:times; margin: 1px; background-color: black :font 24px monaco;}");
            styleSheet.addRule("h1 {color:white;background-color: black}");
            styleSheet.addRule("h3 {color:white; background-color: black}");
            styleSheet.addRule("p {color:white;background-color: black}");
            editor.setEditorKit(kit);
            editor.setBorder(null);

            if (editMode) {
                editor.addMouseListener(this);
                buttonPanel = new JPanel();
                buttonPanel.setBackground(Color.black);
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
                Color buttonBackColor = Color.BLACK;
                JButton editButton = new JButton(new ImageIcon(((new ImageIcon(
                        "icons/document-edit128x128.png").getImage()
                        .getScaledInstance(48, 48,
                                java.awt.Image.SCALE_SMOOTH)))));
                editButton.setPreferredSize(new Dimension(48, 48));
                editButton.setBackground(buttonBackColor);
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        IntroductionHTMLEditor htmlEditor = new IntroductionHTMLEditor(getInstance());
                        htmlEditor.Show(editor.getText());
                    }
                });
                buttonPanel.add(editButton);
                buttonPanel.addMouseListener(this);
                buttonPanel.setEnabled(true);
                buttonPanel.setVisible(true);
                buttonPanel.setOpaque(false);
                editButton.addMouseListener(this);
                this.add(buttonPanel);
            }
            //        this.add(editor,gbc);

            JScrollPane scrollPane = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setMinimumSize(new Dimension((int) (resolution.width * 0.7), 50));
            scrollPane.setPreferredSize(new Dimension((int) (resolution.width * 0.7), (int) (resolution.getHeight() * 0.3)));
            scrollPane.setBackground(Color.black);
            scrollPane.setBorder(null);
            if (editMode) {
                scrollPane.addMouseListener(this);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            }
            editor.setPreferredSize(new Dimension((int) (resolution.getWidth() * 0.8), Integer.MAX_VALUE));
            this.add(scrollPane, gbc);
        //    revalidate();
        //    JViewport jv = scrollPane.getViewport();
        //    jv.setViewPosition(new Point(0, 0));
        }

        public void setText(String text) {
            editor.setBackground(Color.black);
            if (!text.contains("<body>")) {
                text = "<body><h2>" + text + "</h2></body>";
            }

            HTMLEditorKit kit = new HTMLEditorKit();
            editor.setEditorKit(kit);
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("h2 {color: white; font-family:times; margin: 0px; background-color: black;font : 24px monaco;}");
            styleSheet.addRule("body {color:white; font-family:times; margin: 1px; background-color: black :font 24px monaco;}");
            styleSheet.addRule("h1 {color:white;background-color: black}");
            styleSheet.addRule("h3 {color:white; background-color: black}");
            styleSheet.addRule("p {color:white;background-color: black}");
            editor.setBackground(new java.awt.Color(0, 0, 0, 0));
            editor.setText(text);
            editor.setEditable(false);
         //   revalidate();
        //    repaint();

        }

        public void setFocus() {
         //   editor.requestFocus();
            editor.setSelectionStart(0);
            editor.setSelectionEnd(0);
           // scrollPane1.requestFocus();
        //    editor.setCaretPosition(0);
         //   ((JComponent)scrollPane1.getViewport().getComponentAt( 0, 0 ) ).scrollRectToVisible( new Rectangle() );
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

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


    class MouseActionEditorPane extends JPanel implements MouseListener {

        private final int pos;
        Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
        Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
        Border blueBorder = BorderFactory.createLineBorder(Color.blue, 2);
        JEditorPane questionPane;
        private JPanel buttonPanel;

        public MouseActionEditorPane(boolean editMode, final AbstractQuestion question, final int pos, final DisplayQuestionnairePanel parent) {
            this.pos = pos;
            JLayeredPane layers = new JLayeredPane();
            layers.setOpaque(false);
            layers.setBorder(null);
            this.setBorder(null);
            this.setOpaque(false);
            questionPane = new JEditorPane();
            questionPane.setContentType("text/html");
            questionPane.setOpaque(false);
            questionPane.setBorder(null);
            Dimension screen = getToolkit().getScreenSize();
            HTMLEditorKit kit = new HTMLEditorKit();
            questionPane.setEditorKit(kit);
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("h1 {color: white; font-family:times; margin: 0px; background-color: black;font : 11px monaco;}");
            questionPane.setDocument(kit.createDefaultDocument());
            this.setMaximumSize(new Dimension(screen.width / 3, screen.height));
            this.setMinimumSize(new Dimension(screen.width / 3, 25));
            this.setPreferredSize(new Dimension(screen.width / 3, 25));
            layers.setMaximumSize(new Dimension(screen.width / 3, screen.height));
            layers.setMinimumSize(new Dimension(screen.width / 3, 25));
            layers.setPreferredSize(new Dimension(screen.width / 3, 25));
            questionPane.setEditable(false);
            questionPane.setBackground(Color.black);
            questionPane.setMaximumSize(new Dimension(screen.width, screen.height));
            questionPane.setText("<body bgcolor=\"black\"><h1>" + question.getQuestion() + "</h1></body>");
            questionPane.setEnabled(true);
            questionPane.setVisible(true);
            layers.add(questionPane, JLayeredPane.DEFAULT_LAYER);
            questionPane.setBounds(5, 0, (screen.width / 3) - 10, 20);


            if (editMode) {
                questionPane.addMouseListener(this);
                addMouseListener(this);
                buttonPanel = new JPanel();
                buttonPanel.setOpaque(false);
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
                Color buttonBackColor = Color.BLACK;

                JButton editButton = new JButton(new ImageIcon(((new ImageIcon(
                        "icons/document-edit22x22.png").getImage()
                        .getScaledInstance(22, 22,
                                java.awt.Image.SCALE_SMOOTH)))));
                editButton.setPreferredSize(new Dimension(24, 24));
                editButton.setBackground(buttonBackColor);
                //new JButton("document-edit22x22.png");
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        questionnaireModel = new QuestionnaireModel(question, pos);
                        questionnaireModel.addObserver(parent);
                        currentEditor = questionnaireModel.getEditFrame();
                        currentEditor.setEnabled(true);
                        currentEditor.setVisible(true);
                        lastEditedPos = pos;
                    }
                });

                JButton upButton = new JButton(new ImageIcon(((new ImageIcon(
                        "icons/go-up22x22.png").getImage()
                        .getScaledInstance(22, 22,
                                java.awt.Image.SCALE_SMOOTH)))));
                upButton.setPreferredSize(new Dimension(24, 24));
                upButton.setBackground(buttonBackColor);
                upButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        parent.changeQuestionPos(pos, pos - 1);
                    }
                });


                JButton downButton = new JButton(new ImageIcon(((new ImageIcon(
                        "icons/go-down22x22.png").getImage()
                        .getScaledInstance(22, 22,
                                java.awt.Image.SCALE_SMOOTH)))));
                downButton.setBackground(buttonBackColor);
                downButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        parent.changeQuestionPos(pos, pos + 1);
                    }
                });
                downButton.setPreferredSize(new Dimension(24, 24));

                JButton addButton = new JButton(new ImageIcon(((new ImageIcon(
                        "icons/add22x22.png").getImage()
                        .getScaledInstance(22, 22,
                                java.awt.Image.SCALE_SMOOTH)))));
                addButton.setBackground(buttonBackColor);
                addButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        parent.insertQuestionAt(pos + 1); //Add question after this one
                    }
                });
                addButton.setPreferredSize(new Dimension(24, 24));


                JButton delButton = new JButton(new ImageIcon(((new ImageIcon(
                        "icons/delete22x22.png").getImage()
                        .getScaledInstance(22, 22,
                                java.awt.Image.SCALE_SMOOTH)))));
                delButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        parent.removeQuestion(pos);
                    }
                });
                delButton.setPreferredSize(new Dimension(24, 24));
                delButton.setBackground(buttonBackColor);

                buttonPanel.add(editButton);
                buttonPanel.add(upButton);
                buttonPanel.add(downButton);
                buttonPanel.add(addButton);
                buttonPanel.add(delButton);
                buttonPanel.setEnabled(true);
                buttonPanel.setVisible(false);
                buttonPanel.setBorder(null);
                layers.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
                buttonPanel.setBounds((screen.width / 3) - 135, 0, 125, 25);
                //   buttonPanel.setBounds(10,questionPane.getHeight()-20,125,30);
                buttonPanel.addMouseListener(this);
                editButton.addMouseListener(this);     //Add the mouselisteners to the buttons for the colored border.
                editButton.setToolTipText("Edit this question");
                upButton.addMouseListener(this);
                upButton.setToolTipText("Move this question one level up");
                downButton.addMouseListener(this);
                downButton.setToolTipText("Move this question one level down");
                delButton.addMouseListener(this);
                delButton.setToolTipText("Delete this question");
                addButton.addMouseListener(this);
                addButton.setToolTipText("Add a new question after this one");

            }
            this.add(layers);
        }

        //     @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }

        public void setFocus() {
            if(editMode) {
                questionPane.setSelectionStart(0);
                questionPane.setSelectionEnd(0);
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
            buttonPanel.setEnabled(true);
            buttonPanel.setVisible(true);
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            setBorder(blackBorder);
            buttonPanel.setVisible(false);
            if (pos == lastEditedPos) {
                setBorder(blueBorder);
            }
        }

        public void setLastEdited(boolean edited) {
            if (edited && editMode) {
                setBorder(blueBorder);
            } else {
                setBorder(null);
            }
        }
    }
}



