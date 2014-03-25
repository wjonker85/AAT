package views.ConfigurationBuilder;

import AAT.Util.ExtensionFileFilter;
import AAT.Util.FileUtils;
import AAT.Util.SpringUtilities;
import AAT.Util.TitledSeparator;
import AAT.Configuration.Validation.FalseConfigException;
import Controller.JoystickController;
import AAT.Configuration.TestConfiguration;
import IO.ConfigFileReader;
import IO.ConfigWriter;
import Model.AATModel;
import views.HTMLEditPanel;
import views.Questionnaire.DisplayQuestionnairePanel;
import views.TestFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by marcel on 3/16/14.
 */
public class ConfigBuilderPanel extends JPanel implements  Observer{

    private JTextField inputPushTag, inputPullTag, inputAffDir, inputNeutralDir, inputLangFile, inputPrDir, inputQuestionFile, inputPractRepeat;
    private JTextField inputTrials, inputBreak;
    private JTextField inputAffectRatioPush, inputAffectRatioPull, inputNeutralRatioPush, inputNeutralRatioPull, inputTestRatioA, inputTestRatioN, inputTrialSize;
    private JButton inputPushColor, inputPullColor, inputPracticeFill, selectQButton;
    private JTextField inputBorderSize;
    private JTextField inputStepSize, inputDataStepSize;
    private JComboBox inputQuestions;
    private JCheckBox inputBoxplot, inputColoredBorder, inputHasPractice, inputBuiltinPractice;
    private int pullColor, pushColor, prFillColor;
    private File workingDir = new File("");
    private File nDir, aDir, pDir;
    private File currentConfig;
    private AATModel model;
    private JoystickController joystick;
    private TestFrame testFrame;
    private JLabel pullColorL, pushColorL, borderSizeL, pushTagL, pullTagL, askBuiltinPractL, practFillL, prDirL, practL, selectQL;
    private JButton prDirButton;
    private String practRepeatValue = "3";
    private JTextField inputMaxSizeP, inputImageSizeP;
    private Boolean newTest;
    private int test_id = 1;

    private DisplayQuestionnairePanel displayQuestionnairePanel;
    private JScrollPane questionPane;
    private HTMLEditPanel htmlEditPanel;
    private JTabbedPane tabbedPane;
    private ImageSelectionPanel imageSelectionPanel;

