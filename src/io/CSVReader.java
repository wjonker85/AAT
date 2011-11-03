/** This file is part of Foobar.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
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
    private ArrayList<String> columnNames;


    public CSVReader(File file) {

        data = new ArrayList<Object>();
        columnNames = new ArrayList<String>();
        int lines = 0;

        int fragSize = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String strLine;
            StringTokenizer st;

            int tokenNumber = 0;
            while ((strLine = br.readLine()) != null) {

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
        } catch (Exception e) {
            //  throw new CsvReaderException(e.getMessage());
        }
    }

    //Data is een vector met Sentences;
    public ArrayList<Object> getData() {
        return data;
    }

    public ArrayList<String> getColumnNames() {
        return columnNames;
    }
}