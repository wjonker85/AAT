package DataStructures.Questionnaire;

/**
 * Created by marcel on 4/4/14.
 * Interface used to convert a question from one type to another. E.g. from likert to semantic differential
 */
public interface IChangeQuestionType {
    public <T extends AbstractQuestion> T convertQuestion(T newQuestion);
}
