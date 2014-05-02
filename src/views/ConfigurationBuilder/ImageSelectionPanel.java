package views.ConfigurationBuilder;

import IO.XMLWriter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static IO.XMLReader.getIncludedFiles;

/**
 * Created by marcel on 3/16/14.
 * This is a panel that is shown in the configuration builder. It consists of two or three tables (depending on whether there are practice images).
 * The tables show the available image files in the directories that were selected. When this is the first time a AATConfig file is being created all images
 * are selected. When the AATConfig is saved, a included.xml file is written in the image directories containing all the files that are selected to be included in the AAT
 * <p/>
 * When an existing AATConfig is opened, these included.xml files are read and the images that are present there will be selected. It will load all images in the selected
 * directories, but will only have the checkbox checked when they are present in the included.xml file.
 */
public class ImageSelectionPanel extends JPanel {

    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpeg|jpg|png|gif|bmp))$)";
    /**
     * Filter so that only the image files in a directory will be selected
     */
    java.io.FileFilter extensionFilter = new java.io.FileFilter() {
        public boolean accept(File file) {
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };
    private JTable tableA, tableN, tableP;
    private File nDir = null, aDir = null, pDir = null;
    private boolean newTest;
    private JLabel pLabel;
    private JScrollPane scrollPaneP;
    //regex for extension filtering
    private Pattern pattern;

    public ImageSelectionPanel() {
        pattern = Pattern.compile(IMAGE_PATTERN); //create regex
        JPanel panel = new JPanel();
        this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 2));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        JLabel title = new JLabel("Please select the images you want to include in the test.", SwingConstants.LEFT);
        Font f = title.getFont();
        title.setFont(new Font(f.getName(), Font.PLAIN, 24));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(title, c);

        JLabel aLabel = new JLabel("Affective Images");
        Font f2 = aLabel.getFont();
        aLabel.setFont(new Font(f2.getName(), Font.BOLD, 16));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.insets = new Insets(70, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        panel.add(aLabel, c);

        tableA = new JTable();
        tableA.setBackground(Color.decode("#eeece9"));
        tableA.setComponentPopupMenu(new JPopupMenu());
        tableA.setFillsViewportHeight(true);

        JScrollPane scrollPaneA = new JScrollPane(tableA);
        scrollPaneA.setMaximumSize(new Dimension(350, 500));
        scrollPaneA.setPreferredSize(new Dimension(350, 500));

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
        Font f3 = nLabel.getFont();
        nLabel.setFont(new Font(f3.getName(), Font.BOLD, 16));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(70, 0, 0, 0);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(nLabel, c);

        tableN = new JTable();
        tableN.setBackground(Color.decode("#eeece9"));
        setTableColumnWidths(tableN);
        JScrollPane scrollPaneN = new JScrollPane(tableN);
        scrollPaneN.setMaximumSize(new Dimension(350, 500));
        scrollPaneN.setPreferredSize(new Dimension(350, 500));

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

        pLabel = new JLabel("Practice Images");
        Font f4 = pLabel.getFont();

        pLabel.setFont(new Font(f4.getName(), Font.BOLD, 16));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(70, 0, 0, 0);
        c.gridx = 2;
        c.gridy = 1;
        panel.add(pLabel, c);
        tableP = new JTable();
        tableP.setBackground(Color.decode("#eeece9"));
        setTableColumnWidths(tableP);

        scrollPaneP = new JScrollPane(tableP);
        scrollPaneP.setMaximumSize(new Dimension(350, 500));
        scrollPaneP.setPreferredSize(new Dimension(350, 500));
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
        this.add(panel);
    }

    /**
     * Refresh the image data for the two or three tables with image files
     *
     * @param nDir neutral images
     * @param aDir affective images
     * @param pDir practice images
     */
    public void updateTableData(File nDir, File aDir, File pDir, boolean newTest) {
        this.nDir = nDir;
        this.aDir = aDir;
        this.pDir = pDir;
        this.newTest = newTest;
        refreshTables();
    }

    /**
     * Loads all image files in a given directory. Extension filter with regular expression.
     *
     * @param dir Directory containing images
     * @return ArrayList<File> with all image files in a directory
     */
    public ArrayList<File> getImages(File dir) {
        if (dir != null) {
            if (dir.length() > 0) {
                if (dir.exists()) {
                    File[] files = dir.listFiles(extensionFilter);

                    return new ArrayList<File>(Arrays.asList(files));
                } else return new ArrayList<File>();
            } else return new ArrayList<File>();
        }
        return new ArrayList<File>();
    }

    private void setTableColumnWidths(JTable table) {
        if (table.getRowCount() > 0) {
            TableColumn column;
            for (int i = 0; i < 2; i++) {
                column = table.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(150); //third column is bigger
                } else {
                    column.setPreferredWidth(10);
                }
            }
        }
    }

    /**
     * Update all the image data. Sets the correct checkboxes bases on what was previously selected in an earlier configuration.
     * When this is a new configuration all the images found in the directories are selected.
     */
    private void refreshTables() {
        ArrayList<File> imageFilesA = getImages(aDir);
        ArrayList<File> imageFilesN = getImages(nDir);
        System.out.println("REFRESH " + aDir.getAbsolutePath());
        ArrayList<String> aFiles = getIncludedFiles(aDir);  //These lists contain the files that were specified for this test.
        ArrayList<String> nFiles = getIncludedFiles(nDir);

        tableA.setModel(new ImageTableModel(imageFilesA, aFiles));
        tableN.setModel(new ImageTableModel(imageFilesN, nFiles));
        setTableColumnWidths(tableA);
        setTableColumnWidths(tableN);
        tableA.repaint();
        tableN.repaint();


        if (pDir != null) {          //Add the images when a practice dir is selected
            ArrayList<File> imageFilesP = getImages(pDir);
            ArrayList<String> pFiles = getIncludedFiles(pDir);
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

    public void writeToFile() {
        XMLWriter.writeXMLImagesList(tableA.getModel(), tableN.getModel(), tableP.getModel(), aDir, nDir, pDir);
    }

    //Table model for the two JTables that contain the image files. This model contains the data that is displayed.
    //When it is the first time a test is created all image files in a directory are added. When it is a modification of an existing test
    //then only the already included images are selected.
    class ImageTableModel extends AbstractTableModel {

        private String[] columnNames = {"Image File",
                "Included"};
        private Object[][] data;

        public ImageTableModel(ArrayList<File> imageFiles, ArrayList<String> includedFiles) {
            data = new Object[imageFiles.size()][2];
            int x = 0;
            for (File f : imageFiles) {
                data[x][0] = f.getName();
                if (newTest || includedFiles.size() == 0) {
                    data[x][1] = Boolean.TRUE;
                } else {
                    if (includedFiles.contains(f.getName())) {
                        data[x][1] = Boolean.TRUE;
                    } else {
                        data[x][1] = Boolean.FALSE;
                    }
                }
                x++;
            }


        }

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
            return col >= 1;
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }
}
