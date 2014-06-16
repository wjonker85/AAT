/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package AAT.Configuration.Validation;

import AAT.IConfigOption;

import java.io.File;
import java.util.HashMap;

/**
 * Created by marcel on 3/16/14.
 * This is a map structure that can contain all the different variables that can be set for the configuration of the test
 * These variables can be either String, Integer, Boolean or File datatypes. Each config option can also have it's own validator
 * attached to it.
 * <p/>
 * The structure makes it ease to easy add different options to the the test and have them validated. The AAT can only be started when all the
 * validators that are assigned to the variables have returned true. Otherwise an exception is thrown with a messing telling the user about the problem.
 */
public class TestConfigurationMap<TKey> {

    private HashMap<TKey, TestConfigurationOption<String>> StringOptions;
    private HashMap<TKey, TestConfigurationOption<Integer>> IntegerOptions;
    private HashMap<TKey, TestConfigurationOption<Boolean>> BoolOptions;
    private HashMap<TKey, TestConfigurationOption<File>> FileOptions;

    public TestConfigurationMap() {
        StringOptions = new HashMap<TKey, TestConfigurationOption<String>>();
        IntegerOptions = new HashMap<TKey, TestConfigurationOption<Integer>>();
        BoolOptions = new HashMap<TKey, TestConfigurationOption<Boolean>>();
        FileOptions = new HashMap<TKey, TestConfigurationOption<File>>();
    }

    public TestConfigurationOption<String> GetSetConfigOption(TKey key, String value) throws FalseConfigException {
        TestConfigurationOption<String> option = new TestConfigurationOption<String>(value);
        System.out.println("Adding or getting config option " + key + " with value " + String.valueOf(value));
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
        System.out.println("Adding or getting config option " + key + " with value " + String.valueOf(value));
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
        System.out.println("Adding or getting config option " + key + " with value " + String.valueOf(value));
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
        System.out.println("Adding or getting config option " + key + " with value " + String.valueOf(value));
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

    public boolean contains(TKey key) {
        return StringOptions.containsKey(key) || IntegerOptions.containsKey(key) || BoolOptions.containsKey(key) || FileOptions.containsKey(key);
    }

    public int getIntValue(TKey key) throws FalseConfigException {
        if (IntegerOptions.containsKey(key)) {
            return IntegerOptions.get(key).getValue();
        } else {
            throw new FalseConfigException(String.valueOf(key) + " not found.");
        }
    }

    public String getStringValue(TKey key) throws FalseConfigException {
        if (StringOptions.containsKey(key)) {
            return StringOptions.get(key).getValue();
        } else {
            throw new FalseConfigException(String.valueOf(key) + " not found.");
        }
    }

    public File getFileValue(TKey key) throws FalseConfigException {
        if (FileOptions.containsKey(key)) {
            return FileOptions.get(key).getValue();
        } else {
            throw new FalseConfigException(String.valueOf(key) + " not found.");
        }
    }

    public boolean getBooleanValue(TKey key) throws FalseConfigException {
        if (BoolOptions.containsKey(key)) {
            return BoolOptions.get(key).getValue();
        } else {
            throw new FalseConfigException(String.valueOf(key) + " not found.");
        }
    }

    public int getSize() {
        return IntegerOptions.size() + StringOptions.size() + BoolOptions.size() + FileOptions.size();
    }

    public boolean isValidated() throws FalseConfigException {
        for (TestConfigurationOption<String> option : StringOptions.values()) {
            if (option.getValidator() != null) {

                option.getValidator().validate(option.getValue());
                System.out.println("Config option " + option.getValue() + " validated OK");
            }
        }

        for (TestConfigurationOption<Integer> option : IntegerOptions.values()) {
            if (option.getValidator() != null) {
                option.getValidator().validate(option.getValue());
                System.out.println("Config option " + option.getValue() + " validated OK");
            }
        }
        for (TestConfigurationOption<Boolean> option : BoolOptions.values()) {
            if (option.getValidator() != null) {

                option.getValidator().validate(option.getValue());
                System.out.println("Config option " + option.getValue() + " validated OK");
            }
        }
        for (TestConfigurationOption<File> option : FileOptions.values()) {
            if (option.getValidator() != null) {

                option.getValidator().validate(option.getValue());
                System.out.println("Config option " + option.getValue() + " validated OK");
            }
        }
        return true;      //When no exception is thrown, test configuration is valid and loading can continue.
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
        return value;
    }

    @Override
    public void setValue(TValue value) {
        this.value = value;
    }

    public void addValidator(IConfigValidator<TValue> validator) {
        this.validator = validator;
    }

    @Override
    public Class getType() {
        return value.getClass();
    }

    public IConfigValidator<TValue> getValidator() {
        return validator;
    }
}

