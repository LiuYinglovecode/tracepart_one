package redis.clients.jedis.commands;

import java.util.Map;

public interface BinaryTSDBCommands {
	 Long tadd(byte[] key, Map<byte[], byte[]> tvPair);
}
