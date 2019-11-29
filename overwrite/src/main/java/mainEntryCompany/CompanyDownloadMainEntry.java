package mainEntryCompany;

import Utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.company.download.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class CompanyDownloadMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDownloadMainEntry.class);

    public static void main(String[] args) {
        CompanyDownloadMainEntry downloadMainEntry = new CompanyDownloadMainEntry();
        downloadMainEntry.getFromRedis();
    }

    private void getFromRedis() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.CallerRunsPolicy());
            String url = "";
            while (true) {
                while (null != (url = RedisUtil.getUrlFromeSet("toCatchUrl-Company"))) {
                    if (!RedisUtil.isExist("catchedUrl-Company", url)) {
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
                if (taskName.contains("71.net")) {
                    QiyiDownload qiyiDownload = new QiyiDownload();
                    qiyiDownload.companyInfo(taskName);
                }else if (taskName.contains("corp.net114.com")) {
                    Net114Download net114Download = new Net114Download();
                    net114Download.companyInfo(taskName);
                }else if (taskName.contains("yellowurl")) {
                    YellowurlDownload yellowurlDownload = new YellowurlDownload();
                    yellowurlDownload.info(taskName);
                }else if (taskName.contains("99114")) {
                    ShopCompanyDownload shopCompanyDownload = new ShopCompanyDownload();
                    shopCompanyDownload.info(taskName);
                }else if (taskName.contains("51sole")) {
                    SoleDownload soleDownload = new SoleDownload();
                    soleDownload.info(taskName);
                }else if (taskName.contains("qiyeku")) {
                    QiYeKuDownload qiYeKuDownload = new QiYeKuDownload();
                    qiYeKuDownload.companyInfo(taskName);
                }else if (taskName.contains("www.herostart.com/")) {
                    HerostartDownload herostartDownload = new HerostartDownload();
                    herostartDownload.info(taskName);
                }else if (taskName.contains("qincai.net")) {
                    QinCaiDownload qinCaiDownload = new QinCaiDownload();
                    qinCaiDownload.companyInfo(taskName);
                }else if (taskName.contains("jdzj.com")) {
                    JdzjDownload jdzjDownload = new JdzjDownload();
                    jdzjDownload.companyInfo(taskName);
                }else if (taskName.contains("atobo.com")) {
                    AtoboToDownload atoboToDownload = new AtoboToDownload();
                    atoboToDownload.productDetails(taskName);
                }else if (taskName.contains("www.atobo.com")) {
                    SoosHongDownload soosHongDownload = new SoosHongDownload();
                    soosHongDownload.companyinfo(taskName);
                }else if (taskName.contains("d17.cc")) {
                    D17Download d17Download = new D17Download();
                    d17Download.detail(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
