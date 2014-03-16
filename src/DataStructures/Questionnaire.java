package DataStructures;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 5/5/12
 * Time: 4:16 PM
 * Simple class containing the Questionnaire data
 */
public class Questionnaire {

    private String introduction = "";
    private ArrayList<QuestionData> allQuestions;

    public Questionnaire(ArrayList<QuestionData> allQuestions, String introduction) {
        this.allQuestions = allQuestions;
        this.introduction = introduction;

    }

    public String getIntroduction() {
        return introduction;
    }

    /**
     * In the language file it is possible to add extra questions that are asked to the participant before the
     * real test is started.
     *
     * @return ArrayList with questionObjects. These objects are passed to the questionsView that displays them
     */
    public ArrayList<QuestionData> getExtraQuestions() {
        return allQuestions;
    }
}
