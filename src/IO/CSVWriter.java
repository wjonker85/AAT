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

package IO;

import javax.swing.table.TableModel;
import java.io.BufferedWriter;
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


    //Write data to file. Appends if there already is data in the file
    public static boolean writeData(File file, boolean append, TableModel tableModel) {
        try {
            FileWriter writer = new FileWriter(file, append);
            BufferedWriter fbw = new BufferedWriter(writer);
            String columnNames = "";        //Create first line with column names
            if (file.length() == 0) {           //Add column headers only if file is empty

                for (int x = 0; x < tableModel.getColumnCount(); x++) {
                    if (x < tableModel.getColumnCount() - 1) {
                        columnNames += tableModel.getColumnName(x) + ",";
                    } else {
                        //     columnNames += tableModel.getColumnName(x) + "\n";
                        columnNames += tableModel.getColumnName(x);
                    }

                }
                //  writer.append(columnNames);    //First line of the file contains the column names, only when file is empty
                fbw.write(columnNames);
                fbw.newLine();
            }
            for (int itt = 0; itt < tableModel.getRowCount(); itt++) {
                String row = "";
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (i < tableModel.getColumnCount() - 1) {
                        row += tableModel.getValueAt(itt, i) + ",";
                    } else {
                        //   row += tableModel.getValueAt(itt, i) + "\n";
                        row += tableModel.getValueAt(itt, i);
                    }
                }
                //    writer.append(row);          //Add a row of data to the CSV file
                fbw.write(row);
                fbw.newLine();
            }
            //   fbw.newLine();
            fbw.close();


        } catch (IOException err) {
            System.out.println("Problem creating csv file: " + err);
            return false;
        }
        return true;
    }
}