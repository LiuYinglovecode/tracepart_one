package mainEntryProduct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.product.toRedis.ChemmProductToRedis;
import parse.product.toRedis.Net114ProductToRedis;
import parse.product.toRedis.WuageProductToRedis;
import parse.product.toRedis.YellowurlToRedis;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lyj
 */
public class ProductSeedMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSeedMainEntry.class);

    public static void main(String[] args) {
        try {
            ProductSeedMainEntry mainEntry = new ProductSeedMainEntry();
            mainEntry.SeedUrlList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void SeedUrlList() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.CallerRunsPolicy());
            InputStream path = this.getClass().getResourceAsStream("/ProductSeedUrl.txt");
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
                if (taskName.contains("http://www.net114.com/")) {
                    Net114ProductToRedis net114ProductToRedis = new Net114ProductToRedis();
                    net114ProductToRedis.productPage(taskName);
                }else if (taskName.contains("http://product.yellowurl.cn/")) {
                    YellowurlToRedis yellowurlToRedis = new YellowurlToRedis();
                    yellowurlToRedis.productPage(taskName);
                }else if (taskName.contains("www.chemm.cn/Sample/")) {
                    ChemmProductToRedis chemmProductToRedis = new ChemmProductToRedis();
                    chemmProductToRedis.productPage(taskName);
                }else if (taskName.contains("www.wuage.com/")) {
                    WuageProductToRedis wuageProductToRedis = new WuageProductToRedis();
                    wuageProductToRedis.productPage(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
