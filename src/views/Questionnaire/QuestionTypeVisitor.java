package views.Questionnaire;

import DataStructures.Questionnaire.*;

/**
 * Created by marcel on 4/4/14.
 * Visitor which gives the translation used in the selection combo box.
 */
public class QuestionTypeVisitor implements IQuestionVisitor<String> {
    @Override
    public String Visit(ClosedButtonQuestion question) {
        return "Closed question (buttons)";
    }

    @Override
    public String Visit(ClosedComboQuestion question) {
        return "Closed question (combo-box)";
    }

    @Override
    public String Visit(LikertQuestion question) {
        return "Likert Scale";
    }

    @Override
    public String Visit(SemDiffQuestion question) {
        return "Semantic differential";
    }

    @Override
    public String Visit(OpenQuestion question) {
        return "Open question";
    }

    @Override
    public String Visit(OpenTextAreaQuestion question) {
        return "Open question with text area";
    }
}
