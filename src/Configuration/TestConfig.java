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

package Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/31/11
 * Time: 7:46 PM
 * <p/>
 * This class reads the configuration file belonging to a AAT Test. All the important options like Directories, no of trials, Bordercolors etc.
 * can be set in this file
 */

public class TestConfig {

    private Map<String, String> testOptions = new HashMap<String, String>();
    private File testConfig;

    //All the configuration options
    String[] options = {
            "ColoredBorders",
            "BorderColorPush",
            "BorderColorPull",
            "BorderWidth",
            "StepSize",
            "Trails",
            "BreakAfter",
            "AffectiveDir",
            "NeutralDir",
            "LanguageFile",
            "PracticeDir",
            "PracticeFillColor",
            "PracticeRepeat",
            "NoPracticeTrail",
            "ParticipantsFile",
            "DataFile"

    };

    //Constructor, fills the Hashtable with the options
    public TestConfig(File testConfig) {
        this.testConfig = testConfig;
        testOptions = new Hashtable<String, String>();
        for (String option : options) {
            testOptions.put(option, "");
        }
        readConfig();
    }

    /*
    Reads the configuration file. Every time the reader discovers a key that is in the options list. It reads it's value from
    the configuration file and updates it's value in the HashMap
     */
    private void readConfig() {
        String strLine;
        StringTokenizer st;
        try {
            BufferedReader br = new BufferedReader(new FileReader(testConfig));
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("#")) {
                    strLine = null;
                }
                if (strLine != null) {
                    st = new StringTokenizer(strLine, " ");
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (testOptions.containsKey(token)) {
                            for (Map.Entry<String, String> entry : testOptions.entrySet()) {
                                if (entry.getKey().equals(token)) {
                                    entry.setValue(st.nextToken());
                                    strLine = null;
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public String getValue(String key) {
        return testOptions.get(key);
    }
}
