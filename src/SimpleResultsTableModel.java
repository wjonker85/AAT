import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleResultsTableModel extends AbstractTableModel {

    private ArrayList<Object> data;
    private ArrayList<String> columns;
    String[] columnNames = {
            "Run",
            "ImageName",
            "Direction",
            "Type",
            "ReactionTime",
    };


    public SimpleResultsTableModel() {
        columns = new ArrayList<String>();
        data = new ArrayList<Object>();
        columns.add("Run");
        columns.add("ImageName");
        columns.add("Direction");
        columns.add("Type");
        columns.add("ReactionTime");
        fireTableDataChanged();

    }

    public void add(ArrayList<Object> result) {
        data.addAll(result);
        System.out.println("Results " + data.size());
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

