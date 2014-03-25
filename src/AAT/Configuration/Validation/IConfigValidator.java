package AAT.Configuration.Validation;

/**
 * Created by marcel on 3/16/14.
 */

public interface IConfigValidator<TValue> {
    public void validate (TValue value) throws FalseConfigException;
}

