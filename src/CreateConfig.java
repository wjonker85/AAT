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

import AAT.AatObject;
import AAT.Util.ExtensionFileFilter;
import AAT.Util.FileUtils;
import AAT.Util.SpringUtilities;
import AAT.Util.TitledSeparator;
import Configuration.TestConfig;
import Controller.JoystickController;
import IO.XMLReader;
import IO.XMLWriter;
import Model.AATModel;
import views.HTMLEditPanel;
import views.TestFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 5/7/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateConfig extends JPanel implements Observer {

    private JTextField inputPushTag, inputPullTag, inputAffDir, inputNeutralDir, inputLangFile, inputPrDir, inputQuestion, inputPractRepeat;
    private JTextField inputTrials, inputBreak;
    private JTextField inputAffectRatioPush, inputAffectRatioPull, inputNeutralRatioPush, inputNeutralRatioPull, inputTestRatioA, inputTestRatioN, inputTrialSize;
    private JButton inputPushColor, inputPullColor, inputPracticeFill, selectQButton;
    private JTextField inputBorderSize;
    private JTextField inputStepSize, inputDataStepSize;
    private JComboBox inputQuestions;
    private JCheckBox inputBoxplot, inputColoredBorder, inputHasPractice, inputBuiltinPractice;
    private int pullColor, pushColor, prFillColor;
    private File workingDir = new File("");
    private File nDir, aDir, pDir, currentConfig;
    private AATModel model;
    private JoystickController joystick;
    private TestFrame testFrame;
    private JLabel pullColorL, pushColorL, borderSizeL, pushTagL, pullTagL, askBuiltinPractL, practFillL, prDirL, practL, selectQL;
    private JButton prDirButton;
    private String practRepeatValue = "3";
    private JTextField inputMaxSizeP, inputImageSizeP;
    private JTable tableA, tableN, tableP;
    private Boolean newTest;
    private int test_id = 1;
    private XMLReader reader;
    private JLabel pLabel;
    private JScrollPane scrollPaneP;
    HTMLEditPanel htmlEditPanel;


    //regex for extension filtering
    private Pattern pattern;

    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpeg|jpg|png|gif|bmp))$)";

    private static final String HEX_PATTERN = "(^[0-9A-F]+$)";


    public CreateConfig() {
        super(new SpringLayout());
        //  super(new FlowLayout(FlowLayout.CENTER));
        pattern = Pattern.compile(IMAGE_PATTERN); //create regex
        nDir = new File("");
        aDir = new File("");
        pDir = new File("");
        currentConfig = new File("");
        newTest = true;
        reader = new XMLReader();

        JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
        final JButton newButton = new JButton(new ImageIcon("document-new.png"));
        newButton.setToolTipText("Create a new AAT Config file");
        newButton.setPreferredSize(new Dimension(48, 48));


        JButton openButton = new JButton(new ImageIcon("document-open.png"));
        openButton.setToolTipText("Open a AAT Config file");
        openButton.setPreferredSize(new Dimension(48, 48));

        final JButton saveAsButton = new JButton(new ImageIcon("document-saveAs.png"));
        saveAsButton.setPreferredSize(new Dimension(48, 48));
        saveAsButton.setToolTipText("Save AAT Config file as ...");
        saveAsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = fileSaveDialog("AATConfig");
                if (file != null) {
                    writeToFile(file);
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
                    writeToFile(currentConfig);
                    JOptionPane.showMessageDialog(null,
                            "AAT Config file saved.");
                } else {
                    File file = fileSaveDialog("AATConfig");
                    if (file != null) {
                        writeToFile(file);
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

        final JButton tryButton = new JButton(new ImageIcon("playButton48.png"));
        tryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    File testFile = new File(workingDir.toString() + File.separator + "AATConfig.temp");
                    model = new AATModel();
                    model.addObserver(getInstance());
                    writeToFile(testFile);
                    model.loadNewAAT(testFile);     //Only start when config is valid
                    joystick = new JoystickController(model);
                    joystick.start(); //Start joystick Thread
                    testFrame = new TestFrame(model);
                    model.addObserver(testFrame);
                    model.addObserver(joystick);
                    model.startTest(false); //start test without saving data
                    testFile.delete();    //Delete the test config file


                } catch (AatObject.FalseConfigException e) {
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
        this.add(toolbar);
        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = null;
        final JPanel mainPanel = createMainPanel();
        mainPanel.setEnabled(false);
        mainPanel.setVisible(false);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        tabbedPane.addTab("AAT configuration", icon, scrollPane,
                "Sets the main configuration of the test");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        add(tabbedPane);
        ArrayList<File> practiceImages = new ArrayList<File>();
        if (inputPrDir.getText().length() > 0) {
            practiceImages = getImages(pDir);
        }
        JPanel images = createImageListTable(getImages(aDir), getImages(nDir), getImages(pDir));
        JScrollPane contentPane = new JScrollPane(images);
        contentPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        //   JPanel images = createImageListTable(getImages(new File("/home/marcel/AAT/AAT/images/Affective/")),getImages(new File("/home/marcel/AAT/AAT/images/Neutral/")));

        tabbedPane.addTab("Images", contentPane);

        htmlEditPanel = new HTMLEditPanel();

           JScrollPane HtmlEditPane = new JScrollPane((htmlEditPanel));
        HtmlEditPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        HtmlEditPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        tabbedPane.addTab("Language File",HtmlEditPane);

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
                    writeToFile(file);
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

    private Observer getInstance() {

        return this;
    }


    /**
     * Loads all image files in a given directory. Extension filter with regular expression.
     *
     * @param dir Directory containing images
     * @return ArrayList<File> with all image files in a directory
     */
    public ArrayList<File> getImages(File dir) {
        if (dir.length() > 0) {
            if (dir.exists()) {
                File[] files = dir.listFiles(extensionFilter);

                return new ArrayList<File>(Arrays.asList(files));
            } else return new ArrayList<File>();
        } else return new ArrayList<File>();
    }


    /**
     * Filter so that only the image files in a directory will be selected
     */
    java.io.FileFilter extensionFilter = new java.io.FileFilter() {
        public boolean accept(File file) {
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };


    private JPanel createImageListTable(ArrayList<File> imageFilesA, ArrayList<File> imageFilesN, ArrayList<File> imageFilesP) {
        ArrayList<String> aFiles = reader.getIncludedFiles(aDir);  //These lists contain the files that were specified for this test.
        ArrayList<String> nFiles = reader.getIncludedFiles(nDir);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        JLabel title = new JLabel("Please select the neutral and affective images to be included in the test.", SwingConstants.LEFT);
        Font f = title.getFont();

        title.setFont(new Font(f.getName(), Font.PLAIN, 24));

        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(title, c);
        JLabel aLabel = new JLabel("Affective Images");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;

        c.insets = new Insets(70, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        panel.add(aLabel, c);
        tableA = new JTable(new ImageTableModel(imageFilesA, aFiles));
        tableA.setComponentPopupMenu(new JPopupMenu());
        JScrollPane scrollPaneA = new JScrollPane(tableA);
        tableA.setFillsViewportHeight(true);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(10, 0, 0, 0);
        final JPopupMenu popupMenuA = new JPopupMenu();
        JMenuItem selectAllA = new JMenuItem("Select All");
        selectAllA.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll(tableA, true);
            }
        });
        popupMenuA.add(selectAllA);
        JMenuItem deselectAllA = new JMenuItem("Deselect All");
        deselectAllA.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll(tableA, false);
            }
        });
        popupMenuA.add(deselectAllA);
        tableA.setComponentPopupMenu(popupMenuA);
        setTableColumnWidths(tableA);
        panel.add(scrollPaneA, c);

        JLabel nLabel = new JLabel("Neutral Images");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(70, 0, 0, 0);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(nLabel, c);
        tableN = new JTable(new ImageTableModel(imageFilesN, nFiles));
        setTableColumnWidths(tableN);
        JScrollPane scrollPaneN = new JScrollPane(tableN);
        tableN.setFillsViewportHeight(true);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(10, 0, 0, 0);
        final JPopupMenu popupMenuN = new JPopupMenu();
        JMenuItem selectAllN = new JMenuItem("Select All");
        selectAllN.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll(tableN, true);
            }
        });
        popupMenuN.add(selectAllN);
        JMenuItem deselectAllN = new JMenuItem("Deselect All");
        deselectAllN.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll(tableN, false);
            }
        });
        popupMenuN.add(deselectAllN);
        tableN.setComponentPopupMenu(popupMenuN);

        panel.add(scrollPaneN, c);


        System.out.println("Adding practice table");
        pLabel = new JLabel("Practice Images");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(70, 0, 0, 0);
        c.gridx = 2;
        c.gridy = 1;
        panel.add(pLabel, c);
        tableP = new JTable(new ImageTableModel(imageFilesP, nFiles));
        setTableColumnWidths(tableP);
        scrollPaneP = new JScrollPane(tableP);
        tableP.setFillsViewportHeight(true);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 2;
        c.insets = new Insets(10, 0, 0, 0);
        final JPopupMenu popupMenuP = new JPopupMenu();
        JMenuItem selectAllP = new JMenuItem("Select All");
        selectAllP.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll(tableP, true);
            }
        });
        popupMenuP.add(selectAllP);
        JMenuItem deselectAllP = new JMenuItem("Deselect All");
        deselectAllP.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll(tableP, false);
            }
        });
        popupMenuP.add(deselectAllP);
        tableP.setComponentPopupMenu(popupMenuP);
        panel.add(scrollPaneP, c);
        scrollPaneP.setVisible(false);
        pLabel.setVisible(false);


        //  }


        return panel;
    }


    private void setTableColumnWidths(JTable table) {
        TableColumn column = null;
        for (int i = 0; i < 2; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(300); //third column is bigger
            } else {
                column.setPreferredWidth(20);
            }
        }
    }

    private void refreshTables() {
        ArrayList<File> imageFilesA = getImages(aDir);
        ArrayList<File> imageFilesN = getImages(nDir);

        ArrayList<String> aFiles = reader.getIncludedFiles(aDir);  //These lists contain the files that were specified for this test.
        ArrayList<String> nFiles = reader.getIncludedFiles(nDir);

        tableA.setModel(new ImageTableModel(imageFilesA, aFiles));
        tableN.setModel(new ImageTableModel(imageFilesN, nFiles));
        setTableColumnWidths(tableA);
        setTableColumnWidths(tableN);
        tableA.repaint();
        tableN.repaint();


        if (!inputBuiltinPractice.isSelected() && inputHasPractice.isSelected()) {          //Add the images when a practice dir is selected
            ArrayList<File> imageFilesP = getImages(pDir);
            ArrayList<String> pFiles = reader.getIncludedFiles(pDir);
            tableP.setModel(new ImageTableModel(imageFilesP, pFiles));
            scrollPaneP.setVisible(true);
            pLabel.setVisible(true);
            setTableColumnWidths(tableP);
            tableP.repaint();
        }

    }


    private void selectAll(JTable table, boolean select) {
        TableModel model = table.getModel();
        for (int x = 0; x < model.getRowCount(); x++) {
            model.setValueAt(select, x, 1);
        }
        table.setModel(model);
    }


    //Table model for the two JTables that contain the image files. This model contains the data that is displayed.
    //When it is the first time a test is created all image files in a directory are added. When it is a modification of an existing test
    //then only the already included images are selected.
    class ImageTableModel extends AbstractTableModel {

        public ImageTableModel(ArrayList<File> imageFiles, ArrayList<String> includedFiles) {
            data = new Object[imageFiles.size()][2];
            int x = 0;
            for (File f : imageFiles) {
                data[x][0] = f.getName();
                if (newTest || includedFiles.size() == 0) {
                    data[x][1] = new Boolean(true);
                } else {
                    if (includedFiles.contains(f.getName())) {
                        data[x][1] = new Boolean(true);
                    } else {
                        data[x][1] = new Boolean(false);
                    }
                }
                x++;
            }


        }

        private String[] columnNames = {"Image File",
                "Included"};

        private Object[][] data;


        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
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
                    try {
                        inputAffDir.setText(FileUtils.getRelativePath(workingDir, aDir));
                    } catch (IOException e) {
                        inputAffDir.setText(file.getName());
                    }

                    refreshTables();
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
                    try {
                        inputNeutralDir.setText(FileUtils.getRelativePath(workingDir, nDir));

                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        inputNeutralDir.setText(file.getName());
                    }


                    System.out.println("Working dir: " + workingDir.getAbsoluteFile());
                    System.out.println("neutral dir: " + nDir.getAbsoluteFile());
                    System.out.println("Relative path: ");

                    refreshTables();
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
                    if(file.exists()) {
                    inputLangFile.setText(file.getName());
                        htmlEditPanel.setDocument(file);
                    }
                    else {
                        int reply = JOptionPane.showConfirmDialog(null, "Do you want to create the file: "+file.getName()+" ?", "Create new language file", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            inputLangFile.setText(file.getName());
                            htmlEditPanel.setDocument(file);
                        }
                        else {
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
                    try {
                        inputPrDir.setText(FileUtils.getRelativePath(workingDir, pDir));
                    } catch (IOException e) {
                        inputPrDir.setText(file.getName());
                    }
                    refreshTables();
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
        JLabel displayQL = new JLabel("When do you want to display a questionnaire?");
        inputQuestions = new JComboBox(new Object[]{"None", "Before", "After"});
        inputQuestions.setPreferredSize(new Dimension(100, 20));
        inputQuestions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (inputQuestions.getSelectedItem().equals("None")) {
                    inputQuestion.setEnabled(false);
                    selectQButton.setEnabled(false);
                    selectQL.setEnabled(false);

                } else {
                    selectQL.setEnabled(true);
                    inputQuestion.setEnabled(true);
                    selectQButton.setEnabled(true);
                }
            }
        });
        panel.add(displayQL);
        comboP.add(inputQuestions);
        comboP.add(Box.createHorizontalBox());
        panel.add(comboP);

        selectQL = new JLabel("Select the xml file that contains the questionnaire");
        selectQL.setEnabled(false);
        JPanel selectQPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputQuestion = new JTextField();
        inputQuestion.setPreferredSize(new Dimension(200, 20));
        inputQuestion.setEditable(false);
        inputQuestion.setEnabled(false);
        selectQButton = new JButton("Choose questionnaire file");
        selectQButton.setEnabled(false);
        selectQPanel.add(inputQuestion);
        selectQPanel.add(selectQButton);
        selectQButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = fileOpenDialog();
                if (file != null) {
                    inputQuestion.setText(file.getName());
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
        //   scrollPane.add(this.createRatiosPanel());
        //   scrollPane.add(this.createPerformancePanel());


        //   return scrollPane;

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
        askBuiltinPractL.setForeground(UIManager.getColor("Label.disabledForeground"));
        practRepeatValue = inputPractRepeat.getText();
        inputPractRepeat.setText("0");
        inputPractRepeat.setEnabled(false);
        inputPracticeFill.setEnabled(false);
        inputPracticeFill.setBackground(Color.lightGray);
        practFillL.setEnabled(false);
        inputPrDir.setText("");
        inputPrDir.setEnabled(false);
        prDirL.setEnabled(false);
        prDirButton.setEnabled(false);
        practL.setEnabled(false);
    }

    private void enablePracticeAction() {
        inputBuiltinPractice.setEnabled(true);
        askBuiltinPractL.setEnabled(true);
        askBuiltinPractL.setForeground(Color.black);
        //  inputPracticeFill.setEnabled(true);

        inputPractRepeat.setText(practRepeatValue);
        inputPractRepeat.setEnabled(true);
        inputPrDir.setText("");
        inputPrDir.setEnabled(true);
        prDirButton.setEnabled(true);
        practL.setEnabled(true);
        prDirL.setEnabled(true);
        if (inputBuiltinPractice.isSelected()) {
            enableBuiltinPracticeAction();
        } else {
            disableBuiltinPracticeAction();
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

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("AAT Config generator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

    private void writeToFile(File file) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        String firstheader = "# Configuration file for the AAT.\n" +
                "#\n" +
                "# The next options are for determining the course of the test.\n" +
                "#\n" +
                "# Trials - defines the number of trails\n" +
                "# BreakAfter - after how much Trails the test is on a break. Set this to a higher number than the trails\n" +
                "#\t       if a break is not necessary.\n" +
                "# PracticeRepeat - How many practice images are needed at the beginning of the test. The total number of images\n" +
                "# \t\t   Shown is twice this amount. (Every image has a pull and push variant). Comment this or set to 0 when you don't want a practice.\n" +
                "# DisplayQuestions - Should the questions be asked Before or After the test. Set to None for no questions\n" +
                "# ShowBoxPlot - Set this to True when you want to display a boxplot showing the results, otherwise set to False\n";
        String ratioHeader = "#The next two options are only needed when you want to change the ratio push/pull in the two conditions (push:pull). Default is 50% push and 50% #pull 1:1 ratio.\n";
        String testRatioHeader = "#This option specifies the ratio of Affective vs Neutral images (Affect:Neutral). Default is a 1:1 ratio. \n";
        String NoImagesHeader = "#This option sets the number of images shown in each trial. Normally this options doesnt need to be set. Only needed when you change the ratio('s)\n" +
                "#and want a specified number of images in each trial";
        String colorsHeader = "#The next options are for the way images should be shown on screen. A test can use the build in method that can create a colored border around\n" +
                "#an image.\n" +
                "# ColoredBorders - If this is set to True, the program will show a colored border around the image\n" +
                "# BorderColorPush - Determines the color to be used for the push images. Color is in hex value.\n" +
                "# BorderColorPull - Same for the pull images.\n" +
                "# BorderWidth - Specifies the width the border has in the center position.\n" +
                "# PracticeFillColor - Specifies the color the practice image gets. This only works if the PracticeDir is not set\n";
        String tagsHeader = "# PullTag & PushTag - These are needed when ColoredBorders is set to False. Image file names should contain these tags.";
        String dirHeader = "#Next options are for the specification of directories\n" +
                "# AffectiveDir - This is the directory containing the affective images\n" +
                "# NeutralDir - The directory containing the neutral images\n" +
                "# PracticeDir - A directory containing Practice images. If this is not set, the program will use self-generated images with the specified fill\n" +
                "# color. It will also use the border colors that are specified. \n";
        String languageHeader = "#It is possible for the same test to have different languages. \n" +
                "#LanguageFile specifies the language file used for this test. Change this value to another language file for the test to be performed in a \n" +
                "#different language.";
        String dataFileHeader = "#The next option specifies in which file the data will be saved. When not set it will default to Data.xml";
        String questionHeader = "#When a questionnaire is added to the AAT, the next option specifies which file contains those questions. ";
        String performanceHeader = "#Next options are for test performance\n" +
                "# StepSize - Determines in how many steps the image is resized. This has to be an odd number. A higher number is smoother, but setting this  \n" +
                "#\t     too high can be bad for performance. (Defaults to 31 when not set)\n" +
                "# DataSteps - Determines the accuracy for data recording. Higher value means smaller movements are recorded, but this also increases\n" +
                "#\t     the error rate. (Defaults to 9 when not set)\n" +
                "# MaxSizePerc - Determines how large the image can be. Value is percentage of the screen height. Can be >100% (Default 100%)\n" +
                "# ImageSizePerc - Determines how large the image will be when first shown on the screen. Value is percentage of the screen height. Can be >100%\n" +
                "#\t\t  (Default 50%)";
        pw.write(firstheader);
        pw.println();
        pw.write("Trials " + checkForValue(inputTrials.getText()));
        pw.println();
        pw.write("BreakAfter " + checkForValue(inputBreak.getText()));
        pw.println();
        pw.write("PracticeRepeat " + checkForValue(inputPractRepeat.getText()));
        pw.println();
        pw.write("DisplayQuestions " + checkForValue(inputQuestions.getSelectedItem().toString()));
        pw.println();
        String boxPlot = "False";
        if (inputBoxplot.isSelected()) {
            boxPlot = "True";
        }
        pw.write("ShowBoxPlot " + boxPlot);
        pw.println();
        pw.write("PlotType " + "Boxplot");
        pw.println();
        pw.write(ratioHeader);
        pw.println();
        pw.println();
        pw.write("AffectRatio " + checkForValue(inputAffectRatioPush.getText()) + ":" + checkForValue(inputAffectRatioPull.getText()));
        pw.println();
        pw.write("NeutralRatio " + checkForValue(inputNeutralRatioPush.getText()) + ":" + checkForValue(inputNeutralRatioPull.getText()));
        pw.println();
        pw.println();
        pw.write(testRatioHeader);
        pw.println();
        pw.println();
        pw.write("TestRatio " + checkForValue(inputTestRatioA.getText()) + ":" + checkForValue(inputTestRatioN.getText()));
        pw.println();
        pw.println();
        pw.write(NoImagesHeader);
        pw.println();
        pw.println();
        if (inputTrialSize.getText().length() > 0) {
            pw.write("TrialSize " + inputTrialSize.getText());
        } else {
            pw.write("# TrialSize 20");
        }
        pw.println();
        pw.println();
        pw.write(colorsHeader);
        pw.println();
        pw.println();
        String hasBorders = "False";
        if (inputColoredBorder.isSelected()) {
            hasBorders = "True";
        }
        pw.write("ColoredBorders " + hasBorders);
        pw.println();
        if (inputColoredBorder.isSelected()) {
            String pullHex = Integer.toHexString(inputPullColor.getBackground().getRGB());
            String pushHex = Integer.toHexString(inputPushColor.getBackground().getRGB());
            pullHex = pullHex.substring(2, pullHex.length());
            pushHex = pushHex.substring(2, pushHex.length());
            pw.write("BorderColorPush " + pushHex.toUpperCase());
            pw.println();
            pw.write("BorderColorPull " + pullHex.toUpperCase());
            pw.println();
            pw.write("BorderWidth " + checkForValue(inputBorderSize.getText()));
            pw.println();
        } else {
            pw.write("# BorderColorPush F5FE02");
            pw.println();
            pw.write("# BorderColorPull 00A4E7");
            pw.println();
        }
        if (inputPracticeFill.isEnabled()) {
            String fillHex = Integer.toHexString(inputPracticeFill.getBackground().getRGB());
            fillHex = fillHex.substring(2, fillHex.length());
            pw.write("PracticeFillColor " + fillHex.toUpperCase());
            pw.println();
        } else {
            pw.write("# PracticeFillColor FFDEDE");
            pw.println();
        }
        pw.println();
        pw.write(tagsHeader);
        pw.println();
        pw.println();
        if (inputColoredBorder.isSelected()) {
            pw.write("# PullTag pull");
            pw.println();
            pw.write("# PushTag push");
            pw.println();
        } else {
            if (inputPullTag.getText().length() > 0) {
                pw.write("PullTag " + inputPullTag.getText());
            } else {
                pw.write("#PullTag");
            }
            pw.println();
            if (inputPushTag.getText().length() > 0) {
                pw.write("PushTag " + inputPushTag.getText());
            } else {
                pw.write("#PushTag");
            }
            pw.println();
        }
        pw.println();
        pw.write(dirHeader);
        pw.println();
        pw.println();
        pw.write("AffectiveDir " + checkForValue(inputAffDir.getText()));
        pw.println();
        pw.write("NeutralDir " + checkForValue(inputNeutralDir.getText()));
        pw.println();
        if (inputPrDir.getText().length() > 0) {
            pw.write("PracticeDir " + checkForValue(inputPrDir.getText()));
            pw.println();
        } else {
            pw.write("# PracticeDir practice");
            pw.println();
        }
        pw.println();
        pw.write(dataFileHeader);
        pw.println();
        pw.println();
        pw.write("# Data.xml");
        pw.println();
        pw.write(languageHeader);
        pw.println();
        pw.println();
        pw.write("LanguageFile " + checkForValue(inputLangFile.getText()));
        pw.println();
        pw.println();
        pw.write(questionHeader);
        pw.println();
        pw.println();
        if (inputQuestions.getSelectedItem().equals("None")) {
            pw.write("# Questionnaire questionnaire.xml");
            pw.println();
        } else {
            if (inputQuestion.getText().length() > 0) {
                pw.write("Questionnaire " + inputQuestion.getText());
            } else {
                pw.write("#Questionnaire ");
            }
            pw.println();
        }
        pw.println();
        pw.print(performanceHeader);
        pw.println();
        pw.println();
        pw.println("StepSize " + checkForValue(inputStepSize.getText()));
        pw.println("DataSteps " + checkForValue(inputDataStepSize.getText()));
        pw.println("MaxSizePerc " + checkForValue(inputMaxSizeP.getText()));
        pw.println("ImageSizePerc " + checkForValue(inputImageSizeP.getText()));
        pw.println();
        pw.println();
        pw.println("# Unique ID value. This value is used to determine whether this file has changed since the last time the test was taken.");
        pw.println("ID " + createIDValue());
        pw.flush();
        pw.close();
        try {
            if (fw != null) {
                fw.close();
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }

        XMLWriter.writeXMLImagesList(tableA.getModel(), tableN.getModel(), tableP.getModel(), aDir, nDir, pDir);
    }


    public File fileSaveDialog(String file) {
        File export = new File(file);
        JFileChooser fc = new JFileChooser(workingDir) {
            @Override

            public void approveSelection() {

                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
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
        }
        else {
            export = null;
        }
        return export;
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

    private String createIDValue() {
        if (newTest) {
            return "1";
        } else {
            int newId = test_id; //Increase the old id with one.
            newId++;
            System.out.println("New ID value is " + newId);
            return String.valueOf(newId);
        }
    }

    private String checkForValue(String input) {
        if (input.equals("")) {
            return "NA";
        } else {
            return input;
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
        TestConfig config = new TestConfig(file);
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
        //TODO eigenlijk zou dit hardcoded images niet moeten.

        System.out.println(nDir.getAbsolutePath());

        inputPullTag.setText(config.getValue("PullTag"));
        inputPushTag.setText(config.getValue("PushTag"));
        inputQuestion.setText(config.getValue("Questionnaire"));
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
            inputQuestion.setText("");
            inputQuestion.setEnabled(false);
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
            //TODO hier aanpassen

            System.out.println("Practice directory found at " + pDir.getAbsoluteFile());
        }

        //Refresh the images tables.
        refreshTables();
    }


    private String[] getRatio(String ratio) {
        return ratio.split(":");
    }

    private Color getColor(String hex) {
        int intColor = Integer.parseInt(hex, 16);
        return new Color(intColor);
    }


}

