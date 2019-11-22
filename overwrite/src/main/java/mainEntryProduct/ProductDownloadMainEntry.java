package mainEntryProduct;

import Utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.product.download.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


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
                while (null != (url = RedisUtil.getUrlFromeSet("toCatchUrl-shhProduct"))) {
                    if (!RedisUtil.isExist("catchedUrl-shhProduct", url)) {
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
                }else if (taskName.contains("pe168")) {
                    Pe168ProductDownload pe168ProductDownload = new Pe168ProductDownload();
                    pe168ProductDownload.productInfo(taskName);
                }else if (taskName.contains("www.ehsy.com")) {
                    EhsyProductDownload ehsyProductDownload = new EhsyProductDownload();
                    ehsyProductDownload.productInfo(taskName);
                }else if (taskName.contains("www.grainger.cn")) {
                    GraingerProductDownload graingerProductDownload = new GraingerProductDownload();
                    graingerProductDownload.productInfo(taskName);
                }else if (taskName.contains("www.wmb2b.com")) {
                    Wmb2bDownload wmb2bDownload = new Wmb2bDownload();
                    wmb2bDownload.productInfo(taskName);
                }else if (taskName.contains("www.jdzj.com/chanpin")) {
                    JdzjProductDownload jdzjProductToRedis = new JdzjProductDownload();
                    jdzjProductToRedis.productInfo(taskName);
                }else if (taskName.contains("https://chanpin.gongchang.com")) {
                    GongChangDownload GongChangDownload = new GongChangDownload();
                    GongChangDownload.productInfo(taskName);
                }else if (taskName.contains("www.912688.com")) {
                    SouHaoHuoProductDownload souHaoHuoProductDownload = new SouHaoHuoProductDownload();
                    souHaoHuoProductDownload.productInfo(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
