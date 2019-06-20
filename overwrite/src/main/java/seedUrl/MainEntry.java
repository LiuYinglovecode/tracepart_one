package seedUrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * @author lyj
 */
public class MainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainEntry.class);

    public static void main(String[] args) {
        try {
            MainEntry mainEntry = new MainEntry();
            mainEntry.SeedUrlList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void SeedUrlList() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.DiscardOldestPolicy());
            InputStream path = this.getClass().getResourceAsStream("/seedUrl.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(path));
            String line = "";
            while ((line = reader.readLine()) != null) {
                SeedTask seed = new SeedTask(line);
                executor.execute(seed);
            }
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SeedTask implements Runnable {
        private String taskName;

        private SeedTask(String name) {
            this.taskName = name;
        }

        @Override
        public void run() {
            try {
//                if ()
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
