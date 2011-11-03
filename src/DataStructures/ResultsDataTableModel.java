/** This file is part of Foobar.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package DataStructures;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/14/11
 * Time: 8:24 PM
 * This class contains tableData. It's a usefull structure, not only for displaying data on the screen, but also
 * for what it is used for in this program. To easily write and read data to and from csv files.
 */

public class ResultsDataTableModel extends AbstractTableModel {
    private ArrayList<Object> data;
    private ArrayList<String> columns;
    String[] columnNames = {
            "ID",
            "Trail",
            "ImageName",
            "Direction",
            "Type",
            "Position",
            "Time",
    };


    public ResultsDataTableModel() {
        columns = new ArrayList<String>();
        data = new ArrayList<Object>();
        columns.add("ID");
        columns.add("Trail");
        columns.add("ImageName");
        columns.add("Direction");
        columns.add("Type");
        columns.add("Position");
        columns.add("Time");
        fireTableDataChanged();

    }

    public void add(ArrayList<Object> result) {
        data.addAll(result);
    }

    public int getRowCount() {
        return data.size() / getColumnCount();
    }

    //totaal aantal kolommen.
    public int getColumnCount() {
        return columns.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get((rowIndex * getColumnCount())
                + columnIndex);
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public void setValueAt(Object value, int row, int col) {
        if (row <= getRowCount()) {
            data.set(((row * getColumnCount()) + col), value);
        } else {
            data.add(value);
        }
        fireTableCellUpdated(row, col);
    }


    public boolean isCellEditable(int row, int col) {
        return true;
    }
}

