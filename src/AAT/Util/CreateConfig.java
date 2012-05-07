package AAT.Util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 5/7/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateConfig extends JPanel {

    private JTextField inputTrials, inputBreak, inputPushTag, inputPullTag, inputAffDir, inputNeutralDir, inputLangFile, inputPrDir, inputQuestion;
    private JTextField inputAffectRatioPush, inputAffectRatioPull, inputNeutralRatioPush, inputNeutralRatioPull, inputTestRatioA, inputTestRatioN, inputTrialSize;
    private JButton inputPushColor, inputPullColor, inputPracticeFill, selectQButton;
    private JPanel selectQPanel;
    private JTextField inputStepSize, inputDataStepSize;
    private JComboBox inputQuestions;
    private JCheckBox inputBoxplot, inputColoredBorder;
    private int pullColor, pushColor, prFillColor;
    private File workingDir = new File("");


    public CreateConfig() {
        super(new GridLayout(2, 1));
        // super(new FlowLayout(FlowLayout.LEFT));


        JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
        JButton openButton = new JButton(new ImageIcon("document-open.png"));
        openButton.setToolTipText("Open a AAT Config file");
        openButton.setPreferredSize(new Dimension(48, 48));
        JButton saveButton = new JButton(new ImageIcon("document-save.png"));
        saveButton.setPreferredSize(new Dimension(48, 48));
        saveButton.setToolTipText("Save AAT Config file");
        JButton tryButton = new JButton(new ImageIcon("playButton48.png"));
        tryButton.setToolTipText("Try the current configuration");
        tryButton.setPreferredSize(new Dimension(48, 48));
        toolbar.add(openButton);
        toolbar.add(saveButton);
        toolbar.add(tryButton);
        this.add(toolbar);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(500, 500));
        ImageIcon icon = null;

        JComponent panel1 = createMainPanel();
        tabbedPane.addTab("Main configuration", icon, panel1,
                "Sets the main configuration of the test");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent panel2 = createRatiosPanel();
        tabbedPane.addTab("Ratio's", icon, panel2,
                "Change the ratio's of the test");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        JComponent panel3 = createPerformancePanel();
        tabbedPane.addTab("Performance", icon, panel3,
                "Options for performace of the test");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    JScrollPane createMainPanel() {

        JPanel panel = new JPanel(new SpringLayout());
        JScrollPane scrollPane = new JScrollPane(panel);
        //       panel.setPreferredSize(new Dimension(600,600));
        // panel.setMaximumSize(new Dimension(400,400));
        JPanel affDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel neutrDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel prDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton affDirButton = new JButton("Choose Affect Directory");
        JButton neutrDirButton = new JButton("Choose Neutral Directory");
        JButton prDirButton = new JButton("Choose Practice Directory");
        JLabel affDirL = new JLabel("Choose the directory that contains the affective images");
        JLabel neutrDirL = new JLabel("Choose the directory that contains the neutral images");
        JLabel prDirL = new JLabel("When you have a seperate directory containing practice images, set is here");
        inputAffDir = new JTextField("Affective");
        inputAffDir.setEditable(false);
        inputAffDir.setPreferredSize(new Dimension(200, 20));
        inputNeutralDir = new JTextField("Neutral");
        inputNeutralDir.setEditable(false);
        inputNeutralDir.setPreferredSize(new Dimension(200, 20));
        inputPrDir = new JTextField("");
        inputPrDir.setEditable(true);
        inputPrDir.setPreferredSize(new Dimension(200, 20));
        affDirPanel.add(inputAffDir);
        affDirPanel.add(affDirButton);
        affDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = getDirectory();
                if (file != null) {
                    inputAffDir.setText(file.getName());
                    workingDir = file.getParentFile();
                    System.out.println(workingDir.toString());
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
                    inputNeutralDir.setText(file.getName());
                    workingDir = file.getParentFile();
                    System.out.println(workingDir.toString());
                } else {
                    inputNeutralDir.setText("");
                }
            }
        });

        prDirPanel.add(inputPrDir);
        prDirPanel.add(prDirButton);
        prDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = getDirectory();
                if (file != null) {
                    inputPrDir.setText(file.getName());
                    workingDir = file.getParentFile();
                    System.out.println(workingDir.toString());
                    inputPracticeFill.setEnabled(false);
                    inputPracticeFill.setBackground(Color.lightGray);
                } else {
                    inputNeutralDir.setText("");
                    inputPracticeFill.setEnabled(true);
                    inputPracticeFill.setBackground(new Color(prFillColor));
                }
            }
        });
        panel.add(affDirL);
        panel.add(affDirPanel);
        panel.add(neutrDirL);
        panel.add(neutrDirPanel);
        panel.add(prDirL);
        panel.add(prDirPanel);

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
                    inputLangFile.setText(file.getName());
                    workingDir = file.getParentFile();
                    System.out.println(workingDir.toString());
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
        inputTrials = new JTextField("4");
        inputTrials.setPreferredSize(new Dimension(50, 20));
        panel.add(trialsL);
        trialP.add(inputTrials);
        trialP.add(Box.createHorizontalBox());
        panel.add(trialP);

        JPanel breakP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel breakL = new JLabel("When will there be a break? Set to 0 when you don't want a break");
        inputBreak = new JTextField("2");
        inputBreak.setPreferredSize(new Dimension(50, 20));
        panel.add(breakL);
        breakP.add(inputBreak);
        breakP.add(Box.createHorizontalBox());
        panel.add(breakP);

        JPanel comboP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel displayQL = new JLabel("When do you want to display a questionnaire?");
        inputQuestions = new JComboBox(new Object[]{"None", "Before", "After"});
        inputQuestions.setPreferredSize(new Dimension(100, 20));
        inputQuestions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (inputQuestions.getSelectedItem().equals("None")) {
                    inputQuestion.setEnabled(false);
                    selectQButton.setEnabled(false);

                } else {
                    inputQuestion.setEnabled(true);
                    selectQButton.setEnabled(true);
                }
            }
        });
        panel.add(displayQL);
        comboP.add(inputQuestions);
        comboP.add(Box.createHorizontalBox());
        panel.add(comboP);

        JLabel selectQL = new JLabel("Select the xml file that contains the questionnaire");
        selectQPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputQuestion = new JTextField();
        inputQuestion.setPreferredSize(new Dimension(200, 20));
        inputQuestion.setEditable(false);
        inputQuestion.setEnabled(false);
        selectQButton = new JButton("Select File");
        selectQButton.setEnabled(false);
        selectQPanel.add(inputQuestion);
        selectQPanel.add(selectQButton);
        selectQButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = getDirectory();
                if (file != null) {
                    inputQuestion.setText(file.getName());
                    workingDir = file.getParentFile();
                    System.out.println(workingDir.toString());
                } else {
                    inputQuestion.setText("");
                }
            }
        });
        panel.add(selectQL);
        panel.add(selectQPanel);
        inputQuestion.setEnabled(false);
        JLabel boxPlotL = new JLabel("Do you want the test to show a BoxPlot at the end?");
        inputBoxplot = new JCheckBox();
        inputBoxplot.setPreferredSize(new Dimension(20, 20));
        inputBoxplot.setSelected(true);
        panel.add(boxPlotL);
        panel.add(inputBoxplot);

        JLabel coloredBorderL = new JLabel("Do you want to automatically create a colored border around the images?");
        inputColoredBorder = new JCheckBox();
        inputColoredBorder.setPreferredSize(new Dimension(20, 20));
        inputColoredBorder.setSelected(true);

        panel.add(coloredBorderL);
        panel.add(inputColoredBorder);

        JLabel pushColorL = new JLabel("Set the border color for the push images");
        JLabel pullColorL = new JLabel("Set the border color for the pull images");
        JLabel practFillL = new JLabel("Set the fill color for the generated practice images");
        //    inputPushColor = new JTextField("F5FE02");
        //   inputPullColor = new JTextField("00A4E7");
        pushColor = Integer.parseInt("F5FE02", 16);
        pullColor = Integer.parseInt("00A4E7", 16);
        prFillColor = Integer.parseInt("FFDEDE", 16);
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
        panel.add(pushColorL);
        pushColorP.add(inputPushColor);
        pushColorP.add(Box.createHorizontalBox());
        panel.add(pushColorP);
        panel.add(pullColorL);
        pullColorP.add(inputPullColor);
        pullColorP.add(Box.createHorizontalBox());
        panel.add(pullColorP);
        panel.add(practFillL);
        practFillP.add(inputPracticeFill);
        practFillP.add(Box.createHorizontalBox());
        panel.add(practFillP);
        JLabel pushTagL = new JLabel("<html>Set the tag by which the test can know which image is a push image.<br>" +
                "The image filename needs to contain this tag</html>");
        JLabel pullTagL = new JLabel("<html>Set the tag by which the test can know which image is a pull image.<br>" +
                "The image filename needs to contain this tag</html>");
        JPanel pushTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pullTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPushTag = new JTextField("push");
        inputPushTag.setPreferredSize(new Dimension(100, 20));
        inputPushTag.setEnabled(false);
        inputPullTag = new JTextField("pull");
        inputPullTag.setPreferredSize(new Dimension(100, 20));
        inputPullTag.setEnabled(false);
        panel.add(pushTagL);
        pushTP.add(inputPushTag);
        pushTP.add(Box.createHorizontalBox());
        panel.add(pushTP);
        panel.add(pullTagL);
        pullTP.add(inputPullTag);
        pullTP.add(Box.createHorizontalBox());
        panel.add(pullTP);

        inputColoredBorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (inputColoredBorder.isSelected()) {
                    inputPushColor.setEnabled(true);
                    inputPullColor.setEnabled(true);
                    inputPullColor.setBackground(new Color(pullColor));
                    inputPushColor.setBackground(new Color(pushColor));

                    inputPushTag.setEnabled(false);
                    inputPullTag.setEnabled(false);
                } else {
                    inputPullColor.setEnabled(false);
                    inputPushColor.setEnabled(false);
                    inputPushColor.setBackground(Color.lightGray);
                    inputPullColor.setBackground(Color.lightGray);
                    inputPushTag.setEnabled(true);
                    inputPullTag.setEnabled(true);
                }
            }
        });

        SpringUtilities.makeCompactGrid(panel,
                15, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        return scrollPane;
    }

    private JPanel createPerformancePanel() {
        //     JPanel panel = new JPanel(new GridLayout(2,2));
        JPanel panel = new JPanel(new SpringLayout());
        JLabel stepL = new JLabel("Change in how many steps the image will be resized, has to be an odd number");
        JLabel dataStepL = new JLabel("<html>Change the accuracy of the joystick, When set to a larger number, errors are faster detected. <br>" +
                "This also needs to be an odd number. <br>" +
                "Warning: When set to high even very small movements of the joystick are recorded as error</html>");
        JPanel stepSizeP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel dataStepP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputStepSize = new JTextField("31");
        inputStepSize.setPreferredSize(new Dimension(50, 15));
        inputDataStepSize = new JTextField("9");
        inputDataStepSize.setPreferredSize(new Dimension(50, 15));
        panel.add(stepL);
        stepSizeP.add(inputStepSize);
        stepSizeP.add(Box.createHorizontalBox());
        panel.add(stepSizeP);
        panel.add(dataStepL);
        dataStepP.add(inputDataStepSize);
        dataStepP.add(Box.createHorizontalBox());
        panel.add(dataStepP);
        SpringUtilities.makeCompactGrid(panel,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        //   panel.repaint();
        return panel;
    }

    private JScrollPane createRatiosPanel() {

        JPanel panel = new JPanel(new SpringLayout());
        panel.setLayout(new SpringLayout());
        JScrollPane scrollPane = new JScrollPane(panel);
        JPanel affectRatioP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel neutralRatioP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel testRatioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel testRatioL = new JLabel("Set the ratio affect : neutral images");
        JLabel affectiveRatioL = new JLabel("Set the pull:push ratio for the affect images");
        JLabel neutralRatioL = new JLabel("Set the pull:push ratio for the neutral images");
        JLabel trialSizeL = new JLabel("Set the number of images in a trial to a custom value");

        inputTestRatioA = new JTextField("1");
        inputTestRatioA.setPreferredSize(new Dimension(20, 20));
        inputTestRatioN = new JTextField("1");
        inputTestRatioN.setPreferredSize(new Dimension(20, 20));
        testRatioPanel.add(inputTestRatioA);
        testRatioPanel.add(new JLabel(":"));
        testRatioPanel.add(inputTestRatioN);
        testRatioPanel.add(Box.createHorizontalBox());

        inputAffectRatioPull = new JTextField("1");
        inputAffectRatioPull.setPreferredSize(new Dimension(20, 20));
        inputAffectRatioPush = new JTextField("1");
        inputAffectRatioPush.setPreferredSize(new Dimension(20, 20));
        affectRatioP.add(inputAffectRatioPull);
        affectRatioP.add(new JLabel(":"));
        affectRatioP.add(inputAffectRatioPush);
        affectRatioP.add(Box.createHorizontalBox());

        inputNeutralRatioPull = new JTextField("1");
        inputNeutralRatioPull.setPreferredSize(new Dimension(20, 20));
        inputNeutralRatioPush = new JTextField("1");
        inputNeutralRatioPush.setPreferredSize(new Dimension(20, 20));
        neutralRatioP.add(inputNeutralRatioPull);
        neutralRatioP.add(new JLabel(":"));
        neutralRatioP.add(inputNeutralRatioPush);
        neutralRatioP.add(Box.createHorizontalBox());

        JPanel trialsizeP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputTrialSize = new JTextField("");
        inputTrialSize.setPreferredSize(new Dimension(50, 20));
        trialsizeP.add(inputTrialSize);
        trialsizeP.add(Box.createHorizontalBox());
        panel.add(testRatioL);
        panel.add(testRatioPanel);
        panel.add(affectiveRatioL);
        panel.add(affectRatioP);
        panel.add(neutralRatioL);
        panel.add(neutralRatioP);
        panel.add(trialSizeL);
        panel.add(trialsizeP);

        SpringUtilities.makeCompactGrid(panel,
                4, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        return scrollPane;
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

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TabbedPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new CreateConfig(), BorderLayout.CENTER);
        JMenuBar menu = new JMenuBar();
        JMenuItem file = new JMenuItem("exit");
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(file);
        menu.add(fileMenu);
        frame.setJMenuBar(menu);
        file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(-1);
            }
        });
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
}

//filter voor de file extensions. Komt ook van het internet. Wordt nu gebruik om .input en csv bestanden te filteren.
class ExtensionFileFilter extends FileFilter {
    String description;

    String extensions[];

    public ExtensionFileFilter(String description, String extensions[]) {
        if (description == null) {
            this.description = extensions[0];
        } else {
            this.description = description;
        }
        this.extensions = extensions.clone();
        toLower(this.extensions);
    }

    private void toLower(String array[]) {
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = array[i].toLowerCase();
        }
    }

    public String getDescription() {
        return description;
    }

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            String path = file.getAbsolutePath().toLowerCase();
            for (String extension : extensions) {
                if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
        }
        return false;
    }
}