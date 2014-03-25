package DataStructures.Questionnaire;

/**
 * Created by marcel on 3/25/14.
 */
public interface IQuestionVisitor<TObject> {

    public TObject Visit(ClosedButtonQuestion question);
    public TObject Visit(ClosedComboQuestion question);
    public TObject Visit(LikertQuestion question);
    public TObject Visit(SemDiffQuestion question);
    public TObject Visit(OpenQuestion question);
    public TObject Visit(OpenTextAreaQuestion question);

}
