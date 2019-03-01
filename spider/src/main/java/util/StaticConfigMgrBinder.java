package util;


public class StaticConfigMgrBinder {
    /**
     * The unique instance of this class.
     */
    private static final StaticConfigMgrBinder SINGLETON = new StaticConfigMgrBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticLoggerBinder singleton
     */
    public static final StaticConfigMgrBinder getSingleton() {
        return SINGLETON;
    }

    public ConfigClient getIConfigManager() {
        return ConfigClient.instance();
    }
}

