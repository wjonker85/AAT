package AAT;

import AAT.Configuration.Validation.IConfigValidator;

/**
 * Created by marcel on 3/16/14.
 * This interface defines configuration options. It can be used to set values of different datatabes and attach validators to them
 */

public interface IConfigOption<TValue> {
    public TValue getValue();

    public void setValue(TValue value);

    public Class getType();

}

