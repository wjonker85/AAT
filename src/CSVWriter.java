import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/26/11
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSVWriter {


    private AbstractTableModel tableModel;

    public CSVWriter(AbstractTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public boolean writeData(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            String head = "";        //Create first line with column names
            for (int x = 0; x < tableModel.getColumnCount(); x++) {
                head += tableModel.getColumnName(x) + ",";
            }
            head += "\n";
            writer.append(head);

            for (int itt = 0; itt < tableModel.getRowCount(); itt++) {
                String row = "";
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    row += tableModel.getValueAt(itt, i)+",";
                }
                row += "\n";
                writer.append(row);

            }
            writer.close();


        } catch (IOException err) {
            System.out.println("Problem creating csv file: " + err);
            return false;
        }
        return true;
    }
}