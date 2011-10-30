import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 11:28 AM
 * TableModel with the data from the participants. If the test contains no extra questions, the only thing this will contain is the
 * user id. If there are more questions asked. Then there will be more.
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
            System.out.println(columnNames[x]);
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
            System.out.println("Remove "+data.get(x));
            data.remove(x);
        }
    }

    public void display() {
        System.out.println(data);
    }

}
