package AAT;

import AAT.validation.IConfigValidator;

/**
 * Created by marcel on 3/16/14.
 */

  public interface IConfigOption<TValue> {
        public TValue getValue();

        public void setValue(TValue value);

        public IConfigValidator<TValue> getValidator();

        public void addValidator(IConfigValidator<TValue> validator);

        public Class getType();

    }

