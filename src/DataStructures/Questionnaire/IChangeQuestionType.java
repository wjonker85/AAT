package DataStructures.Questionnaire;

/**
 * Created by marcel on 4/4/14.
 */
public interface IChangeQuestionType {
    public <T extends AbstractQuestion> T convertQuestion(T newQuestion);
}
