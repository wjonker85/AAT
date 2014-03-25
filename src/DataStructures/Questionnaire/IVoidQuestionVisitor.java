package DataStructures.Questionnaire;

/**
 * Created by marcel on 3/25/14.
 */
public interface IVoidQuestionVisitor {
    public void Visit(ClosedButtonQuestion question);
    public void Visit(ClosedComboQuestion question);
    public void Visit(LikertQuestion question);
    public void Visit(SemDiffQuestion question);
    public void Visit(OpenQuestion question);
    public void Visit(OpenTextAreaQuestion question);
}
