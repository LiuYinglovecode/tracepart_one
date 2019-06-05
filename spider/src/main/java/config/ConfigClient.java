package config;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author shanyou manage config with zookeeper
 */
public class ConfigClient implements Watcher, IConfigManager {

    public static final int kDefaultZKTimeout = 300000;

    public static final String kDefaultZKConfigPath = "conf/zookeeper.properties";

    public static final String kDefaultZKConnectionString = "192.168.125.136:2181";

    public static final String kDefaultConfigRoot = "/conf_yunlu.bde";

    private static Logger logger = LoggerFactory.getLogger(ConfigClient.class);

    private static final Object lockobj = new Object();

    private static ConfigClient configClient = null;

    /**
     * zookeeper connections str
     */
    private String zkConnectionString;

    /**
     * Internal zookeeper instance
     */
    private ZooKeeper zooKeeper = null;

    /**
     * if remote config can be changed
     */
    private boolean readOnly = true;

    /**
     * properties map with difference configuration section
     */
    private Map<String, IConfig> configMap;

    /**
     * @param hostPort hostPort zk connections string ip:port
     * @param readOnly {@link this.readOnly}
     * @throws IOException
     */
    public ConfigClient(String hostPort, boolean readOnly) throws IOException {
        configMap = new HashMap<String, IConfig>();
        zooKeeper = new ZooKeeper(hostPort, kDefaultZKTimeout, this);
        this.zkConnectionString = hostPort;
        this.readOnly = readOnly;
    }

    /**
     * @param hostPort hostPort zk connections string ip:port
     * @throws IOException
     */
    public ConfigClient(String hostPort) throws IOException {
        this.zkConnectionString = hostPort;
        configMap = new HashMap<String, IConfig>();
        zooKeeper = new ZooKeeper(hostPort, kDefaultZKTimeout, this);
    }

    /**
     * Init config client instance when application start
     *
     * @param zkHostAndPort
     */
    public static synchronized ConfigClient initConfigClient(String zkHostAndPort) {
        if (configClient != null) {
            configClient.close();
            configClient = null;
        }

        try {
            configClient = new ConfigClient(zkHostAndPort);
        } catch (IOException e) {
            logger.error("create config client error", e);
            e.printStackTrace();
        }

        return configClient;
    }

    /**
     * Init config client instance when application start
     *
     * @param zkHostAndPort
     * @param readOnly      if remote config is read only
     * @return
     */
    public static synchronized ConfigClient initConfigClient(String zkHostAndPort, boolean readOnly) {
        if (configClient != null) {
            configClient.close();
            configClient = null;
        }

        try {
            configClient = new ConfigClient(zkHostAndPort, readOnly);
        } catch (IOException e) {
            logger.error("create config client error", e);
            e.printStackTrace();
        }

        return configClient;
    }

    /**
     * create config client by default settings or given properties
     *
     * @return Config client instance
     */
    public static synchronized ConfigClient instance() {
        if (configClient == null) {
            try {
                String zkQuorum = loadZooKeeperQuorum();
                configClient = new ConfigClient(zkQuorum);
            } catch (IOException e) {
                logger.error("create config client error", e);
                e.printStackTrace();
            }
        }
        return configClient;
    }

