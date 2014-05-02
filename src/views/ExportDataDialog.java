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

import AAT.Configuration.TestMetaData;
import AAT.Util.SpringUtilities;
import IO.DataExporter;

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
 * Simple form that gathers information about how the filtering should take place for the data that is
 * getting exported
 */
public class ExportDataDialog extends JFrame {

    private JTextField minRTime, maxRtime, errorPerc;
    private JCheckBox removeFalseCenter, practiceCheck;
    private int min, max, perc = 10;
    private TestMetaData metaData;

    public ExportDataDialog(final TestMetaData metaData) {

        this.setName("Export Data");
        this.setTitle("Export Data - options");
        int test_id = metaData.getExport_id();
        System.out.println("Using test id " + test_id);
        this.metaData = metaData;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel p = new JPanel(new SpringLayout());
        JLabel minTime = new JLabel("Min Reaction Time (ms)");
        minRTime = new JTextField(10);
        minRTime.setText("100");         //Set Default value
        minTime.setLabelFor(minRTime);
        p.add(minTime);
        p.add(minRTime);
        JLabel maxTime = new JLabel("Max Reaction Time (ms)");
        maxRtime = new JTextField(10);
        maxRtime.setText("3000");
        maxTime.setLabelFor(maxRtime);
        p.add(maxTime);
        p.add(maxRtime);
        removeFalseCenter = new JCheckBox();
        removeFalseCenter.setSelected(true);
        JLabel fcLabel = new JLabel("Remove wrong center positions");
        this.removeFalseCenter.setToolTipText("Removes all the reaction times for images where the joystick was in the wrong start position \n " +
                "(not in the centre)");
        JLabel ePercent = new JLabel("Max error percentage");
        errorPerc = new JTextField(10);
        errorPerc.setToolTipText("Maximum percentage of errors allowed in the data. Total numbers of errors is:" +
                " wrong startpositions + wrong first directions + > max & < min Reaction Time");
        p.add(fcLabel);
        p.add(removeFalseCenter);
        errorPerc.setText("25");
        ePercent.setLabelFor(errorPerc);
        p.add(ePercent);
        p.add(errorPerc);

        JLabel practiceLabel = new JLabel("Include practice in output Data");
        practiceCheck = new JCheckBox();
        practiceCheck.setToolTipText("When checked the results from the practice images will be included in the exported data");
        p.add(practiceLabel);
        p.add(practiceCheck);
        //Layout everything in a nice Form
        SpringUtilities.makeCompactGrid(p,
                5, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad


        mainPanel.add(p);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        JButton saveMeasures = new JButton("Export measurement data");
        JButton saveParticipants = new JButton("Export Questionnaire data");
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        saveMeasures.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    validateInput();
                    File saveFile = fileSaveDialog("Measures.csv");
                    setEnabled(false);
                    //  DataExporter.exportMeasurementsAnova(model, saveFile, min, max, perc, practiceCheck.isSelected(), removeFalseCenter.isSelected());
                    DataExporter.exportMeasurements(metaData, saveFile, min, max, perc, practiceCheck.isSelected(), removeFalseCenter.isSelected());

                } catch (SubmitDataException e) {
                    JOptionPane.showMessageDialog(null,
                            e.getMessage(),
                            "Problem submitting input",
                            JOptionPane.ERROR_MESSAGE);
                }
                setEnabled(true);
            }
        });

        saveParticipants.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    validateInput();
                    File saveFile = fileSaveDialog("Questionnaire.csv");
                    setEnabled(false);
                    DataExporter.exportQuestionnaire(metaData, saveFile, min, max, perc, practiceCheck.isSelected());


                } catch (SubmitDataException e) {
                    JOptionPane.showMessageDialog(null,
                            e.getMessage(),
                            "Problem submitting input",
                            JOptionPane.ERROR_MESSAGE);
                }
                setEnabled(true);
            }
        });

        controlPanel.add(saveMeasures);
        controlPanel.add(saveParticipants);
        controlPanel.add(closeButton);


        mainPanel.add(controlPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Set up the content pane.
        p.setOpaque(true);
        setContentPane(mainPanel);
        pack();
    }

    /**
     * Validates the data from the form
     */
    private void validateInput() throws SubmitDataException {

        try {
            min = Integer.parseInt(minRTime.getText());
        } catch (Exception e) {
            throw new SubmitDataException("Minimum reaction-time is not a number");
        }
        if (min < 0) {
            throw new SubmitDataException("Minimum reaction-time can't be lower than 0 ms");
        }
        try {
            max = Integer.parseInt(maxRtime.getText());
        } catch (Exception e) {
            throw new SubmitDataException("Maximum reaction-time is not a number");
        }
        if (max <= min) {
            throw new SubmitDataException("Maximum reaction-time can't be smaller or equal to the minimum reaction-time");
        }
        try {
            perc = Integer.parseInt(errorPerc.getText());
        } catch (Exception e) {
            throw new SubmitDataException("Error percentage is not a number");
        }
        if (perc < 0) {
            throw new SubmitDataException("Error percentage can't be smaller than 0%");
        }
        if (perc > 100) {
            throw new SubmitDataException("Error percentage can't be higher than 100%");
        }
    }

    /*
   Create a dialog for the selection of the file where the exported data is saved to
   Has a file filter for csv files
    */

    public File fileSaveDialog(String file) {
        File export = new File(file);
        JFileChooser fc = new JFileChooser() {
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
        FileFilter filter1 = new ExtensionFileFilter("CSV File", new String[]{"CSV", "csv"});
        fc.setFileFilter(filter1);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)

        {
            export = fc.getSelectedFile();
        }
        return export;
    }

    private class SubmitDataException extends Exception {

        public SubmitDataException(String error) {
            super(error);
        }
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
