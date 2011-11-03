package io;

import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/26/11
 * Time: 3:54 PM
 * Output a TableModel to a CSV file. Can use any TableModel as input
 */
public class CSVWriter {


    private TableModel tableModel;

    public CSVWriter(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    //Write data to file. Appends if there already is data in the file
    public boolean writeData(File file, boolean append) {
        boolean newFile = !file.exists();
      //  boolean appendTo
      //  if(append && newFile) {

     //   }

        try {

            FileWriter writer = new FileWriter(file, append);
            String columnNames = "";        //Create first line with column names
            if (newFile) {           //Add column headers only if file is empty

                for (int x = 0; x < tableModel.getColumnCount(); x++) {
                    if (x < tableModel.getColumnCount() - 1) {
                        columnNames += tableModel.getColumnName(x) + ",";
                    } else {
                        columnNames += tableModel.getColumnName(x) + "\n";
                    }

                }
                writer.append(columnNames);    //First line of the file contains the column names, only when file is empty
            }
            for (int itt = 0; itt < tableModel.getRowCount(); itt++) {
                String row = "";
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (i < tableModel.getColumnCount() - 1) {
                        row += tableModel.getValueAt(itt, i) + ",";
                    } else {
                        row += tableModel.getValueAt(itt, i) + "\n";
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