    public ConfigBuilderPanel() {
        super(new SpringLayout());

        nDir = new File("");
        aDir = new File("");
        pDir = new File("");
        currentConfig = new File("");
        newTest = true;

        JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
        final JButton newButton = new JButton(new ImageIcon("document-new.png"));
        newButton.setToolTipText("Create a new AAT Config file");
        newButton.setPreferredSize(new Dimension(48, 48));


        JButton openButton = new JButton(new ImageIcon("document-open.png"));
        openButton.setToolTipText("Open an AAT Config file");
        openButton.setPreferredSize(new Dimension(48, 48));

        final JButton saveAsButton = new JButton(new ImageIcon("document-save-as.png"));
        saveAsButton.setPreferredSize(new Dimension(48, 48));
        saveAsButton.setToolTipText("Save AAT Config file as ...");
        saveAsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = fileSaveDialog("AATConfig");
                if (file != null) {
                    saveAction(file);
                    currentConfig = file;
                    workingDir = file.getParentFile();
                    htmlEditPanel.save();
                    JOptionPane.showMessageDialog(null,
                            "AAT Config file saved.");
                }
            }
        });

        saveAsButton.setEnabled(false);

        final JButton saveButton = new JButton(new ImageIcon("document-save.png"));
        saveButton.setPreferredSize(new Dimension(48, 48));
        saveButton.setToolTipText("Save AAT Config file");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentConfig.exists()) {
                    saveAction(currentConfig);
                    displayQuestionnairePanel.saveQuestionnaire(new File(inputQuestionFile.getText()));
                    JOptionPane.showMessageDialog(null,
                            "AAT Config file saved.");
                } else {
                    File file = fileSaveDialog("AATConfig");
                    if (file != null) {
                        saveAction(file);
                        displayQuestionnairePanel.saveQuestionnaire(new File(inputQuestionFile.getText()));
                        currentConfig = file;
                        workingDir = file.getParentFile();
                        JOptionPane.showMessageDialog(null,
                                "AAT Config file saved.");
                    }


                }
                htmlEditPanel.save();  //Save the language file.
            }
        });

        saveButton.setEnabled(false);

        final JButton tryButton = new JButton(new ImageIcon("playGray.png"));
        tryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    File testFile = new File(workingDir.toString() + File.separator + "AATConfig.temp");
                    model = new AATModel();
                    model.addObserver(getInstance());
                    saveAction(testFile);
                    displayQuestionnairePanel.saveQuestionnaire(new File(inputQuestionFile.getText()));
                    model.loadNewAAT(testFile);     //Only start when config is valid
                    joystick = new JoystickController(model);
                    joystick.start(); //Start joystick Thread
                    testFrame = new TestFrame(model);
                    model.addObserver(testFrame);
                    model.addObserver(joystick);
                    model.startTest(false); //start test without saving data
                    testFile.delete();    //Delete the test config file


                } catch (FalseConfigException e) {
                    JOptionPane.showMessageDialog(null,
                            e.getMessage(),
                            "Configuration error",
                            JOptionPane.ERROR_MESSAGE);
                    System.out.println("Configuration error: " + e.getMessage());
                }
            }
        });
        tryButton.setToolTipText("Try the current configuration");
        tryButton.setPreferredSize(new Dimension(48, 48));
        tryButton.setEnabled(false);



        toolbar.add(newButton);
        toolbar.add(openButton);
        toolbar.add(saveAsButton);
        toolbar.add(saveButton);
        toolbar.add(tryButton);
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        final JButton addButton = new JButton(new ImageIcon("list-add.png"));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displayQuestionnairePanel.addQuestionAction();
            }
        });
        toolbar.add(addButton);
        this.add(toolbar);

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                if(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).equals("Questionnaire")) {
                    addButton.setEnabled(true);
                }
                else {
                    addButton.setEnabled(false);
                }

            }
        });
        ImageIcon icon = null;
        final JPanel mainPanel = createMainPanel();
        mainPanel.setEnabled(false);
        mainPanel.setVisible(false);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        tabbedPane.addTab("AAT configuration", icon, scrollPane,
                "Sets the main configuration of the test");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        add(tabbedPane);

        boolean hasPracticeImages = false;
        if(!inputBuiltinPractice.isSelected() && inputHasPractice.isSelected())  {
            hasPracticeImages = true;
        }
        imageSelectionPanel = new ImageSelectionPanel(aDir,nDir,pDir,hasPracticeImages,newTest);
        JScrollPane contentPane = new JScrollPane(imageSelectionPanel);
        contentPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        //   JPanel images = createImageListTable(getImages(new File("/home/marcel/AAT/AAT/images/Affective/")),getImages(new File("/home/marcel/AAT/AAT/images/Neutral/")));

        tabbedPane.addTab("Images", contentPane);

        htmlEditPanel = new HTMLEditPanel();

        JScrollPane HtmlEditPane = new JScrollPane((htmlEditPanel));
        HtmlEditPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        HtmlEditPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        tabbedPane.addTab("Language File", HtmlEditPane);

        Rectangle r = this.getBounds();
        displayQuestionnairePanel = new DisplayQuestionnairePanel(null,new Dimension(r.width,r.height)); //without reference to model.
        questionPane = new JScrollPane(displayQuestionnairePanel);
        tabbedPane.addTab("Questionnaire", questionPane);

        SpringUtilities.makeCompactGrid(this,
                2, 1, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = ConfigOpenDialog();
                if (file != null) {
                    workingDir = file.getParentFile();
                    LoadConfig(file);
                    currentConfig = file;
                    saveAsButton.setEnabled(true);
                    saveButton.setEnabled(true);
                    tryButton.setEnabled(true);
                    mainPanel.setEnabled(true);
                    mainPanel.setVisible(true); //Show and enable all the buttons and options.
                    repaint();
                }
            }
        });


        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = fileSaveDialog("AATConfig");
                if (file != null) {
                    saveAction(file);
                    currentConfig = file;
                    workingDir = file.getParentFile();
                    saveAsButton.setEnabled(true);
                    saveButton.setEnabled(true);
                    tryButton.setEnabled(true);
                    mainPanel.setEnabled(true);
                    mainPanel.setVisible(true); //Show and enable all the buttons and options.
                }
            }
        });
    }


    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new SpringLayout());
        panel.add(Box.createVerticalStrut(10));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new TitledSeparator("Main options", 0));
        panel.add(new TitledSeparator("", -1));
        panel.add(Box.createVerticalStrut(5));
        panel.add(Box.createVerticalStrut(5));
        JPanel affDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel neutrDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel prDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton affDirButton = new JButton("Choose Affect Directory");
        JButton neutrDirButton = new JButton("Choose Neutral Directory");
        prDirButton = new JButton("Choose Practice Directory");
        JLabel affDirL = new JLabel("Choose the directory that contains the affective images");
        JLabel neutrDirL = new JLabel("Choose the directory that contains the neutral images");

        inputAffDir = new JTextField();
        inputAffDir.setEditable(false);
        inputAffDir.setPreferredSize(new Dimension(200, 20));
        inputNeutralDir = new JTextField();
        inputNeutralDir.setEditable(false);
        inputNeutralDir.setPreferredSize(new Dimension(200, 20));
        inputPrDir = new JTextField("");
        inputPrDir.setEditable(false);
        inputPrDir.setPreferredSize(new Dimension(200, 20));
        affDirPanel.add(inputAffDir);
        affDirPanel.add(affDirButton);
        affDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = getDirectory();
                if (file != null) {
                    aDir = file.getAbsoluteFile();
                 //   try {
                        inputAffDir.setText(FileUtils.getRelativePath(workingDir, aDir));
                  //  } catch (IOException e) {
                 //       inputAffDir.setText(file.getName());
                 //   }
                     imageSelectionPanel.setaDir(aDir);

                } else {
                    inputAffDir.setText("");
                }
            }
        });

        neutrDirPanel.add(inputNeutralDir);
        neutrDirPanel.add(neutrDirButton);
        neutrDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = getDirectory();

                if (file != null) {
                    nDir = file.getAbsoluteFile();
                //    try {
                        inputNeutralDir.setText(FileUtils.getRelativePath(workingDir, nDir));

                 //   } catch (IOException e) {
                 //       System.out.println(e.getMessage());
                  //      inputNeutralDir.setText(file.getName());
                  //  }


                    System.out.println("Working dir: " + workingDir.getAbsoluteFile());
                    System.out.println("neutral dir: " + nDir.getAbsoluteFile());
                    System.out.println("Relative path: ");

                    imageSelectionPanel.setnDir(nDir);
                } else {
                    inputNeutralDir.setText("");
                }
            }
        });

        panel.add(affDirL);
        panel.add(affDirPanel);
        panel.add(neutrDirL);
        panel.add(neutrDirPanel);

        JLabel langFL = new JLabel("Select the language xml file");
        inputLangFile = new JTextField();
        inputLangFile.setEditable(false);
        inputLangFile.setPreferredSize(new Dimension(200, 20));
        JButton setLangFile = new JButton("Choose language file");
        setLangFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = fileOpenDialog();
                if (file != null) {
                    if (file.exists()) {
                        inputLangFile.setText(file.getName());
                        htmlEditPanel.setDocument(file);
                    } else {
                        int reply = JOptionPane.showConfirmDialog(null, "Do you want to create the file: " + file.getName() + " ?", "Create new language file", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            inputLangFile.setText(file.getName());
                            htmlEditPanel.setDocument(file);
                        } else {
                            inputLangFile.setText("");
                            System.exit(0);
                        }
                    }
                } else {
                    inputLangFile.setText("");
                }
            }
        });
        JPanel langFP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(langFL);
        langFP.add(inputLangFile);
        langFP.add(setLangFile);
        langFP.add(Box.createHorizontalBox());
        panel.add(langFP);
        JPanel trialP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel trialsL = new JLabel("Number of trials");
        inputTrials = new JFormattedTextField(NumberFormat.getInstance());
        inputTrials.setText("4");
        inputTrials.setPreferredSize(new Dimension(50, 20));
        panel.add(trialsL);
        trialP.add(inputTrials);
        trialP.add(Box.createHorizontalBox());
        panel.add(trialP);

        JPanel breakP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel breakL = new JLabel("When will there be a break? Set to 0 when you don't want a break");
        inputBreak = new JFormattedTextField(NumberFormat.getInstance());
        inputBreak.setText("2");
        inputBreak.setPreferredSize(new Dimension(50, 20));
        panel.add(breakL);
        breakP.add(inputBreak);
        breakP.add(Box.createHorizontalBox());
        panel.add(breakP);
        //TODO

        panel.add(Box.createVerticalStrut(10));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new TitledSeparator("Practice options", 0));
        panel.add(new TitledSeparator("", -1));
        panel.add(Box.createVerticalStrut(5));
        panel.add(Box.createVerticalStrut(5));
        prFillColor = Integer.parseInt("FFDEDE", 16);
        prDirL = new JLabel("Select the directory containing the practice images");
        practL = new JLabel("How many times should the practice images be repeated?");
        practFillL = new JLabel("Set the fill color for the generated practice images");
        JLabel askPractL = new JLabel("Do you want to start the test with a practice?");
        JPanel askPractP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputHasPractice = new JCheckBox();
        inputHasPractice.setSelected(true);
        inputHasPractice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!inputHasPractice.isSelected()) {
                    disablePracticeAction();
                } else {
                    enablePracticeAction();
                }
            }
        });
        panel.add(askPractL);
        askPractP.add(inputHasPractice);
        askPractP.add(Box.createVerticalBox());
        panel.add(askPractP);

        askBuiltinPractL = new JLabel("<html>Do you want the test to self-generate practice images? <br>" +
                "Needs colored borders to be enabled</html>");
        JPanel askBuitltinPractP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputBuiltinPractice = new JCheckBox();
        inputBuiltinPractice.setSelected(true);
        inputBuiltinPractice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!inputBuiltinPractice.isSelected()) {
                    disableBuiltinPracticeAction();
                } else {
                    enableBuiltinPracticeAction();
                }
            }
        });
        panel.add(askBuiltinPractL);
        askBuitltinPractP.add(inputBuiltinPractice);
        askBuitltinPractP.add(Box.createVerticalBox());
        panel.add(askBuitltinPractP);

        JPanel practFillP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPracticeFill = new JButton();
        inputPracticeFill.setBackground(new Color(prFillColor));
        inputPracticeFill.setPreferredSize(new Dimension(100, 20));
        inputPracticeFill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Color c = JColorChooser.showDialog(
                        null, "Choose a color...", getBackground());
                if (c != null) {
                    inputPracticeFill.setBackground(c);
                    prFillColor = c.getRGB();
                }
            }
        });
        panel.add(practFillL);
        practFillP.add(inputPracticeFill);
        practFillP.add(Box.createHorizontalBox());
        panel.add(practFillP);

        prDirPanel.add(inputPrDir);
        prDirPanel.add(prDirButton);
        prDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = getDirectory();
                if (file != null) {
                    pDir = file.getAbsoluteFile();
                 //   try {
                        inputPrDir.setText(FileUtils.getRelativePath(workingDir, pDir));
                 //   } catch (IOException e) {
                 //       inputPrDir.setText(file.getName());
                 //   }
                    imageSelectionPanel.setHasPracticeImages(true);
                    imageSelectionPanel.setpDir(pDir);
                    inputPracticeFill.setEnabled(false);
                    inputPracticeFill.setBackground(Color.lightGray);
                } else {
                    inputNeutralDir.setText("");
                    inputPracticeFill.setEnabled(true);
                    inputPracticeFill.setBackground(new Color(prFillColor));
                }
            }
        });
        panel.add(prDirL);
        panel.add(prDirPanel);

        //  final JLabel practL = new JLabel("How many times should the practice images be repeated?");
        JPanel practP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPractRepeat = new JFormattedTextField(NumberFormat.getInstance());
        inputPractRepeat.setText(practRepeatValue);
        inputPractRepeat.setPreferredSize(new Dimension(50, 20));
        panel.add(practL);
        practP.add(inputPractRepeat);
        practP.add(Box.createVerticalBox());
        panel.add(practP);


        panel.add(Box.createVerticalStrut(10));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new TitledSeparator("Border & Color options", 0));
        panel.add(new TitledSeparator("", -1));
        panel.add(Box.createVerticalStrut(5));
        panel.add(Box.createVerticalStrut(5));
        JLabel coloredBorderL = new JLabel("Do you want to automatically create a colored border around the images?");
        inputColoredBorder = new JCheckBox();
        inputColoredBorder.setPreferredSize(new Dimension(20, 20));
        inputColoredBorder.setSelected(true);

        panel.add(coloredBorderL);
        panel.add(inputColoredBorder);

        pushColorL = new JLabel("Set the border color for the push images");
        pullColorL = new JLabel("Set the border color for the pull images");

        //    inputPushColor = new JTextField("F5FE02");
        //   inputPullColor = new JTextField("00A4E7");
        pushColor = Integer.parseInt("F5FE02", 16);
        pullColor = Integer.parseInt("00A4E7", 16);

        JPanel pushColorP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPushColor = new JButton();
        inputPushColor.setBackground(new Color(pushColor));
        inputPushColor.setPreferredSize(new Dimension(100, 20));
        inputPushColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Color c = JColorChooser.showDialog(
                        null, "Choose a color...", getBackground());
                if (c != null) {
                    inputPushColor.setBackground(c);
                    pushColor = c.getRGB();
                }

            }
        });
        JPanel pullColorP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPullColor = new JButton();
        inputPullColor.setBackground(new Color(pullColor));
        inputPullColor.setPreferredSize(new Dimension(100, 20));
        inputPullColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Color c = JColorChooser.showDialog(
                        null, "Choose a color...", getBackground());
                if (c != null) {
                    inputPullColor.setBackground(c);
                    pullColor = c.getRGB();
                }
            }
        });
        borderSizeL = new JLabel("Set the size of the border");
        JPanel borderSizeP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputBorderSize = new JFormattedTextField(NumberFormat.getInstance());
        inputBorderSize.setPreferredSize(new Dimension(50, 20));
        inputBorderSize.setText("20");
        panel.add(borderSizeL);
        borderSizeP.add(inputBorderSize);
        borderSizeP.add(Box.createHorizontalBox());
        panel.add(borderSizeP);


        panel.add(pushColorL);
        pushColorP.add(inputPushColor);
        pushColorP.add(Box.createHorizontalBox());
        panel.add(pushColorP);
        panel.add(pullColorL);
        pullColorP.add(inputPullColor);
        pullColorP.add(Box.createHorizontalBox());
        panel.add(pullColorP);

        panel.add(Box.createVerticalStrut(10));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new TitledSeparator("Image filename tags (Only when Colored Borders isn't selected)", 0));
        panel.add(new TitledSeparator("", -1));
        panel.add(Box.createVerticalStrut(5));
        panel.add(Box.createVerticalStrut(5));
        pushTagL = new JLabel("<html>Select the tag by which the test can know which image is a push image<br>" +
                "The image filename needs to contain this tag</html>");
        pullTagL = new JLabel("<html>Select the tag by which the test can know which image is a pull image<br>" +
                "The image filename needs to contain this tag</html>");
        JPanel pushTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pullTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPushTag = new JTextField("push");
        inputPushTag.setPreferredSize(new Dimension(100, 20));
        inputPushTag.setEnabled(false);
        inputPullTag = new JTextField("pull");
        inputPullTag.setPreferredSize(new Dimension(100, 20));
        inputPullTag.setEnabled(false);
        panel.add(pullTagL);
        pullTP.add(inputPullTag);
        pullTP.add(Box.createHorizontalBox());
        panel.add(pullTP);
        panel.add(pushTagL);
        pushTP.add(inputPushTag);
        pushTP.add(Box.createHorizontalBox());
        panel.add(pushTP);

        inputColoredBorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (inputColoredBorder.isSelected()) {
                    enableColoredBorderAction();
                } else {
                    disableColoredBorderAction();
                }
            }
        });

        panel.add(Box.createVerticalStrut(10));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new TitledSeparator("Boxplot & Questionnaire", 0));
        panel.add(new TitledSeparator("", -1));
        panel.add(Box.createVerticalStrut(5));
        panel.add(Box.createVerticalStrut(5));

        JPanel comboP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel displayQL = new JLabel("When do you want to display a Questionnaire?");
        inputQuestions = new JComboBox(new Object[]{"None", "Before", "After"});
        inputQuestions.setPreferredSize(new Dimension(100, 20));
        inputQuestions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (inputQuestions.getSelectedItem().equals("None")) {
                    inputQuestionFile.setEnabled(false);
                    selectQButton.setEnabled(false);
                    selectQL.setEnabled(false);

                } else {
                    selectQL.setEnabled(true);
                    inputQuestionFile.setEnabled(true);
                    selectQButton.setEnabled(true);
                }
            }
        });
        panel.add(displayQL);
        comboP.add(inputQuestions);
        comboP.add(Box.createHorizontalBox());
        panel.add(comboP);

        selectQL = new JLabel("Select the xml file that contains the Questionnaire");
        selectQL.setEnabled(false);
        JPanel selectQPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputQuestionFile = new JTextField();
        inputQuestionFile.setPreferredSize(new Dimension(200, 20));
        inputQuestionFile.setEditable(false);
        inputQuestionFile.setEnabled(false);
        selectQButton = new JButton("Choose Questionnaire file");
        selectQButton.setEnabled(false);
        selectQPanel.add(inputQuestionFile);
        selectQPanel.add(selectQButton);
        selectQButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = fileOpenDialog();
                if (file != null) {
                    inputQuestionFile.setText(file.getName());
                    Rectangle r = getBounds();
                    displayQuestionnairePanel = new DisplayQuestionnairePanel(null,new Dimension(r.width,r.height));
                    displayQuestionnairePanel.displayQuestions(file);
                    questionPane = new JScrollPane((displayQuestionnairePanel));
                    tabbedPane.remove(tabbedPane.indexOfTab("Questionnaire"));
                    tabbedPane.addTab("Questionnaire", questionPane);

                } else {
                    inputQuestionFile.setText("");
                }
            }
        });
        panel.add(selectQL);
        panel.add(selectQPanel);
        inputQuestionFile.setEnabled(false);
        JLabel boxPlotL = new JLabel("Do you want the test to show a BoxPlot at the end?");
        inputBoxplot = new JCheckBox();
        inputBoxplot.setPreferredSize(new Dimension(20, 20));
        inputBoxplot.setSelected(true);
        panel.add(boxPlotL);
        panel.add(inputBoxplot);

        panel.add(Box.createVerticalStrut(10));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new TitledSeparator("Advanced Options", 0));
        panel.add(new TitledSeparator("", -1));
        JLabel advancedOptions = new JLabel("<html>Do you want to enable the advanced options? <br> WARNING: Only change these options when you really have to. <br>" +
                "For example: creating tests with custom ratio's or when you are having performance problems.<br>" +
                "Wrong settings can cause corrupt data!");
        JCheckBox enableAdvanced = new JCheckBox("Enable advanced options");
        enableAdvanced.setSelected(false);
        panel.add(advancedOptions);
        panel.add(enableAdvanced);


        JPanel affectRatioP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel neutralRatioP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel testRatioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel testRatioL = new JLabel("Set the ratio affect : neutral images");

        JLabel affectiveRatioL = new JLabel("Set the pull:push ratio for the affect images");
        JLabel neutralRatioL = new JLabel("Set the pull:push ratio for the neutral images");
        JLabel trialSizeL = new JLabel("Set the number of images in a trial to a custom value");

        inputTestRatioA = new JFormattedTextField(NumberFormat.getInstance());
        inputTestRatioA.setText("1");
        inputTestRatioA.setPreferredSize(new Dimension(20, 20));
        inputTestRatioN = new JFormattedTextField(NumberFormat.getInstance());
        inputTestRatioN.setText("1");
        inputTestRatioN.setPreferredSize(new Dimension(20, 20));
        testRatioPanel.add(inputTestRatioA);
        testRatioPanel.add(new JLabel(":"));
        testRatioPanel.add(inputTestRatioN);
        testRatioPanel.add(Box.createHorizontalBox());

        inputAffectRatioPull = new JFormattedTextField(NumberFormat.getInstance());
        inputAffectRatioPull.setText("1");
        inputAffectRatioPull.setPreferredSize(new Dimension(20, 20));
        inputAffectRatioPush = new JFormattedTextField(NumberFormat.getInstance());
        inputAffectRatioPush.setText("1");
        inputAffectRatioPush.setPreferredSize(new Dimension(20, 20));
        affectRatioP.add(inputAffectRatioPull);
        affectRatioP.add(new JLabel(":"));
        affectRatioP.add(inputAffectRatioPush);
        affectRatioP.add(Box.createHorizontalBox());

        inputNeutralRatioPull = new JFormattedTextField(NumberFormat.getInstance());
        inputNeutralRatioPull.setText("1");
        inputNeutralRatioPull.setPreferredSize(new Dimension(20, 20));
        inputNeutralRatioPush = new JFormattedTextField(NumberFormat.getInstance());
        inputNeutralRatioPush.setText("1");
        inputNeutralRatioPush.setPreferredSize(new Dimension(20, 20));
        neutralRatioP.add(inputNeutralRatioPull);
        neutralRatioP.add(new JLabel(":"));
        neutralRatioP.add(inputNeutralRatioPush);
        neutralRatioP.add(Box.createHorizontalBox());

        JPanel trialsizeP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputTrialSize = new JFormattedTextField(NumberFormat.getInstance());

        inputTrialSize.setPreferredSize(new Dimension(50, 20));
        trialsizeP.add(inputTrialSize);
        trialsizeP.add(Box.createHorizontalBox());

        final JPanel ratioPanelL = new JPanel(new SpringLayout());
        ratioPanelL.setEnabled(false);
        ratioPanelL.setVisible(false);
        final JPanel ratioPanelR = new JPanel(new SpringLayout());
        ratioPanelR.setEnabled(false);
        ratioPanelR.setVisible(false);
        //    ratioPanelL.add(Box.createVerticalStrut(10));
        //    ratioPanelR.add(Box.createVerticalStrut(10));
        ratioPanelL.add(new TitledSeparator("Ratio options (Only change when needed)", 0));
        ratioPanelR.add(new TitledSeparator("", -1));
        ratioPanelL.add(testRatioL);
        ratioPanelR.add(testRatioPanel);
        ratioPanelL.add(affectiveRatioL);
        ratioPanelR.add(affectRatioP);
        ratioPanelL.add(neutralRatioL);
        ratioPanelR.add(neutralRatioP);
        ratioPanelL.add(trialSizeL);
        ratioPanelR.add(trialsizeP);
        panel.add(ratioPanelL);
        panel.add(ratioPanelR);

        SpringUtilities.makeCompactGrid(ratioPanelL,
                5, 1, //rows, cols
                6, 6,        //initX, initY
                6, 16);       //xPad, yPad

        SpringUtilities.makeCompactGrid(ratioPanelR,
                5, 1, //rows, cols
                6, 10,        //initX, initY
                6, 6);       //xPad, yPad


        final JPanel perfPanelL = new JPanel(new SpringLayout());
        final JPanel perfPanelR = new JPanel(new SpringLayout());
        perfPanelL.add(Box.createVerticalStrut(10));
        perfPanelR.add(Box.createVerticalStrut(10));
        perfPanelL.add(new TitledSeparator("Performance options (Only change when needed)", 0));
        perfPanelR.add(new TitledSeparator("", -1));
        perfPanelL.add(Box.createVerticalStrut(5));
        perfPanelR.add(Box.createVerticalStrut(5));
        inputStepSize = new JFormattedTextField(NumberFormat.getInstance());
        inputStepSize.setText("31");
        inputStepSize.setPreferredSize(new Dimension(50, 15));
        inputDataStepSize = new JFormattedTextField(NumberFormat.getInstance());
        inputDataStepSize.setText("9");
        inputDataStepSize.setPreferredSize(new Dimension(50, 15));
        JPanel stepSizeP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel dataStepP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel stepL = new JLabel("<html>Change in how many steps the image will be resized, has to be an odd number" +
                "<br>A higher value means the image will be resized more smoothly<br>" +
                "Warning: When set too high, resizing may get too slow</html", JLabel.LEFT);
        JLabel dataStepL = new JLabel("<html>Change the accuracy of the joystick (Odd number), When set to a larger number, errors are faster detected. <br>" +
                "Warning: When set too high even very small movements of the joystick are recorded as error</html>", JLabel.LEFT);


        perfPanelL.add(stepL);
        stepSizeP.add(inputStepSize);
        stepSizeP.add(Box.createHorizontalBox());
        perfPanelR.add(stepSizeP);
        perfPanelL.add(dataStepL);
        //     panel.setMaximumSize(new Dimension(500, 20));
        dataStepP.add(inputDataStepSize);
        dataStepP.add(Box.createHorizontalBox());
        perfPanelR.add(dataStepP);

        JLabel imgSizeL = new JLabel("<html>Set the size the image will have when first shown on the screen<br>" +
                "Value is percentage of the height of your screen</html>");
        JPanel imgSizeP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputImageSizeP = new JFormattedTextField(NumberFormat.getIntegerInstance());
        inputImageSizeP.setText("50");
        inputImageSizeP.setPreferredSize(new Dimension(50, 20));
        perfPanelL.add(imgSizeL);
        imgSizeP.add(inputImageSizeP);
        imgSizeP.add(Box.createHorizontalBox());
        perfPanelR.add(imgSizeP);

        JLabel maxSizeL = new JLabel("<html>Set the maximum size the image can be. Value is percentage of the height of your screen.<br>" +
                "This value can be set higher than 100%, the image will then be resized larger than your screen height</html>");
        JPanel maxSizeP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputMaxSizeP = new JFormattedTextField(NumberFormat.getIntegerInstance());
        inputMaxSizeP.setText("100");
        inputMaxSizeP.setPreferredSize(new Dimension(50, 20));
        maxSizeP.add(inputMaxSizeP);
        maxSizeP.add(Box.createHorizontalBox());
        perfPanelL.add(maxSizeL);
        perfPanelR.add(maxSizeP);

        panel.add(perfPanelL);
        panel.add(perfPanelR);

        //Disable these panels at the start, so students don't become tempted to change these options.
        perfPanelL.setEnabled(false);
        perfPanelL.setVisible(false);
        perfPanelR.setEnabled(false);
        perfPanelR.setVisible(false);

        SpringUtilities.makeCompactGrid(perfPanelL,
                7, 1, //rows, cols
                6, 6,        //initX, initY
                6, 10);       //xPad, yPad

        SpringUtilities.makeCompactGrid(perfPanelR,
                7, 1, //rows, cols
                6, 6,        //initX, initY
                6, 16);       //xPad, yPad


        enableBuiltinPracticeAction(); //Enable this by default
        //Toggle the visibility of the advanced options.
        enableAdvanced.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    ratioPanelL.setEnabled(true);
                    ratioPanelL.setVisible(true);
                    ratioPanelR.setEnabled(true);
                    ratioPanelR.setVisible(true);
                    perfPanelL.setEnabled(true);
                    perfPanelL.setVisible(true);
                    perfPanelR.setEnabled(true);
                    perfPanelR.setVisible(true);
                } else if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                    ratioPanelL.setEnabled(false);
                    ratioPanelL.setVisible(false);
                    ratioPanelR.setEnabled(false);
                    ratioPanelR.setVisible(false);
                    perfPanelL.setEnabled(false);
                    perfPanelL.setVisible(false);
                    perfPanelR.setEnabled(false);
                    perfPanelR.setVisible(false);
                }
            }
        });

        SpringUtilities.makeCompactGrid(panel,
                39, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        //   return scrollPane;
        return panel;
    }

    private void disablePracticeAction() {
        inputBuiltinPractice.setEnabled(false);
        //  inputBuiltinPractice.setForeground(Color.RED);
        askBuiltinPractL.setEnabled(false);
       // askBuiltinPractL.setForeground(UIManager.getColor("Label.disabledForeground"));
        practRepeatValue = inputPractRepeat.getText();
        inputPractRepeat.setText("0");
        inputPractRepeat.setEnabled(false);
        inputPracticeFill.setEnabled(false);
        inputPracticeFill.setBackground(Color.lightGray);
        practFillL.setEnabled(false);
        inputPrDir.setText("");
        inputPrDir.setEnabled(false);
        imageSelectionPanel.setHasPracticeImages(false);
        prDirL.setEnabled(false);
        prDirButton.setEnabled(false);
        practL.setEnabled(false);
    }

    private void enablePracticeAction() {
        inputBuiltinPractice.setEnabled(true);
        askBuiltinPractL.setEnabled(true);
        askBuiltinPractL.setForeground(Color.black);
        //  inputPracticeFill.setEnabled(true);
        inputPracticeFill.setBackground(Color.lightGray);
        inputPractRepeat.setText(practRepeatValue);
        inputPractRepeat.setEnabled(true);
        inputPrDir.setText("");
        inputPrDir.setEnabled(true);
        prDirButton.setEnabled(true);
        practL.setEnabled(true);
        prDirL.setEnabled(true);
        if (inputBuiltinPractice.isSelected()) {
            imageSelectionPanel.setHasPracticeImages(false);
            enableBuiltinPracticeAction();
        } else {
            disableBuiltinPracticeAction();
            imageSelectionPanel.setHasPracticeImages(true);
        }
    }

    private void enableBuiltinPracticeAction() {
        inputPracticeFill.setEnabled(true);
        inputPracticeFill.setBackground(new Color(prFillColor));
        inputColoredBorder.setSelected(true);
        enableColoredBorderAction();
        inputPractRepeat.setText(practRepeatValue);
        inputPractRepeat.setEnabled(true);
        inputPrDir.setText("");
        inputPrDir.setEnabled(false);
        prDirButton.setEnabled(false);
        practFillL.setEnabled(true);
        prDirL.setEnabled(false);
    }

    private void disableBuiltinPracticeAction() {
        inputPracticeFill.setEnabled(false);
        inputPracticeFill.setBackground(Color.lightGray);
        inputPrDir.setText("");
        inputPrDir.setEnabled(true);
        prDirL.setEnabled(true);
        prDirButton.setEnabled(true);
        practFillL.setEnabled(false);
    }

    private void enableColoredBorderAction() {
        pullColorL.setEnabled(true);
        pushColorL.setEnabled(true);
        borderSizeL.setEnabled(true);
        inputPushColor.setEnabled(true);
        inputPullColor.setEnabled(true);
        inputBorderSize.setEnabled(true);
        inputPullColor.setBackground(new Color(pullColor));
        inputPushColor.setBackground(new Color(pushColor));

        pushTagL.setEnabled(false);
        pushTagL.setForeground(UIManager.getColor("Label.disabledForeground"));
        pullTagL.setEnabled(false);
        pullTagL.setForeground(UIManager.getColor("Label.disabledForeground"));
        inputPushTag.setEnabled(false);
        inputPullTag.setEnabled(false);
    }

    private void disableColoredBorderAction() {
        pullColorL.setEnabled(false);
        pushColorL.setEnabled(false);
        borderSizeL.setEnabled(false);
        inputPullColor.setEnabled(false);
        inputPushColor.setEnabled(false);
        inputBorderSize.setEnabled(false);
        inputPushColor.setBackground(Color.lightGray);
        inputPullColor.setBackground(Color.lightGray);
        pushTagL.setEnabled(true);
        pushTagL.setForeground(Color.black);
        pullTagL.setEnabled(true);
        pullTagL.setForeground(Color.black);
        inputPushTag.setEnabled(true);
        inputPullTag.setEnabled(true);
        inputBuiltinPractice.setSelected(false);
        disableBuiltinPracticeAction();
    }

    //File dialog to select a file to be opened
    public File fileOpenDialog() {

        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(workingDir);
        File file = null;
        FileFilter filter1 = new ExtensionFileFilter("XML File", new String[]{"XML", "xm;"});
        fc.setFileFilter(filter1);
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        if (file != null) {
            return file;
        } else {
            return null;
        }
    }

    public File ConfigOpenDialog() {

        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(workingDir);
        File file = null;
        //   FileFilter filter1 = new ExtensionFileFilter("XML File", new String[]{"XML", "xm;"});
        //   fc.setFileFilter(filter1);
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        if (file != null) {
            return file;
        } else {
            return null;
        }
    }

    private File getDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(workingDir);
        chooser.setDialogTitle("Select directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                    + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }
        return chooser.getSelectedFile();
    }

    public File fileSaveDialog(String file) {
        File export = new File(file);
        JFileChooser fc = new JFileChooser(workingDir) {
            @Override

            public void approveSelection() {

                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    workingDir = f.getParentFile();
                    int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            super.cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }
        };
        fc.setSelectedFile(export);
        //   FileFilter filter1 = new ExtensionFileFilter("CSV File", new String[]{"CSV", "csv"});
        //   fc.setFileFilter(filter1);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)

        {
            export = fc.getSelectedFile();
        } else {
            export = null;
        }
        return export;
    }



    private int createIDValue() {
        if (newTest) {
            return 1;
        } else {
            int newId = test_id; //Increase the old id with one.
            newId++;
            System.out.println("New ID value is " + newId);
            return newId;
        }
    }


    /**
     * Load a current config file for editing.
     *
     * @param file the config file.
     */
    private void LoadConfig(File file) {
        boolean hasPractice = true, builtinPractice = true, hasColoredBorders = true;
        newTest = false;
        workingDir = file.getParentFile();
        ConfigFileReader config = new ConfigFileReader(file);
        if (config.getValue("ID").length() > 0) {
            try {
                test_id = Integer.parseInt(config.getValue("ID"));
                System.out.println("Test ID = " + test_id);
            } catch (Exception e) {
                System.out.println("Invalid ID value detected, resetting the value to 99999");
                test_id = 99999;
            }

        } else {
            test_id = 1;  //Older config files don't have the id option. Add the ID to upgrade these config files to the newest version.
        }

        inputBorderSize.setText(config.getValue("BorderWidth"));
        inputAffDir.setText(config.getValue("AffectiveDir"));
        aDir = new File(workingDir.getAbsoluteFile() + File.separator + inputAffDir.getText() + File.separator);
        imageSelectionPanel.setaDir(aDir);
        //TODO eigenlijk zou dit hardcoded images niet moeten.
        inputBreak.setText(config.getValue("BreakAfter"));
        inputDataStepSize.setText(config.getValue("DataSteps"));
        if (inputDataStepSize.getText().equals("")) {
            inputDataStepSize.setText("9");
        }
        inputLangFile.setText(config.getValue("LanguageFile"));
        File langFile = new File(workingDir.getAbsoluteFile() + File.separator + inputLangFile.getText() + File.separator);
        htmlEditPanel.setDocument(langFile);
        inputNeutralDir.setText(config.getValue("NeutralDir"));
        nDir = new File(workingDir.getAbsoluteFile() + File.separator + inputNeutralDir.getText() + File.separator);
        imageSelectionPanel.setnDir(nDir);
        //TODO eigenlijk zou dit hardcoded images niet moeten.

        System.out.println(nDir.getAbsolutePath());

        inputPullTag.setText(config.getValue("PullTag"));
        inputPushTag.setText(config.getValue("PushTag"));
        inputQuestionFile.setText(config.getValue("Questionnaire"));
        System.out.println("Q2 File "+inputQuestionFile.getText());
        Rectangle r = this.getBounds();
        displayQuestionnairePanel = new DisplayQuestionnairePanel(null,new Dimension(r.width,r.height));
        displayQuestionnairePanel.displayQuestions(new File(workingDir+File.separator+inputQuestionFile.getText()));
        questionPane = new JScrollPane((displayQuestionnairePanel));
        tabbedPane.remove(tabbedPane.indexOfTab("Questionnaire"));
        tabbedPane.addTab("Questionnaire", questionPane);
        revalidate();
        repaint();
        inputStepSize.setText(config.getValue("StepSize"));

        if (config.getValue("MaxSizePerc").equals("")) {
            inputMaxSizeP.setText("100");
        } else {
            inputMaxSizeP.setText(config.getValue("MaxSizePerc"));
        }
        if (config.getValue("ImageSizePerc").equals("")) {
            inputImageSizeP.setText("50");
        } else {
            inputImageSizeP.setText(config.getValue("ImageSizePerc"));
        }

        practRepeatValue = config.getValue("PracticeRepeat");
        if (practRepeatValue.equals("") || practRepeatValue.equals("0")) {
            hasPractice = false;
            practRepeatValue = "0";
        } else {
            hasPractice = true;
        }
        if (inputStepSize.getText().equals("")) {
            inputStepSize.setText("31");
        }
        inputTrials.setText(config.getValue("Trials"));
        inputTrialSize.setText(config.getValue("TrialSize"));
        inputQuestions.setSelectedItem(config.getValue("DisplayQuestions"));
        if (config.getValue("DisplayQuestions").equals("None")) {
            inputQuestionFile.setText("");
            inputQuestionFile.setEnabled(false);
            selectQL.setEnabled(false);

        } else {
            selectQL.setEnabled(true);
        }
        String showBoxPlot = config.getValue("ShowBoxPlot");
        if (showBoxPlot.equals("True")) {
            inputBoxplot.setSelected(true);
        } else {
            inputBoxplot.setSelected(false);
        }
        if (config.getValue("AffectRatio").contains(":")) {
            inputAffectRatioPush.setText(getRatio(config.getValue("AffectRatio"))[0]);
            inputAffectRatioPull.setText(getRatio(config.getValue("AffectRatio"))[1]);
        } else {
            inputAffectRatioPull.setText("1");
            inputAffectRatioPush.setText("1");
        }
        if (config.getValue("NeutralRatio").contains(":")) {
            inputNeutralRatioPush.setText(getRatio(config.getValue("NeutralRatio"))[0]);
            inputNeutralRatioPull.setText(getRatio(config.getValue("NeutralRatio"))[1]);
        } else {
            inputNeutralRatioPull.setText("1");
            inputNeutralRatioPush.setText("1");
        }
        if (config.getValue("TestRatio").contains(":")) {
            inputTestRatioA.setText(getRatio(config.getValue("TestRatio"))[0]);
            inputTestRatioN.setText(getRatio(config.getValue("TestRatio"))[1]);
        } else {
            inputTestRatioA.setText("1");
            inputTestRatioN.setText("1");
        }
        if (config.getValue("BorderColorPull").length() == 6) {
            inputPullColor.setBackground(getColor(config.getValue("BorderColorPull")));
        }
        if (config.getValue("BorderColorPush").length() == 6) {
            inputPushColor.setBackground(getColor(config.getValue("BorderColorPush")));
        }
        if (config.getValue("PracticeFillColor").length() == 6) {
            inputPracticeFill.setBackground(getColor(config.getValue("PracticeFillColor")));
            inputPracticeFill.setEnabled(true);
//TODO nog  even kijken of dit werkt
            builtinPractice = true;
        } else {
            builtinPractice = false;
        }
        String coloredBorder = config.getValue("ColoredBorders");
        hasColoredBorders = coloredBorder.equals("True");

        if (builtinPractice) {
            inputBuiltinPractice.setSelected(true);
            enableBuiltinPracticeAction();
        } else {
            inputBuiltinPractice.setSelected(false);
            disableBuiltinPracticeAction();
        }
        if (hasColoredBorders) {
            inputColoredBorder.setSelected(true);
            enableColoredBorderAction();
        } else {
            inputColoredBorder.setSelected(false);
            disableColoredBorderAction();
        }
        if (hasPractice) {
            inputHasPractice.setSelected(true);
            enablePracticeAction();
        } else {
            inputHasPractice.setSelected(false);
            disablePracticeAction();
        }

        //Load the value for the practice images directory.
        if (config.getValue("PracticeDir").length() > 0 && inputHasPractice.isSelected() && !inputBuiltinPractice.isSelected()) {

            inputPrDir.setEnabled(true);
            inputPrDir.setForeground(Color.black);
            inputPrDir.setText(config.getValue("PracticeDir"));
            pDir = new File(workingDir.getAbsoluteFile() + File.separator + inputPrDir.getText() + File.separator);
            imageSelectionPanel.setpDir(pDir);
            //TODO hier aanpassen

            System.out.println("Practice directory found at " + pDir.getAbsoluteFile());
        }
    }


    private String[] getRatio(String ratio) {
        return ratio.split(":");
    }

    private Color getColor(String hex) {
        int intColor = Integer.parseInt(hex, 16);
        return new Color(intColor);
    }


    private void saveAction(File file) {
        ConfigWriter.writeToFile(file, getConfiguration());
        imageSelectionPanel.writeToFile();


    }

    private TestConfiguration getConfiguration() {
        TestConfiguration configuration = new TestConfiguration();

        configuration.setDataFile(createFullPathFile("data.xml"));
        configuration.setWorkingDir(workingDir);
        configuration.setAffectiveDir(createFullPathFile(inputAffDir.getText()));
        configuration.setNeutralDir(createFullPathFile(inputNeutralDir.getText()));
        configuration.setPracticeDir(createFullPathFile(inputPrDir.getText()));
        configuration.setTrials(Integer.parseInt(inputTrials.getText()));
        configuration.setBreakAfter(Integer.parseInt(inputBreak.getText()));
        configuration.setPracticeRepeat(Integer.parseInt(inputPractRepeat.getText()));
        if (inputBoxplot.isSelected()) {
            configuration.setShowBoxPlot(true);
        }
        configuration.setDisplayQuestions(inputQuestions.getSelectedItem().toString());
        configuration.setQuestionnaireFile(createFullPathFile(inputQuestionFile.getText()));
        configuration.setLanguageFile(createFullPathFile(inputLangFile.getText()));
        if (inputColoredBorder.isSelected()) {
            configuration.setColoredBorders(true);
            configuration.setPullColor(Integer.toHexString(inputPullColor.getBackground().getRGB()));
            configuration.setPushColor(Integer.toHexString(inputPushColor.getBackground().getRGB()));
            configuration.setBorderWidth(Integer.parseInt(inputBorderSize.getText()));
        }
        configuration.setPullTag(inputPullTag.getText());
        configuration.setPushTag(inputPushTag.getText());

        configuration.setStepSize(Integer.parseInt(inputStepSize.getText()));
        configuration.setDataSteps(Integer.parseInt(inputDataStepSize.getText()));
        configuration.setMaxSizePerc(Integer.parseInt(inputMaxSizeP.getText()));
        configuration.setImageSizePerc(Integer.parseInt(inputImageSizeP.getText()));
        configuration.setAffectRatio(inputAffectRatioPush.getText() + ":" + inputAffectRatioPull.getText());
        configuration.setNeutralRatio(inputNeutralRatioPush.getText() + ":" + inputNeutralRatioPull.getText());
        configuration.setTestRatio(inputTestRatioA.getText() + ":" + inputTestRatioN.getText());
        if (inputTrialSize.getText().length() > 0) {
            configuration.setTrialSize(Integer.parseInt(inputTrialSize.getText()));
        }
        if (inputHasPractice.isSelected()) {
            configuration.setHasPractice(true);
            configuration.setPracticeRepeat(Integer.parseInt(inputPractRepeat.getText()));
        }

        if (inputBuiltinPractice.isSelected()) {
            configuration.setPracticeFill(true);
            configuration.setPracticeFillColor(Integer.toHexString(inputPracticeFill.getBackground().getRGB()));
        }

        configuration.setTestID(createIDValue());

        return configuration;
    }

    //TODO add  "PlotType"

    public File createFullPathFile(String file) {
        File f = new File(file);
        if (f.isAbsolute()) {
                return new File(FileUtils.getRelativePath(workingDir, f));
        }
         else {
            return new File(workingDir+File.separator+file);
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        if (o.toString().equals("Finished")) {
            model.deleteObservers();
            joystick.exit();
            joystick = null; //Remove instance when finished
            System.gc();

        }
    }


    private Observer getInstance() {
        return this;
    }
}
