package views.TestUpgrade;

import DataStructures.AATDataRecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Ask for user input to define the labels used for the neutral and affective category of images.
 */
public class LabelInputFrame extends JFrame {

    private AATDataRecorder AATDataRecorder;
    private JComboBox<String> affBox, pullBox;
    private JTextField neutField, pushField;

    public LabelInputFrame(final String[] typeLabels, final String[] dirLabels, final AATDataRecorder AATDataRecorder) {
        super("Upgrading old test data");
        this.AATDataRecorder = AATDataRecorder;


        JLabel title = new JLabel("<html>You are using data from an older version of the AAT. <br> Your data file will be upgraded." +
                "<br>Please make sure the labels for the affective and neutral category are correct<br> Also change the push and pull labels to the " +
                "correct values. </html>", SwingConstants.LEFT);
        Font f = title.getFont();

        title.setFont(new Font(f.getName(), Font.BOLD, 14));
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JPanel labelPanel = new JPanel();

        JPanel mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;

        labelPanel.add(title);
        JLabel affLabel = new JLabel("label for the affective category: ");
        //   c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        //  c.weightx = 1;
        mainPanel.add(affLabel, c);


        affBox = new JComboBox<String>(typeLabels);
        affBox.setSelectedItem(typeLabels[0]);
        //  c.gridwidth = 4;
        c.insets = new Insets(20, 10, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        //  c.weightx = 1;
        mainPanel.add(affBox, c);


        JLabel neutLabel = new JLabel("label for the neutral category: ");
        //  c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        //   c.weightx = 1;
        mainPanel.add(neutLabel, c);

        neutField = new JTextField(typeLabels[1]);
        //  c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 3;
        c.gridy = 1;
        //  c.weightx = 1;
        mainPanel.add(neutField, c);

        neutField.setEditable(false); //Just set to readonly

        affBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                int index = affBox.getSelectedIndex();
                index = (index + 1) % 2;
                neutField.setText(typeLabels[index]);
            }
        });

        JLabel pullLabel = new JLabel("label for the pull images: ");
        //   c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 2;
        //  c.weightx = 1;
        mainPanel.add(pullLabel, c);


        pullBox = new JComboBox<String>(dirLabels);
        pullBox.setSelectedItem(dirLabels[0]);
        //  c.gridwidth = 4;
        c.insets = new Insets(20, 10, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 2;
        //  c.weightx = 1;
        mainPanel.add(pullBox, c);


        JLabel pushLabel = new JLabel("label for the push images: ");
        //  c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 3;
        //   c.weightx = 1;
        mainPanel.add(pushLabel, c);

        pushField = new JTextField(dirLabels[1]);
        //  c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 3;
        c.gridy = 3;
        //  c.weightx = 1;
        mainPanel.add(pushField, c);

        neutField.setEditable(false); //Just set to readonly

        pullBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                int index = pullBox.getSelectedIndex();
                index = (index + 1) % 2;
                pushField.setText(dirLabels[index]);
            }
        });


        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                okAction();
            }
        });

        c.fill = GridBagConstraints.HORIZONTAL;
        //   c.gridwidth = 2;
        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 1;
        c.gridy = 4;
        //   c.weightx = 0.5;
        mainPanel.add(okButton, c);
        content.add(labelPanel);
        content.add(mainPanel);
        this.getContentPane().add(content);
        //    this.setPreferredSize(new Dimension(400,400));
    }

    public void display() {
        this.setEnabled(true);
        this.setVisible(true);
        pack();
        //    this.setSize(new Dimension(400,200));
        this.requestFocus();
    }


    private void okAction() {
        AATDataRecorder.continueUpgrade(affBox.getSelectedItem().toString(), neutField.getText(), pullBox.getSelectedItem().toString(), pushField.getText());
        //     AATDataRecorder.affLabelOldData = affBox.getSelectedItem().toString();
        //     AATDataRecorder.neutLabelOldData = neutField.getText();
        this.dispose();
    }
}


