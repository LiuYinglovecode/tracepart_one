package mainEntryProduct;

import Utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.product.download.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ProductDownloadMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDownloadMainEntry.class);

    public static void main(String[] args) {
        ProductDownloadMainEntry downloadMainEntry = new ProductDownloadMainEntry();
        downloadMainEntry.getFromRedis();
    }

    private void getFromRedis() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.CallerRunsPolicy());
            String url = "";
            while (true) {
                while (null != (url = RedisUtil.getUrlFromeSet("toCatchUrl-Product"))) {
                    if (!RedisUtil.isExist("catchedUrl", url)) {
                        SeedTask seed = new SeedTask(url);
                        executor.execute(seed);
                    }
                }
                executor.shutdown();
                Thread.sleep(60000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
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
                if (taskName.contains("net114")) {
                    Net114ProductDownload net114ProductToRedis = new Net114ProductDownload();
                    net114ProductToRedis.productInfo(taskName);
                }else if (taskName.contains("product.yellowurl")) {
                    YellowurlProductDownload yellowurlProductToRedis = new YellowurlProductDownload();
                    yellowurlProductToRedis.productInfo(taskName);
                }else if (taskName.contains("chemm")) {
                    ChemmProductDownload chemmProductDownload = new ChemmProductDownload();
                    chemmProductDownload.productInfo(taskName);
                }else if (taskName.contains("wuage")) {
                    WuageProductDownload WuageProductDownload = new WuageProductDownload();
                    WuageProductDownload.productInfo(taskName);
                }else if (taskName.contains("ebdoor")) {
                    EbdoorProductDownload ebdoorProductDownload = new EbdoorProductDownload();
                    ebdoorProductDownload.productInfo(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
