package DataStructures;

import DataStructures.Questionnaire.*;

import java.util.ArrayList;

/**
 * Created by marcel on 4/22/14.
 */
public class QuestionnaireTemplate {

    public static Questionnaire getTemplate() {
        String introduction =
                "<body bgcolor=\"black\">"
                        + "<h2 bgcolor=\"black\">"
                        + "Example questionaire "
                        + "</h2>";

        ArrayList<AbstractQuestion> qList = new ArrayList<AbstractQuestion>();
        OpenQuestion openQuestion = new OpenQuestion();
        openQuestion.setKey("open");
        openQuestion.setQuestion("Example open question, questions with an asterisk are required questions");
        openQuestion.setRequired(true);
        qList.add(openQuestion);

        ClosedButtonQuestion closedButtonQuestion = new ClosedButtonQuestion();
        closedButtonQuestion.setKey("closed button");
        closedButtonQuestion.setQuestion("Closed question with button options");
        closedButtonQuestion.addOptions("Option 1");
        closedButtonQuestion.addOptions("Option 2");
        closedButtonQuestion.addOptions("Option 3");
        closedButtonQuestion.addOptions("Option 4");
        qList.add(closedButtonQuestion);

        ClosedComboQuestion closedComboQuestion = new ClosedComboQuestion();
        closedComboQuestion.setKey("closed button");
        closedComboQuestion.setQuestion("Closed question with combobox options");
        closedComboQuestion.addOptions("Option 1");
        closedComboQuestion.addOptions("Option 2");
        closedComboQuestion.addOptions("Option 3");
        closedComboQuestion.addOptions("Option 4");
        closedComboQuestion.setRequired(true);
        qList.add(closedComboQuestion);

        LikertQuestion likertQuestion = new LikertQuestion();
        likertQuestion.setKey("likert");
        likertQuestion.setQuestion("7 point Likert scale");
        likertQuestion.setSize(7);
        likertQuestion.setLeft("Disagree");
        likertQuestion.setRight("Agree");
        qList.add(likertQuestion);

        SemDiffQuestion semDiffQuestion = new SemDiffQuestion();
        semDiffQuestion.setKey("likert");
        semDiffQuestion.setQuestion("Semantic differential scale");
        semDiffQuestion.setSize(7);
        semDiffQuestion.setLeft("beautiful");
        semDiffQuestion.setRight("ugly");
        qList.add(semDiffQuestion);

        OpenTextAreaQuestion textAreaQuestion = new OpenTextAreaQuestion();
        textAreaQuestion.setKey("textarea");
        textAreaQuestion.setQuestion("Use a textarea to leave a comment");
        qList.add(textAreaQuestion);

        Questionnaire q = new Questionnaire(qList, introduction);
        return q;
    }
}
