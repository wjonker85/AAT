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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/27/11
 * Time: 11:06 AM
 * This class reads a CSV file. First line of this file contains the columns for a table, the rest is data.
 */
public class CSVReader {


    private ArrayList<Object> data;
    private ArrayList<Object> columnNames;


    public CSVReader(File file) {

        data = new ArrayList<Object>();
        columnNames = new ArrayList<Object>();
        int lines = 0;


        //  Charset charset = Charset.forName("US-ASCII");


        try {
            Scanner scanner = new Scanner(new FileInputStream(file), "ASCII");
            while (scanner.hasNextLine()) {

                //  BufferedReader br = new BufferedReader(new FileReader(file));
                String strLine;
                StringTokenizer st;
                strLine = scanner.nextLine();
                //     while ((strLine = br.readLine()) != null) {

                st = new StringTokenizer(strLine, ",");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();

                    if (lines == 0) {        //First line contains the column names
                        columnNames.add(token);
                    } else {
                        data.add(token);
                    }
                }
                lines++;
            }
        } catch (Exception
                e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    //Data is een arrayList met alle data, geschikt om in een tableModel gezet te worden
    public ArrayList<Object> getData() {
        return data;
    }

    //Namen voor de columns van een tableModel
    public ArrayList<Object> getColumnNames() {
        return columnNames;
    }
}