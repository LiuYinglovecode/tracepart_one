package config;

import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * collect properties and set config
 * @author shanyou
 *
 */
public class ConfigTool {
	public static void main(String[] args) {
		CommandLine cmd = null;
		Options options = new Options();
		options.addOption("d", "directory", true, "configuration directory for store all properties file");
		options.addOption("z", "zkquorum", true, "zookeeper connection string default 127.0.0.1:2181");
		CommandLineParser parser = new BasicParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		if (!cmd.hasOption("d")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("configtool", options);
			return;
		}

		String confDir = cmd.getOptionValue("d");
		if (confDir == null || confDir.isEmpty()) {
			confDir = ".";
		}

		String zkConn = cmd.getOptionValue("z");
		if (zkConn == null || zkConn.isEmpty()) {
			zkConn = "127.0.0.1:2181";
		}
		ConfigManager manager;
		try {
			manager = new ConfigManager(confDir, zkConn);
			manager.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
