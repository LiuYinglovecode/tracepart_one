package mainEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.news.toRedis.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * @author lyj
 */
public class SeedMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeedMainEntry.class);

    public static void main(String[] args) {
        try {
            SeedMainEntry mainEntry = new SeedMainEntry();
            mainEntry.SeedUrlList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void SeedUrlList() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.DiscardOldestPolicy());
            InputStream path = this.getClass().getResourceAsStream("/NewsSeedUrl.txt");
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
                if (taskName.contains("www.ccoalnews.com")) {
                    CcoalnewsToRedis ccoalnewsParseToRedis = new CcoalnewsToRedis();
                    ccoalnewsParseToRedis.getUrlStart(taskName);
                } else if (taskName.contains("www.ca800.com")) {
                    Ca800ToRedis ca800ToRedis = new Ca800ToRedis();
                    ca800ToRedis.homepage(taskName);
                } else if (taskName.contains("news.cableabc.com")) {
                    CableabcToRedis cableabcToRedis = new CableabcToRedis();
                    cableabcToRedis.homepage(taskName);
                } else if (taskName.contains("www.ces.cn")) {
                    CesToRedis cesToRedis = new CesToRedis();
                    cesToRedis.homepage(taskName);
                } else if (taskName.contains("www.chinahightech.com")) {
                    ChinahightechToRedis chinahightechToRedis = new ChinahightechToRedis();
                    chinahightechToRedis.homepage(taskName);
                } else if (taskName.contains("www.cinn.cn")) {
                    CinnToRedis cinnToRedis = new CinnToRedis();
                    cinnToRedis.homepage(taskName);
                } else if (taskName.contains("www.cnmn.com.cn")) {
                    CnmnToRedis cnmnToRedis = new CnmnToRedis();
                    cnmnToRedis.homepage(taskName);
                } else if (taskName.contains("www.cpnn.com.cn")) {
                    CpnnToRedis cpnnToRedis = new CpnnToRedis();
                    cpnnToRedis.homepage(taskName);
                } else if (taskName.contains("www.51dzw.com")) {
                    DzwToRedis dzwToRedis = new DzwToRedis();
                    dzwToRedis.homepage(taskName);
                } else if (taskName.contains("www.gkzhan.com")) {
                    GkzhanToRedis gkzhanToRedis = new GkzhanToRedis();
                    gkzhanToRedis.homePage(taskName);
                } else if (taskName.contains("www.jdzj.com")) {
                    JdzjToRedis jdzjToRedis = new JdzjToRedis();
                    jdzjToRedis.homepage(taskName);
                } else if (taskName.contains("www.jiancai.com")) {
                    JiancaiToRedis jiancaiToRedis = new JiancaiToRedis();
                    jiancaiToRedis.homepage(taskName);
                } else if (taskName.contains("news.lmjx.net")) {
                    LmjxToRedis lmjxToRedis = new LmjxToRedis();
                    lmjxToRedis.homepage(taskName);
                } else if (taskName.contains("news.machine365.com")) {
                    Machine365ToRedis machine365ToRedis = new Machine365ToRedis();
                    machine365ToRedis.homePage(taskName);
                } else if (taskName.contains("www.86mai.com")) {
                    MaiToRedis maiToRedis = new MaiToRedis();
                    maiToRedis.homepage(taskName);
                } else if (taskName.contains("www.membranes.com.cn")) {
                    MembranesToRedis membranesToRedis = new MembranesToRedis();
                    membranesToRedis.industryNews(taskName);
                } else if (taskName.contains("www.nengyuanjie.net")) {
                    NengyuanjieToRedis nengyuanjieToRedis = new NengyuanjieToRedis();
                    nengyuanjieToRedis.homepage(taskName);
                } else if (taskName.contains("www.51pla.com")) {
                    PlaToRedis plaToRedis = new PlaToRedis();
                    plaToRedis.homepage(taskName);
                } else if (taskName.contains("news.steelcn.cn")) {
                    SteelcnToRedis steelcnToRedis = new SteelcnToRedis();
                    steelcnToRedis.homepage(taskName);
                } else if (taskName.contains("www.texindex.com.cn")) {
                    TexindexToRedis texindexToRedis = new TexindexToRedis();
                    texindexToRedis.homePage(taskName);
                } else if (taskName.contains("www.xianjichina.com")) {
                    XianjichinaToRedis xianjichinaToRedis = new XianjichinaToRedis();
                    xianjichinaToRedis.homepage(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}