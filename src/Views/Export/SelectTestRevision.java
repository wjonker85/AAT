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

package Views.Export;

import Model.AATModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


/**
 * Created by marcel on 1/19/14.
 * This frame shows the different revisions that could be available in the testdata. When there is only one revision, then this frame will never show up. When there
 * are more, a user is asked to choose between them. Only one revision at a time can be used to export data from
 */
public class SelectTestRevision extends JFrame {

    AATModel AATmodel;
    private JTable table;
    private boolean current;

    public SelectTestRevision(HashMap<String, String> testData, int current_id, AATModel AATmodel, boolean current) {

        this.setName("Test revisions");
        this.setTitle("Export Data - test selection");
        this.AATmodel = AATmodel;
        this.current = current;
        JLabel title = new JLabel("Multiple versions of the test are found in the data file. \n Please select the one to use.", SwingConstants.LEFT);
        Font f = title.getFont();

        title.setFont(new Font(f.getName(), Font.PLAIN, 14));
        JPanel mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(70, 10, 0, 0);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(title, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;

        c.insets = new Insets(20, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(createRevisionTable(testData, current_id), c);


        JButton OkButton = new JButton("OK");
        JPanel buttonPanel = new JPanel();


        OkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                okAction();
            }
        });
        JButton CancelButton = new JButton("Cancel");
        CancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 20, 0, 0);
        c.gridx = 0;
        c.gridy = 2;
        buttonPanel.add(OkButton);
        buttonPanel.add(CancelButton, c);
        mainPanel.add(buttonPanel, c);
        this.getContentPane().add(mainPanel);
        setContentPane(mainPanel);
        pack();
        //    this.setSize(new Dimension(400,200));
        this.requestFocus();


    }

    private void okAction() {
        TableModel model = table.getModel();
        for (int x = 0; x < model.getRowCount(); x++) {
            Boolean b = (Boolean) model.getValueAt(x, 2);
            if (b) {
                AATmodel.setExport_id(Integer.parseInt(model.getValueAt(x, 0).toString()), current);
                this.dispose();
                return;
            }
        }

    }

    private JPanel createRevisionTable(HashMap<String, String> testData, int current_id) {

        JPanel tablePanel = new JPanel();
        //    scrollPane.setHorizontalScrollBarPolicy(Scroll);
        table = new JTable(new RevisionTableModel(testData, current_id));
        TableColumn column;
        for (int i = 0; i < 3; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 1) {
                column.setPreferredWidth(300); //third column is bigger
            } else {
                column.setPreferredWidth(20);
            }
        }
        //   table.getColumnModel().getColumn(0).setPreferredWidth(27);
        //  table.getColumnModel().getColumn(2).setPreferredWidth(27);
        //  table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane);
        return tablePanel;

    }
}


//Table model for the two JTables that contain the image files. This model contains the data that is displayed.
//When it is the first time a test is created all image files in a directory are added. When it is a modification of an existing test
//then only the already included images are selected.
class RevisionTableModel extends AbstractTableModel {

    public RevisionTableModel(HashMap<String, String> input, int current_id) {

        data = new Object[input.size()][3];
        int x = 0;

        for (String s : input.keySet()) {
            data[x][0] = s;
            System.out.println("Test ID " + s + " " + current_id);
            int id = Integer.parseInt(s);

            if (id == current_id) {
                data[x][1] = "(Current) " + input.get(s);
                data[x][2] = Boolean.TRUE;
            } else {
                data[x][1] = input.get(s);
                data[x][2] = Boolean.FALSE;
            }
            x++;
        }

    }


    private String[] columnNames = {"Test ID",
            "Description", "Selected"};

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
        return col == 2;
    }

    private int findTrue() {
        for (int x = 0; x < data.length; x++) {
            Boolean b = (Boolean) data[x][2];
            if (b) {
                System.out.println("Row " + x);
                return x;
            }
        }
        return 0;
    }


    public void setValueAt(Object value, int row, int col) {
        int pos = findTrue();
        if (col == 2) {
            Boolean b = Boolean.parseBoolean(value.toString());
            if (b) {
                System.out.println("Change " + row);
                data[pos][2] = false;
                //   setValueAt( new Boolean(false),findTrue(),2);
                data[row][col] = value;
            }
        } else {
            data[row][col] = value;
        }
        fireTableCellUpdated(row, col);
        fireTableCellUpdated(pos, 2);
    }
}


