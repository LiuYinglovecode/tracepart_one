package config;

import java.util.HashMap;
import java.util.Map;

public class InMemConfigManager implements IConfigManager {
	
	private Map<String, IConfig> configMap = new HashMap<String, IConfig>();
	
	@Override
	public String get(String session, String key, String defaultValue) {
		 if (configMap.containsKey(session)) {
			 IConfig config = configMap.get(session);
			 return config.get(key, defaultValue);
		 }
		 
		 return defaultValue;
	}

	@Override
	public void set(String session, String key, String value) {
		if (configMap.containsKey(session)) {
			IConfig config = configMap.get(session);
			config.set(key, value);
		} else {
			IConfig config = new InMemConfig();
			config.set(key, value);
			configMap.put(session, config);
		}
	}

	@Override
	public IConfig config(String configRoot) {
		return configMap.get(configRoot);
	}

	public Map<String, IConfig> getConfigMap() {
		return configMap;
	}

	@Override
	public String get(String session, String key) {
		if (configMap.containsKey(session)) {
			 IConfig config = configMap.get(session);
			 return config.get(key);
		 }
		return null;
	}

}
