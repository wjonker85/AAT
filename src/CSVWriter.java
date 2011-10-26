import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/26/11
 * Time: 3:54 PM
 * Output a TableModel to a CSV file.
 */
public class CSVWriter {


    private TableModel tableModel;

    public CSVWriter(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    public boolean writeData(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            String columnNames = "";        //Create first line with column names
            for (int x = 0; x < tableModel.getColumnCount(); x++) {
                if(x<tableModel.getColumnCount()-1) {
                columnNames += tableModel.getColumnName(x) + ",";
                }
                else {
                    columnNames+= tableModel.getColumnName(x)+"\n";
                }

            }
            writer.append(columnNames);    //First line of the file contains the column names

            for (int itt = 0; itt < tableModel.getRowCount(); itt++) {
                String row = "";
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if(i<tableModel.getColumnCount()-1) {
                    row += tableModel.getValueAt(itt, i)+",";
                    }
                    else {
                        row += tableModel.getValueAt(itt,i)+"\n";
                    }
                }
                writer.append(row);          //Add a row of data to the CSV file

            }
            writer.close();


        } catch (IOException err) {
            System.out.println("Problem creating csv file: " + err);
            return false;
        }
        return true;
    }
}