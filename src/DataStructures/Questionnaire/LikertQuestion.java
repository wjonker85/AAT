package DataStructures.Questionnaire;

/**
 * Created by marcel on 3/25/14.
 */
public class LikertQuestion extends AbstractScaleQuestion {

    public LikertQuestion() {
        super();
    }

    @Override
    public <T> T Accept(IQuestionVisitor<T> visitor) {
        return visitor.Visit(this);
    }

    @Override
    public void Accept(IVoidQuestionVisitor visitor) {
        visitor.Visit(this);
    }
}
