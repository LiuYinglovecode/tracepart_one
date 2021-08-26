package config;

/**
 * Get Config by session
 */
public interface IConfigManager {
	static final String DEFUALT_CONFIG_PROPERTY = "yunlu.bde.zookeeper";
    /**
     * Get config
     *
     * @param session      config group
     * @param key          config key
     * @param defaultValue for config key
     * @return config value
     */
    String get(String session, String key, String defaultValue);


    /**
     * Get Config with out default value
     * @param session
     * @param key
     * @return
     */
    String get(String session, String key);
    /**
     * set config
     *
     * @param session
     * @param key
     * @param value
     * @return
     */
    void set(String session, String key, String value);

    /**
     * Get Iconfig for different section
     *-
     * @param configRoot
     * @return
     */
    IConfig config(String configRoot);
}
