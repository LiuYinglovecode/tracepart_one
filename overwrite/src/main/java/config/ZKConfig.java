package config;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Zookeeper Config for implements IConfig and IChangeable
 *
 * @author shanyou
 *         r
 */
public class ZKConfig extends InMemConfig implements IConfigChangeListener {
    private static Logger logger = LoggerFactory.getLogger(ZKConfig.class);
    private ZooKeeper zooKeeper;
    private String configRoot;

    /**
     * if remote zookeeper config read only, default is true
     */
    private boolean readOnly = true;
    
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
    public ZKConfig(String configRoot, ZooKeeper zk) {
        this.zooKeeper = zk;
        this.configRoot = configRoot;
    }

    @Override
    public synchronized void set(String key, String value) {
    	super.set(key, value);
    	if(readOnly) {
    		return;
    	}
    	
        Stat res = null;
        try {
            String path = configRoot + "/" + key;
            res = zooKeeper.exists(path, true);
            if (res == null) {
                createNodeRecursivly(path);
                res = zooKeeper.exists(path, true);
            }

            if (res != null) {
                zooKeeper.setData(path, value.getBytes(StandardCharsets.UTF_8), -1);
            }
        } catch (KeeperException e) {
            logger.error("set config error", e);
            return;
        } catch (InterruptedException e) {
            logger.error("set config error", e);
            return;
        }
    }

    @Override
    public synchronized String get(String key) {
    	if (super.hasKey(key)) {
    		return super.get(key);
    	}
    	
        try {
            String path = configRoot + "/" + key;
             Stat res = zooKeeper.exists(path, false);
            if (res != null) {
                String value = new String(zooKeeper.getData(path, true, res), StandardCharsets.UTF_8);
                super.set(key, value);
                return value;
            }
        } catch (KeeperException e) {
            logger.error("get config error", e);
        } catch (InterruptedException e) {
            logger.error("get config error", e);
        }

        return "";
    }

    @Override
    public synchronized String get(String key, String defaultValue) {
    	if (super.hasKey(key)) {
    		return super.get(key);
    	}
    	
    	
        String config = get(key);
        if (StringUtils.isEmpty(config)) {
            config = defaultValue;
            super.set(key, defaultValue);
        }

        return config;
    }

    @Override
    public synchronized void remove(String key) {
    	super.remove(key);
    	if(readOnly) {
    		return;
    	}
    	try {
            String path = this.configRoot + "/" + key;
            if (zooKeeper.exists(path, false) != null) {
                zooKeeper.delete(path, -1);
                return;
            }
        } catch (KeeperException e) {
            logger.error("remove key error", e);
        } catch (InterruptedException e) {
            logger.error("remove key error", e);
        }

    }

    @Override
    public Properties getAll() {
    	Properties prop = new Properties();
        // get from zk
        try {
            
            Stat res = zooKeeper.exists(configRoot, false);
            if (res != null) {
                List<String> children = zooKeeper.getChildren(configRoot, false);
                for (String child : children) {
                    String childPath = configRoot;
                    if (!childPath.endsWith("/")) {
                        childPath += "/";
                    }
                    childPath += child;

                    byte[] buffer = zooKeeper.getData(childPath, true, res);
                    if (buffer == null) {
                        continue;
                    }

                    String value = new String(buffer, StandardCharsets.UTF_8);
                    String key = child;

                    if (value != null) {
                        prop.setProperty(key, value);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("get all config error", e);
        }

        Properties cacheProp = super.getAll();
        @SuppressWarnings("rawtypes")
		Enumeration enums = cacheProp.keys();
        while (enums.hasMoreElements()) {
        	Object key = enums.nextElement();
        	prop.setProperty((String)key, (String) cacheProp.get(key));
        }
        
        return prop;
    }

    /**
     * Create Znode recursive
     *
     * @param path for znode
     */
    private void createNodeRecursivly(String path) {
        try {
            logger.debug("begin create znode: " + path);
            if (path.length() > 0 && zooKeeper.exists(path, false) == null) {
                String temp = path.substring(0, path.lastIndexOf("/"));
                createNodeRecursivly(temp);
                zooKeeper.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                return;
            }

        } catch (KeeperException e) {
            logger.error("createNodeRecursivly error", e);
        } catch (InterruptedException e) {
            logger.error("createNodeRecursivly error", e);
        }
    }

    @Override
    public synchronized void OnValueChanged(String key, String value) {
        super.set(key, value);
    }

    @Override
    public synchronized void OnKeyRemoved(String key) {
    	super.remove(key);
    }

	@Override
	public boolean hasKey(String key) {
		boolean result = super.hasKey(key);
		if (result) {
			return result;
		}
		
		 String path = configRoot + "/" + key;
         try {
			Stat res = zooKeeper.exists(path, false);
			result = (res != null);
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         return result;
	}
}