    /**
     * load zookeeper config 1. system environment 2. java environment 3. config
     * file in current working directory conf/zookeeper.properites 4.
     * /app.properties
     *
     * @return zookeeper connection string
     */
    private static String loadZooKeeperQuorum() {
        String zkQuorum = System.getProperty(DEFUALT_CONFIG_PROPERTY);
        if (StringUtils.isNotEmpty(zkQuorum)) {
            return zkQuorum;
        }

        zkQuorum = System.getenv(DEFUALT_CONFIG_PROPERTY);
        if (StringUtils.isNotEmpty(zkQuorum)) {
            return zkQuorum;
        }

        File confFile = new File(kDefaultZKConfigPath);
        if (confFile.exists()) {
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(confFile));
                zkQuorum = prop.getProperty(DEFUALT_CONFIG_PROPERTY);

                if (zkQuorum != null && !zkQuorum.isEmpty()) {
                    return zkQuorum;
                }
            } catch (FileNotFoundException e) {
                logger.error("loadZooKeeperQuorum error", e);
            } catch (IOException e) {
                logger.error("loadZooKeeperQuorum error", e);
            }
        }

        InputStream propertiesStream = ConfigClient.class.getResourceAsStream("/app.properties");
        if (propertiesStream != null) {
            Properties p = new Properties();
            try {
                p.load(propertiesStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            zkQuorum = p.getProperty(DEFUALT_CONFIG_PROPERTY);
            if (zkQuorum != null && !zkQuorum.isEmpty()) {
                return zkQuorum;
            }
        }

        if (zkQuorum == null || zkQuorum.isEmpty()) {
            zkQuorum = kDefaultZKConnectionString;
        }

        return zkQuorum;
    }

    public synchronized void close() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                logger.error("close config client error", e);
                e.printStackTrace();
            }
            zooKeeper = null;
        }
    }

    @Override
    public IConfig config(String configRoot) {
        IConfig config = null;
        synchronized (lockobj) {
            if (configMap.containsKey(configRoot)) {
                config = configMap.get(configRoot);
            } else {
                ZKConfig zkConfig = new ZKConfig(ConfigClient.kDefaultConfigRoot + "/" + configRoot, this.zooKeeper);
                zkConfig.setReadOnly(this.readOnly);
                configMap.put(configRoot, zkConfig);
                config = zkConfig;
                //FIXME config = zkConfig ?

                System.out.println(ConfigClient.instance().get("conf_yunlu.bde","url"));
            }
        }

        return config;
    }

    /**
     * Get configuration by search zookeeper znode
     *
     * @param configRoot zookeeper znode parent path
     * @param key        zookeeper znode children path
     * @return string store in znode with key
     */
    @Override
    public String get(String configRoot, String key) {
        IConfig config = config(configRoot);
        return config.get(key);
    }

    /**
     * 获取int value配置
     *
     * @param configRoot
     * @param key
     * @param defaultValue
     * @return
     */
    public int getIntValue(String configRoot, String key, int defaultValue) {
        IConfig config = config(configRoot);
        String value = config.get(key);
        int result = defaultValue;
        try {
            if (StringUtils.isNotEmpty(value)) {
                result = Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String get(String configRoot, String key, String defaultValue) {
        IConfig config = config(configRoot);
        String res = config.get(key);
        if (res == null || res.isEmpty()) {
            // set 可能会导致问题，可能会用默认值覆盖原本的�?
            // config.set(key, defaultValue);
            res = defaultValue;
        }

        return res;

    }

    /**
     * Set configuration with given key and value into memory and store it in
     * zookeeper
     * {@link com.yunlu.bde.common.config.imp.ConfigClient#get}
     *
     * @param configRoot
     * @param key
     * @param value
     */
    @Override
    public void set(String configRoot, String key, String value) {
        IConfig config = config(configRoot);
        config.set(key, value);
    }

    @Override
    public void process(WatchedEvent event) {
        final String CONF = kDefaultConfigRoot + "/";
        String path = event.getPath();
        logger.debug("process event " + path + " with " + event.getType());
        if (StringUtils.isEmpty(path) || !path.startsWith(CONF)) {
            return;
        }

        int startIndex = CONF.length();
        int endIndex = path.indexOf("/", CONF.length());
        if (endIndex == -1) {
            return;
        }

        String configRoot = path.substring(startIndex, endIndex);
        String configKey = path.substring(endIndex + 1);

        if (event.getType() == Event.EventType.NodeDataChanged) {
            // config changed
            synchronized (lockobj) {
                if (configMap.containsKey(configRoot)) {
                    IConfig config = configMap.get(configRoot);
                    Stat stat = null;
                    try {
                        String value = new String(this.zooKeeper.getData(path, true, stat), StandardCharsets.UTF_8);
                        if (config instanceof IConfigChangeListener) {
                            ((IConfigChangeListener) config).OnValueChanged(configKey, value);
                        }

                    } catch (KeeperException e) {
                        logger.error("process event set config error", e);
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        logger.error("process event set config error", e);
                        e.printStackTrace();
                    }
                }
            }
        } else if (event.getType() == Event.EventType.NodeDeleted) {
            synchronized (lockobj) {
                IConfig config = configMap.get(configRoot);
                if (config instanceof IConfigChangeListener) {
                    ((IConfigChangeListener) config).OnKeyRemoved(configKey);
                }
            }
        }
    }

    /**
     * 列出系统当前使用的配�?
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Map getPropertiesMap() {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        try {
            List<String> configRoot = this.zooKeeper.getChildren(kDefaultConfigRoot, false);
            for (String root : configRoot) {
                Map<String, String> insideMap = new HashMap<String, String>();
                Properties prop = ConfigClient.instance().config(root).getAll();
                Iterator<?> itr = prop.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<?, ?> e = (Map.Entry<?, ?>) itr.next();
                    String key = (String) e.getKey();
                    String value = (String) e.getValue();
                    if (key.startsWith("sec.") && value != null && !"".equals(value.trim())) {
                        if (value.length() > 2) {
                            char start = value.charAt(0);
                            char end = value.charAt(value.length() - 1);
                            value = start + "**********" + end;
                        } else {
                            value = "*";
                        }

                    }

                    insideMap.put(key, value);
                }

                map.put(root, insideMap);
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return map;
    }

    public String getZkConnectionString() {
        return zkConnectionString;
    }

}