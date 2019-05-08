package config;


import config.IConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * Wrap {@link Properties} for impelment IConfig
 * @author shanyou
 *
 */
public class InMemConfig implements IConfig {
	protected Properties properties = new Properties();
	
	public InMemConfig() {
		
	}
	
	public InMemConfig(String configFile) throws IOException {
		File confFile = new File(configFile);
		if (!confFile.exists()) {
			throw new IllegalArgumentException("config file not exists");
		}
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(confFile));
		Set<Object> keys = prop.keySet();
		for(Object key:keys){  
            this.properties.setProperty(String.valueOf(key), String.valueOf(prop.get(key)));
        }
	}
	
	public InMemConfig(IConfig config) {
		this(config.getAll());
	}
	
	public InMemConfig(Properties prop) {
		Set<Object> keys = prop.keySet();
		for(Object key:keys){  
            this.properties.setProperty(String.valueOf(key), String.valueOf(prop.get(key)));
        }
	}
	@Override
	public void set(String key, String value) {
		properties.setProperty(key, value);
		
	}

	@Override
	public String get(String key) {
		return properties.getProperty(key);
	}

	@Override
	public String get(String key, String defaultValue) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		} else {
			properties.setProperty(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public void remove(String key) {
		properties.remove(key);
		
	}

	@Override
	public Properties getAll() {
		return properties;
	}

	@Override
	public boolean hasKey(String key) {
		return properties.containsKey(key);
	}

}
