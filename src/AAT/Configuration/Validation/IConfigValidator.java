package AAT.Configuration.Validation;

/**
 * Created by marcel on 3/16/14.
 * Interface defining a configuration validator. Configuration can be of different datatypes and can be assigned to every variable thas is present in
 * the config file
 */

public interface IConfigValidator<TValue> {
    public void validate(TValue value) throws FalseConfigException;
}

