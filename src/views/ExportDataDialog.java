package views;

import Model.AATModel;
import io.DataExporter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/31/11
 * Time: 4:33 PM
 * Simple form that gather information about how the filtering should take place for the data that is
 * getting exported
 */
public class ExportDataDialog extends JFrame {

    private JTextField minRTime, maxRtime, errorPerc;
    private JCheckBox includePartData, includePract;
    private AATModel model;
    private JPanel mainPanel;
    private DataExporter exporter;

    public ExportDataDialog(AATModel model) {
        this.model = model;
        this.setName("Export Data");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel p = new JPanel(new SpringLayout());
        JLabel minTime = new JLabel("Min Reaction Time");
        minRTime = new JTextField(10);
        minRTime.setText("100");         //Set Default value
        minTime.setLabelFor(minRTime);
        p.add(minTime);
        p.add(minRTime);
        JLabel maxTime = new JLabel("Max Reaction Time");
        maxRtime = new JTextField(10);
        maxRtime.setText("3000");
        maxTime.setLabelFor(maxRtime);
        p.add(maxTime);
        p.add(maxRtime);
        JLabel ePercent = new JLabel("Max error percentage");
        errorPerc = new JTextField(10);
        errorPerc.setText("25");
        ePercent.setLabelFor(errorPerc);
        p.add(ePercent);
        p.add(errorPerc);
        JLabel partData = new JLabel("Include participants data");
        includePartData = new JCheckBox();
        partData.setLabelFor(includePartData);
        p.add(partData);
        p.add(includePartData);
        JLabel inclP = new JLabel("Include practice data");
        includePract = new JCheckBox();
        includePract.setSelected(true);
        inclP.setLabelFor(includePract);
        p.add(inclP);
        p.add(includePract);

        //Layout everything in a nice Form
        SpringUtilities.makeCompactGrid(p,
                5, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad


        mainPanel.add(p);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setEnabled(false);
                setVisible(false);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (submitData()) {
                    File saveFile = fileSaveDialog();
                    exporter.writeToFile(saveFile);
                }
            }
        });
        controlPanel.add(cancelButton);
        controlPanel.add(submitButton);
        mainPanel.add(controlPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Set up the content pane.
        p.setOpaque(true);
        setContentPane(mainPanel);

        //Display the window.
        pack();
        setVisible(true);

    }

    /*
    Submits the entered values to the data exporter
     */
    private boolean submitData() {
        int min, max, perc;

        try {
            min = Integer.parseInt(minRTime.getText());
            max = Integer.parseInt(maxRtime.getText());
            perc = Integer.parseInt(errorPerc.getText());
        } catch (Exception e) {
            System.out.println("Something wrong with the entered data");
            return false;
        }
        exporter = new DataExporter(model, min, max, perc);
        return true;

    }

    /*
    Create a dialog for the selection of the file where the exported data is saved to
    Has a file filter for csv files
     */
    public File fileSaveDialog() {
        File export = new File("export.csv");
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(export);
        FileFilter filter1 = new ExtensionFileFilter("CSV File", new String[]{"CSV", "csv"});
        fc.setFileFilter(filter1);
        int returnVal = fc.showSaveDialog(this);


        if (returnVal == JFileChooser.APPROVE_OPTION) {
            export = fc.getSelectedFile();
        }
        return export;
    }
}

//filter voor de file extensions. Komt ook van het internet. Wordt nu gebruik om .input en csv bestanden te filteren.
class ExtensionFileFilter extends FileFilter {
    String description;

    String extensions[];

    public ExtensionFileFilter(String description, String extension) {
        this(description, new String[]{extension});
    }

    public ExtensionFileFilter(String description, String extensions[]) {
        if (description == null) {
            this.description = extensions[0];
        } else {
            this.description = description;
        }
        this.extensions = (String[]) extensions.clone();
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
            for (int i = 0, n = extensions.length; i < n; i++) {
                String extension = extensions[i];
                if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
        }
        return false;
    }
}
