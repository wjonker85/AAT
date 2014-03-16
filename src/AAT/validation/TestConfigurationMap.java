package AAT.validation;

import AAT.IConfigOption;
import AAT.Util.FileUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by marcel on 3/16/14.
 */
public class TestConfigurationMap<TKey> {

        private HashMap<TKey, TestConfigurationOption<String>> StringOptions;
        private HashMap<TKey, TestConfigurationOption<Integer>> IntegerOptions;
        private HashMap<TKey, TestConfigurationOption<Boolean>> BoolOptions;
        private HashMap<TKey,TestConfigurationOption<File>> FileOptions;

        public TestConfigurationMap() {
            StringOptions = new HashMap<TKey, TestConfigurationOption<String>>();
            IntegerOptions = new HashMap<TKey, TestConfigurationOption<Integer>>();
            BoolOptions = new HashMap<TKey, TestConfigurationOption<Boolean>>();
            FileOptions = new HashMap<TKey, TestConfigurationOption<File>>();
        }

        public TestConfigurationOption<String> GetSetConfigOption(TKey key, String value) throws FalseConfigException {
            TestConfigurationOption<String> option = new TestConfigurationOption<String>(value);
            System.out.println("Adding or getting config option "+key+" with value "+String.valueOf(value));
            if (!contains(key)) {
                StringOptions.put(key, option);
            } else if (StringOptions.containsKey(key)) {
                option = StringOptions.get(key);
                option.setValue(value);
            } else {
                throw new FalseConfigException(String.valueOf(key) + " already exists as a different data type");
            }
            return option;

        }

        public TestConfigurationOption<Integer> GetSetConfigOption(TKey key, int value) throws FalseConfigException {
            TestConfigurationOption<Integer> option = new TestConfigurationOption<Integer>(value);
            System.out.println("Adding or getting config option "+key+" with value "+String.valueOf(value));
            if (!contains(key)) {
                IntegerOptions.put(key, option);
            } else if (IntegerOptions.containsKey(key)) {
                option = IntegerOptions.get(key);
                option.setValue(value);
            } else {
                throw new FalseConfigException(String.valueOf(key) + " already exists as a different data type");
            }
            return option;
        }

        public TestConfigurationOption<Boolean> GetSetConfigOption(TKey key, boolean value) throws FalseConfigException {
            TestConfigurationOption<Boolean> option = new TestConfigurationOption<Boolean>(value);
            System.out.println("Adding or getting config option "+key+" with value "+String.valueOf(value));
            if (!contains(key)) {
                BoolOptions.put(key, option);
            } else if (BoolOptions.containsKey(key)) {
                option = BoolOptions.get(key);
                option.setValue(value);

            } else {
                throw new FalseConfigException(String.valueOf(key) + " already exists as a different data type");
            }
            return option;
        }

    public TestConfigurationOption<File> GetSetConfigOption(TKey key, File value) throws FalseConfigException {
        TestConfigurationOption<File> option = new TestConfigurationOption<File>(value);
        System.out.println("Adding or getting config option "+key+" with value "+String.valueOf(value));
        if (!contains(key)) {
            FileOptions.put(key, option);
        } else if (FileOptions.containsKey(key)) {
            option = FileOptions.get(key);
            option.setValue(value);

        } else {
            throw new FalseConfigException(String.valueOf(key) + " already exists as a different data type");
        }
        return option;
    }

    private boolean contains(TKey key) {
        if (StringOptions.containsKey(key) || IntegerOptions.containsKey(key) || BoolOptions.containsKey(key) || FileOptions.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    public int getSize() {
        return IntegerOptions.size() + StringOptions.size() + BoolOptions.size()+ FileOptions.size();
    }

    public boolean isValidated() {
        for (TestConfigurationOption<String> option : StringOptions.values()) {
            if(option.getValidator() == null) {
                return true; //Assume true when no validator is set.
            }
            if (!option.getValidator().validated(option.getValue())) {
                return false;
            }
        }

        for (TestConfigurationOption<Integer> option : IntegerOptions.values()) {
            if(option.getValidator() == null) {
                return true; //Assume true when no validator is set.
            }
            if (!option.getValidator().validated(option.getValue())) {
                return false;
            }
        }

        for (TestConfigurationOption<Boolean> option : BoolOptions.values()) {
            if(option.getValidator() == null) {
                return true; //Assume true when no validator is set.
            }
            if (!option.getValidator().validated(option.getValue())) {
                return false;
            }
        }

        for (TestConfigurationOption<File> option : FileOptions.values()) {
            if(option.getValidator() == null) {
                return true; //Assume true when no validator is set.
            }
            if (!option.getValidator().validated(option.getValue())) {
                return false;
            }
        }
        return true;
    }
}

class TestConfigurationOption<TValue> implements IConfigOption<TValue> {
    private TValue value;
    private IConfigValidator<TValue> validator = null;


    public TestConfigurationOption(TValue value) {
        this.value = value;
    }

    @Override
    public TValue getValue() {
        return null;
    }

    @Override
    public void setValue(TValue value) {
        this.value = value;
    }

    @Override
    public void addValidator(IConfigValidator<TValue> validator) {
        this.validator = validator;
    }

    public IConfigValidator<TValue> getValidator() {
        return validator;
    }
}

