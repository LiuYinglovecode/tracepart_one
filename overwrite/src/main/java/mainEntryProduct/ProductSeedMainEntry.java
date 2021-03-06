package mainEntryProduct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.product.toRedis.*;

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
                }else  if (taskName.contains("ebdoor")) {
                    EbdoorProductToRedis ebdoorProductToRedis = new EbdoorProductToRedis();
                    ebdoorProductToRedis.productPage(taskName);
                }else  if (taskName.contains("www.pe168.com/sell/")) {
                    Pe168ProductToRedis pe168ProductToRedis = new Pe168ProductToRedis();
                    pe168ProductToRedis.homePage(taskName);
                }else  if (taskName.contains("www.ehsy.com")) {
                    EhsyProductToRedis ehsyProductToRedis = new EhsyProductToRedis();
                    ehsyProductToRedis.productPage(taskName);
                }else if (taskName.contains("www.grainger.cn")) {
                    GraingerProductToRedis graingerProductToRedis = new GraingerProductToRedis();
                    graingerProductToRedis.productPage(taskName);
                }else if (taskName.contains("www.wmb2b.com/info/")) {
                    Wmb2bToRedis wmb2bToRedis = new Wmb2bToRedis();
                    wmb2bToRedis.productPage(taskName);
                }else if (taskName.contains("www.jdzj.com/chanpin.html")) {
                    JdzjProductToRedis jdzjProductToRedis = new JdzjProductToRedis();
                    jdzjProductToRedis.productPage(taskName);
                }else if (taskName.contains("chanpin.gongchang.com")) {
                    GongChangToRedis gongChangToRedis = new GongChangToRedis();
                    gongChangToRedis.productPage(taskName);
                }else if (taskName.contains("www.912688.com/chanpin/")) {
                    SouHaoHuoProductToRedis souHaoHuoProductToRedis = new SouHaoHuoProductToRedis();
                    souHaoHuoProductToRedis.productPage(taskName);
                }else if (taskName.contains("www.86mai.com/sell/")) {
                    MaiProductToRedis maiProductToRedis = new MaiProductToRedis();
                    maiProductToRedis.productPage(taskName);
                }else if (taskName.contains("www.qth58.cn/product/")) {
                    Qth58ProductToRedis qth58ProductToRedis = new Qth58ProductToRedis();
                    qth58ProductToRedis.homePage("taskName");
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : ????????????");
        }
    }
}
