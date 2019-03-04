package redis.clients.jedis.commands;

import java.util.Map;

public interface TSDBCommands {
	Long tadd(String key, Map<Long, Double> tvPair);
}
