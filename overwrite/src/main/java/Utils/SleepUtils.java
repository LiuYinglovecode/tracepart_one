package Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    public static int sleepMax() {
        try {
            double random = Math.random() * 20000;
            if (9000 >= random) {
                int i = (int) (random + 5000);
                return i;
            } else {
                int i = (int) (random);
                return i;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return 0;
    }


    public static int sleepMin() {
        try {
            double random = Math.random() * 10000;
            if (5000 >= random) {
                int i = (int) (random + 5000);
                return i;
            } else {
                int i = (int) (random);
                return i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
