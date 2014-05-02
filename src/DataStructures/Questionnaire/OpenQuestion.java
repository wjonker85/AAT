package DataStructures.Questionnaire;

/**
 * Created by marcel on 3/25/14.
 * Open question
 */
public class OpenQuestion extends AbstractQuestion {


    public OpenQuestion() {
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
        return new OpenQuestion();
    }

    @Override
    public <T extends AbstractQuestion> T convertQuestion(T newQuestion) {
        newQuestion.setKey(this.getKey());
        newQuestion.setQuestion(this.getQuestion());
        newQuestion.setRequired(this.isRequired());
        return newQuestion;
        //     return (T) nq;
        //  return T;
    }
}
