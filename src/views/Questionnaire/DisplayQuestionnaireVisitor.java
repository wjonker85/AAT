package views.Questionnaire;

import DataStructures.Questionnaire.*;
import views.Questionnaire.QuestionPanels.*;

import javax.swing.*;

/**
 * Created by marcel on 3/25/14.
 */
public class DisplayQuestionnaireVisitor implements IQuestionVisitor<AbstractQuestionPanel> {
    @Override
    public AbstractQuestionPanel Visit(ClosedButtonQuestion question) {
        return new ClosedButtonsPanel(question.getOptions().toArray(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(ClosedComboQuestion question) {
        return new ClosedComboPanel(question.getOptions().toArray(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(LikertQuestion question) {
       return new LikertPanel(question.getSize(), question.getLeft(), question.getRight(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(SemDiffQuestion question) {
       return new SemDiffPanel(question.getSize(), question.getLeft(), question.getRight(), question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(OpenQuestion question) {
        return new OpenQuestionPanel(question.isRequired());
    }

    @Override
    public AbstractQuestionPanel Visit(OpenTextAreaQuestion question) {
        return new TextAreaPanel(question.isRequired());
    }
}