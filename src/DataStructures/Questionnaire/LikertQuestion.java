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

    @Override
    public AbstractQuestion newInstance() {
        return new LikertQuestion();
    }

    @Override
    public <T extends AbstractQuestion> T convertQuestion(T newQuestion) {
        newQuestion.setKey(this.getKey());
        newQuestion.setQuestion(this.getQuestion());
        newQuestion.setRequired(this.isRequired());
        if(newQuestion instanceof AbstractScaleQuestion)
        {
            ((AbstractScaleQuestion) newQuestion).setLeft(this.getLeft());
            ((AbstractScaleQuestion) newQuestion).setRight(this.getRight());
            ((AbstractScaleQuestion) newQuestion).setSize(this.getSize());
        }
        return newQuestion;
    }
}
