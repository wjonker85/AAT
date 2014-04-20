package views.Questionnaire.QuestionEditPanels;

import DataStructures.Questionnaire.*;

import javax.swing.*;

/**
 * Created by marcel on 3/25/14.
 */
public class EditQuestionVisitor implements IQuestionVisitor<AbstractQuestionEditPanel> {
    @Override
    public AbstractQuestionEditPanel Visit(ClosedButtonQuestion question) {
        return new ClosedQuestionEditPanel<ClosedButtonQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(ClosedComboQuestion question) {
        return new ClosedQuestionEditPanel<ClosedComboQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(LikertQuestion question) {
        return new LikertSemDiffQuestionEditPanel<LikertQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(SemDiffQuestion question) {
        return new LikertSemDiffQuestionEditPanel<SemDiffQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(OpenQuestion question) {
        return new OpenQuestionEditPanel<OpenQuestion>(question);
    }

    @Override
    public AbstractQuestionEditPanel Visit(OpenTextAreaQuestion question) {
        return new OpenQuestionEditPanel<OpenTextAreaQuestion>(question);
    }
}