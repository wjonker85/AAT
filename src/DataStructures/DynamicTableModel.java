package DataStructures;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 11:28 AM
 * Dynamic table model, which means that this table is flexibel in it's number of columns. The number of columns and
 * their names can be defined with an ArrayList containing them. This is especially useful for storing the answers to
 * the optional questions. This way the number of questions that can be asked is unlimited.
 */
public class DynamicTableModel extends AbstractTableModel {


    private ArrayList<Object> data;
    private ArrayList<String> columns;
    String[] columnNames;



    //Create the number of column dynamic.
    public DynamicTableModel() {
        columns = new ArrayList<String>();
        data = new ArrayList<Object>();
    }

    public void setColumnNames(ArrayList<String> columns) {
        columnNames = new String[columns.size()];
        this.columns = columns;
        for(int x = 0;x<columns.size();x++) {
            columnNames[x]=columns.get(x);
        }
        fireTableDataChanged();
    }

    public void add(ArrayList result) {
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

    public void removeRow(int row) {
        int startPos = row*getColumnCount();
        int endPos = startPos+getColumnCount()-1;
        for(int x = endPos;x>=startPos;x--) {
            data.remove(x);
        }
    }

    public void display() {
        System.out.println(data);
    }

}
