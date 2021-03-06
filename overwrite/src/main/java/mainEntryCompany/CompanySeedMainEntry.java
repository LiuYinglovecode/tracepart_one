package mainEntryCompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.company.toRedis.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lyj
 */
public class CompanySeedMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanySeedMainEntry.class);

    public static void main(String[] args) {
        try {
            CompanySeedMainEntry mainEntry = new CompanySeedMainEntry();
            mainEntry.SeedUrlList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void SeedUrlList() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.CallerRunsPolicy());
            InputStream path = this.getClass().getResourceAsStream("/CompanySeedUrl.txt");
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
                if (taskName.contains("71.net")) {
                    QiyiToRedis qiyiToRedis = new QiyiToRedis();
                    qiyiToRedis.homepage(taskName);
                }else if (taskName.contains("corp.net114.com")) {
                    Net114ToRedis net114ToRedis = new Net114ToRedis();
                    net114ToRedis.homepage(taskName);
                }else if (taskName.contains("yellowurl")) {
                    YellowurlToRedis yellowurlToRedis = new YellowurlToRedis();
                    yellowurlToRedis.category(taskName);
                }else if (taskName.contains("shop.99114.com/")) {
                    ShopCompanyToRedis shopCompanyToRedis = new ShopCompanyToRedis();
                    shopCompanyToRedis.category(taskName);
                }else if (taskName.contains("51sole")) {
                    SoleToRedis soleToRedis = new SoleToRedis();
                    soleToRedis.category(taskName);
                }else if (taskName.contains("www.qiyeku.com/huangye/")) {
                    QiYeKuToRedis qiYeKuToRedis = new QiYeKuToRedis();
                    qiYeKuToRedis.category(taskName);
                }else if (taskName.contains("www.herostart.com")) {
                    HerostartToRedis HerostartToRedis = new HerostartToRedis();
                    HerostartToRedis.category(taskName);
                }else if (taskName.contains("www.qincai.net")) {
                    QinCaiToRedis qinCaiToRedis = new QinCaiToRedis();
                    qinCaiToRedis.category(taskName);
                }else if (taskName.contains("www.jdzj.com")) {
                    JdzjToRedis jdzjToRedis = new JdzjToRedis();
                    jdzjToRedis.category(taskName);
                }else if (taskName.contains("http://company.d17.cc/")) {
                    D17ToRedis d17ToRedis = new D17ToRedis();
                    d17ToRedis.guangdong(taskName);
                }else if (taskName.contains("www.atobo.com.cn")) {
                    AtoboToRedis atoboToRedis = new AtoboToRedis();
                    atoboToRedis.productNavigationBar(taskName);
                }else if (taskName.contains("www.sooshong.com/company/")) {
                    SoosHongToRedis soosHongToRedis = new SoosHongToRedis();
                    soosHongToRedis.industryList(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : ????????????");
        }
    }
}
