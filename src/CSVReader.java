import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/27/11
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class CSVReader  {


    private Vector<String> data;
    private Vector<String> fragments;
    private Vector<String> configData;

    public CSVReader(File file) {

        data = new Vector<String>();
        fragments = new Vector<String>();
        configData = new Vector<String>();
        int fragSize = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String strLine;
            StringTokenizer st;

            int tokenNumber = 0;
            while ((strLine = br.readLine()) != null) {
                st = new StringTokenizer(strLine, ",");
                while (st.hasMoreTokens()) {
                    tokenNumber++;
                    String token = st.nextToken();
                    if (token.equals("<config>")) {
                        System.out.println("Config gevonden");
                     //   st.nextToken();
                        readConfig(br);
                    }
                    else if (token.equals("<person>")) {
                        fragSize++;
          //              fragments.add(valueOf(fragSize));
                        String fragment = st.nextToken();
                        fragments.add(fragment);       //voeg een nieuw fragment toe.
                    } else {
                        if (tokenNumber == 1) {
            //                data.add(valueOf(fragSize));
                            data.add(token);
                        } else {
                            data.add(token);
                        }
                    }
                }
                if (tokenNumber == 5) {
                    data.add(" ");
                }
                tokenNumber = 0;
            }

        } catch (Exception e) {
          //  throw new CsvReaderException(e.getMessage());
        }
    }

    //Data is een vector met Sentences;
    public Vector getData() {
        return data;
    }

    public Vector getFragments() {
        return fragments;
    }

    public Vector getConfigData() {
        return configData;
    }

    private void readConfig(BufferedReader br) {
        try {
            String strLine;
            StringTokenizer st;
            int tokenNumber = 0;
            while ((strLine = br.readLine()) != null && !strLine.equals("</config>")) {
                st = new StringTokenizer(strLine, ",");
                while (st.hasMoreTokens()) {
                    tokenNumber++;
                    String token = st.nextToken();
                    System.out.println(token);
                    configData.add(token);
                }
            }
        } catch (Exception e) {
           // throw new CsvReaderException(e.getMessage());
        }
    }
}

class CsvReaderException extends Exception {

    String error;

    public CsvReaderException(String err) {
        super(err);
        error = err;
    }
}
