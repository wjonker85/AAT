package AAT.Configuration.Validation;

/**
 * Created by marcel on 3/16/14.
 * Special type of exception used to identify errors in the configuration file
 */

/**
 * False configuration exception
 */
public class FalseConfigException extends Exception {

    public FalseConfigException(String error) {
        super(error);
    }

}
