package config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

public class ConfigManager {

	private static Logger logger = LoggerFactory.getLogger(ConfigManager.class);

	private File confDir;

	private IConfigManager configMgr;

	public ConfigManager(String path, String zkconn) throws IOException {
		confDir = new File(path);
		if (!confDir.exists() || confDir.isFile()) {
			logger.error("config dir not exists");
			throw new IllegalArgumentException("config dir not exists");
		}

		ConfigClient.initConfigClient(zkconn, false);
		configMgr = ConfigClient.instance();
	}

	public ConfigManager(File confdir) {
		confDir = confdir;
		if (!confDir.exists() || confDir.isFile()) {
			throw new IllegalArgumentException("config dir not exists");
		}
	}

	/**
	 * Load all properties files into zookeeper
	 */
	public void load() {
		Collection<File> files = FileUtils.listFiles(confDir, new RegexFileFilter("\\S+?\\.properties"), DirectoryFileFilter.DIRECTORY);
		for (File file : files) {
			String confRoot = FilenameUtils.removeExtension(file.getName());
			Properties prop = new Properties();
			try {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
				prop.load(reader);
				Iterator<?> itr = prop.entrySet().iterator();
				while (itr.hasNext()) {
					Entry<?, ?> e = (Entry<?, ?>) itr.next();
					String key = (String) e.getKey();
					String value = (String) e.getValue();

					if (key.length() > 0 && value.length() > 0) {
						configMgr.set(confRoot, key, value);
					}

					System.out.println(key + ": " + value);
				}
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
