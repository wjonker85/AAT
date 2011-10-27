import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/14/11
 * Time: 8:24 PM
 * Maak gebruik van een uitbreiding van AbstractTableModel voor een goede gestructureerde manier van data opslaan.
 * Deze class is gewoon als een tabel te gebruiken, of als input voor een JTable
 * TODO: ID toevoegen zodat er meerdere personen deze test kunnen maken.
 */




public class ResultsTableModel extends AbstractTableModel {
    private ArrayList<Object> data;
    private ArrayList<String> columns;
    String[] columnNames = {
            "ID",
            "Run",
            "ImageName",
            "Direction",
            "Type",
            "Position",
            "Time",
    };



    public ResultsTableModel() {
        columns = new ArrayList();
        data = new ArrayList();
        columns.add("ID");
        columns.add("Run");
        columns.add("ImageName");
        columns.add("Direction");
        columns.add("Type");
        columns.add("Position");
        columns.add("Time");
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



}

