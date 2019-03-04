package util;

import java.util.Properties;

/**
 * Config Interface for config with key and string value
 */
public interface IConfig {
	boolean hasKey(String key);
	
    void set(String key, String value);

    String get(String key);

    String get(String key, String defaultValue);

    void remove(String key);

    Properties getAll();
}
