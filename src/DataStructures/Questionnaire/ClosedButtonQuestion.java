package DataStructures.Questionnaire;

/**
 * Created by marcel on 3/25/14.
 */
public class ClosedButtonQuestion extends AbstractClosedQuestion {

    public ClosedButtonQuestion() {
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

    @Override
    public AbstractQuestion newInstance() {
        return new ClosedButtonQuestion();
    }

    @Override
    public <T extends AbstractQuestion> T convertQuestion(T newQuestion) {
        newQuestion.setKey(this.getKey());
        newQuestion.setQuestion(this.getQuestion());
        newQuestion.setRequired(this.isRequired());
        if (newQuestion instanceof AbstractClosedQuestion) {
            for (String s : this.getOptions()) {
                ((AbstractClosedQuestion) newQuestion).addOptions(s);
            }
        }
        return newQuestion;
    }
}
