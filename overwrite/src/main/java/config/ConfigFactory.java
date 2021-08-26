package config;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Factory class to build mercury config
 *
 * @author shanyou
 */
public final class ConfigFactory {
    private static final String STATIC_CONFIG_BINDER_PATH = "com/yunlu/bde/common/config/imp/StaticConfigMgrBinder.class";
    private static boolean inited = false;

    // private constructor prevents instantiation
    private ConfigFactory() {
    }

    private static void init() {
        Set<URL> staticConfigBinderPathSet = new LinkedHashSet<URL>();
        ClassLoader configFactoryClassLoader = ConfigFactory.class.getClassLoader();
        try {
            Enumeration<URL> paths;
            if (configFactoryClassLoader == null) {
                paths = ClassLoader.getSystemResources(STATIC_CONFIG_BINDER_PATH);
            } else {
                paths = configFactoryClassLoader.getResources(STATIC_CONFIG_BINDER_PATH);
            }

            while (paths.hasMoreElements()) {
                URL path = paths.nextElement();
                staticConfigBinderPathSet.add(path);
            }
        } catch (IOException ioe) {
            //
            inited = false;
            return;
        }

        if (staticConfigBinderPathSet.size() > 0) {
            //TODO: notify multi config implement
        }


        inited = true;
    }

    /**
     * Get config Mgr
     *
     * @return ConfigManager
     */
    public static IConfigManager getConfigMgr() {
        if (!inited) {
            init();
        }

        if (inited) {
            // random load IConfigManager
            return StaticConfigMgrBinder.getSingleton().getIConfigManager();
        } else {
            throw new IllegalStateException("Unexpected initialization failure");
        }
    }
}
