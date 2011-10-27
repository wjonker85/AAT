import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.io.*;

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
                       FileInputStream test = new FileInputStream(file);
            int checkEmpty = test.read();
            test.close();
            FileWriter writer = new FileWriter(file,true);
            if(checkEmpty == -1 )   {             //Check if file is empty

               System.out.println("Empty file");
            String columnNames = "";        //Create first line with column names

            for (int x = 0; x < tableModel.getColumnCount(); x++) {
                if(x<tableModel.getColumnCount()-1) {
                columnNames += tableModel.getColumnName(x) + ",";
                }
                else {
                    columnNames+= tableModel.getColumnName(x)+"\n";
                }

            }
            writer.append(columnNames);    //First line of the file contains the column names, only when file is empty
            }
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