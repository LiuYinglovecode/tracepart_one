package config;

/**
 * Listener for config changed
 *
 * @author shanyou
 */
public interface IConfigChangeListener {
    /**
     * Changed config with key and value
     *
     * @param key
     * @param value
     */
    void OnValueChanged(String key, String value);

    /**
     * remove key for config runtime
     *
     * @param key
     */
    void OnKeyRemoved(String key);
}
