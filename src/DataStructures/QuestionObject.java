package DataStructures;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/31/11
 * Time: 7:50 PM
 * Data structure which contains a optional question from the configuration files. This can be used to show the question + (Answer options)
 * to the screen. The key String is used as a column header for use in a table or CSV file.
 */

public class QuestionObject {

    private String key;
    private String question;
    private ArrayList<String> options;

    public QuestionObject() {
        options = new ArrayList<String>();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void addOptions(String option) {
        options.add(option);
    }

    public String getKey() {
        return key;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}

