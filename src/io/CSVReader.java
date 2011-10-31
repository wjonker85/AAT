package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

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
                    }
                    else {
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