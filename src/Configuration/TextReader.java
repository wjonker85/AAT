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

package Configuration;

import AAT.QuestionObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/31/11
 * Time: 7:48 PM
 * <p/>
 * Reads from the language file specified in the test configuration. This makes it possible to use the same test in
 * different languages. This file can also contain optional questions that a researcher might be interested in.
 */

public class TextReader {

    private Map<String, String> testText = new HashMap<String, String>();
    private ArrayList<QuestionObject> extraQuestions;

    private File languageFile;


    private ArrayList<String> questionKeys;

    //All the configuration options
    String[] options = {              //Keys defining the different texts
            "Introduction",
            "Start",
            "Break",
            "Finished"
    };


    public TextReader(File languageFile) {
        this.languageFile = languageFile;
        for (int x = 0; x < options.length; x++) {
            testText.put(options[x], "");
        }
        questionKeys = new ArrayList<String>();
        questionKeys.add("<Question>");         //Keys defining questions
        questionKeys.add("</Question>");
        questionKeys.add("<Option>");
        questionKeys.add("<Key>");
        extraQuestions = new ArrayList<QuestionObject>();
        readConfig();
    }

    //Remove < and > character from a string
    private String transform(String s) {
        s = s.replace("<", "");
        s = s.replace(">", "");
        return s;
    }

    /*
    This method reads the Language file defined in the config file. This file contains the different texts that are showed during
    the test. This file also contains optional extra questions that can be asked to the participant at the start of a new test.
    For example ask for gender or age.
     */
    private void readConfig() {
        String strLine;
        String key = "";
        String text = "";
        QuestionObject newQuestion = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(languageFile));
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("#")) {
                    strLine = null;
                }
                assert strLine != null;
                if (strLine.startsWith(("<"))) {
                    if (!strLine.startsWith("</")) {
                        if (strLine.startsWith("<Question>")) {     //Line indicates that there is a question.
                            newQuestion = new QuestionObject("");
                        } else if (testText.containsKey(transform(strLine))) {
                            key = transform(strLine);
                            text = "";
                        } else {
                            readQuestion(strLine, newQuestion);
                        }
                    } else {
                        if (strLine.startsWith("</Question")) {    //End of the question
                            extraQuestions.add(newQuestion);
                            newQuestion = new QuestionObject("");
                        }
                    }
                } else {
                    if (testText.containsKey(key)) {
                        text += strLine + "\n";      //Read text lines
                    }
                }
                if (!strLine.startsWith("</")) {
                    for (Map.Entry<String, String> entry : testText.entrySet()) {       //Replace value
                        if (entry.getKey().equals(key)) {
                            entry.setValue(text);
                            strLine = null;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    //Returns the requested value belonging to a key in the Hashmap
    public String getValue(String key) {
        return testText.get(key);
    }

    /*
    Input from the languagefiles is checked for lines that indicate there is an extra question
    When there is a <Question> a new question is available. The <Text> is followed by the question line
    <Option> is followed by an answer option. When there are no options given, then this question is a open question.
    <Key> is the name the variable for the answers will be given.
     */
    private void readQuestion(String line, QuestionObject question) {
        StringTokenizer st = new StringTokenizer(line, " ");
        String token = st.nextToken();
        String result = "";

        if (line.length() > token.length()) {
            result = line.substring(token.length() + 1);
        }
        if (token.startsWith("<Key")) {
            question.setKey(result);
        }
        if (token.startsWith("<Text>")) {
            question.setQuestion(result);
        }
        if (token.startsWith("<Option>")) {
            question.addOptions(result);
        }
    }

    public ArrayList<QuestionObject> getExtraQuestions() {
        return extraQuestions;
    }
}
