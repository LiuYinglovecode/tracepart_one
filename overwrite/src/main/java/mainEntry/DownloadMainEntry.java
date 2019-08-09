package mainEntry;

import Utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.news.download.*;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DownloadMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadMainEntry.class);

    public static void main(String[] args) {
        DownloadMainEntry downloadMainEntry = new DownloadMainEntry();
        downloadMainEntry.getFromRedis();
    }

    private void getFromRedis() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.CallerRunsPolicy());
            String url = "";
            while (true) {
                while (null != (url = RedisUtil.getUrlFromeSet("toCatchUrl"))) {
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
                if (taskName.contains("www.ca800.com")) {
                    Ca800Download ca800Download = new Ca800Download();
                    ca800Download.newsInfo(taskName);
                } else if (taskName.contains("news.cableabc.com")) {
                    CableabcDownload cableabcDownload = new CableabcDownload();
                    cableabcDownload.newsInfo(taskName);
                } else if (taskName.contains("www.ces.cn")) {
                    CesDownload cesDownload = new CesDownload();
                    cesDownload.newsInfo(taskName);
                } else if (taskName.contains("www.chinahightech.com")) {
                    ChinahightechDownload chinahightechDownload = new ChinahightechDownload();
                    chinahightechDownload.newsInfo(taskName);
                } else if (taskName.contains("www.cinn.cn")) {
                    CinnDownload cinnDownload = new CinnDownload();
                    cinnDownload.detail(taskName);
                } else if (taskName.contains("www.cnmn.com.cn")) {
                    CnmnDownload cnmnDownload = new CnmnDownload();
                    cnmnDownload.newsInfo(taskName);
                } else if (taskName.contains("www.cpnn.com.cn")) {
                    CpnnDownload cpnnDownload = new CpnnDownload();
                    cpnnDownload.newsInfo(taskName);
                } else if (taskName.contains("www.51dzw.com")) {
                    DzwDownload dzwDownload = new DzwDownload();
                    dzwDownload.newsInfo(taskName);
                } else if (taskName.contains("www.gkzhan.com")) {
                    GkzhanDownload gkzhanDownload = new GkzhanDownload();
                    gkzhanDownload.newsinfo(taskName);
                } else if (taskName.contains("www.jdzj.com")) {
                    JdzjDownload jdzjDownload = new JdzjDownload();
                    jdzjDownload.newsInfo(taskName);
                } else if (taskName.contains("www.jiancai.com")) {
                    JiancaiDownload jiancaiDownload = new JiancaiDownload();
                    jiancaiDownload.newsInfo(taskName);
                } else if (taskName.contains("news.lmjx.net")) {
                    LmjxDownload lmjxDownload = new LmjxDownload();
                    lmjxDownload.newsInfo(taskName);
                } else if (taskName.contains("news.machine365.com")) {
                    MachineDownload machineDownload = new MachineDownload();
                    machineDownload.newsinfo(taskName);
                } else if (taskName.contains("www.86mai.com")) {
                    MaiDownload maiDownload = new MaiDownload();
                    maiDownload.newsInfo(taskName);
                } else if (taskName.contains("www.membranes.com.cn")) {
                    MemDownload memDownload = new MemDownload();
                    memDownload.newsInfo(taskName);
                } else if (taskName.contains("www.nengyuanjie.net")) {
                    NengyuanjieDownload nengyuanjieDownload = new NengyuanjieDownload();
                    nengyuanjieDownload.newsInfo(taskName);
                } else if (taskName.contains("www.51pla.com")) {
                    PlaDownload plaDownload = new PlaDownload();
                    plaDownload.newsInfo(taskName);
                } else if (taskName.contains("news.steelcn.cn")) {
                    SteelcnDownload steelcnDownload = new SteelcnDownload();
                    steelcnDownload.newsInfo(taskName);
                } else if (taskName.contains("www.texindex.com.cn")) {
                    TexindexDownload texindexDownload = new TexindexDownload();
                    texindexDownload.newsInfo(taskName);
                } else if (taskName.contains("www.xianjichina.com")) {
                    XianjichinaDownload xianjichinaDownload = new XianjichinaDownload();
                    xianjichinaDownload.detail(taskName);
                } else if (taskName.contains("news.wjw.cn")) {
                    WjwDownload wjwDownload = new WjwDownload();
                    wjwDownload.detail(taskName);
                } else if (taskName.contains("www.gbs.cn/info")) {
                    GbsDownload gbsDownload = new GbsDownload();
                    gbsDownload.detail(taskName);
                } else if (taskName.contains("news.ddc.net.cn")) {
                    DdcDownload ddcDownload = new DdcDownload();
                    ddcDownload.detail(taskName);
                } else if (taskName.contains("info.china.herostart.com")) {
                    HerostartDownload herostartDownload = new HerostartDownload();
                    herostartDownload.detail(taskName);
                } else if (taskName.contains("news.zgw.com")) {
                    ZgwDownload zgwDownload = new ZgwDownload();
                    zgwDownload.detail(taskName);
                } else if (taskName.contains("elecfans")) {
                    ElecfansDownload elecfansDownload = new ElecfansDownload();
                    elecfansDownload.detail(taskName);
                } else if (taskName.contains("maijx")) {
                    MaijxDownload maijxDownload = new MaijxDownload();
                    maijxDownload.newsInfo(taskName);
                } else if (taskName.contains("famens")) {
                    FamensDownload famensDownload = new FamensDownload();
                    famensDownload.newsInfo(taskName);
                } else if (taskName.contains("spsb114")) {
                    Spsb114Download spsb114Download = new Spsb114Download();
                    spsb114Download.newsInfo(taskName);
                } else if (taskName.contains("chuandong")) {
                    ChuangdongDownload chuangdongDownload = new ChuangdongDownload();
                    chuangdongDownload.newsInfo(taskName);
                } else if (taskName.contains("chinacrane")) {
                    ChinacraneDownload chinacraneDownload = new ChinacraneDownload();
                    chinacraneDownload.newsInfo(taskName);
                } else if (taskName.contains("findzd")) {
                    FindzdDownload findzdDownload = new FindzdDownload();
                    findzdDownload.newsInfo(taskName);
                }else if (taskName.contains("fzfzjx")) {
                    FzfzjxDownload fzfzjxDownload = new FzfzjxDownload();
                    fzfzjxDownload.newsInfo(taskName);
                }else if (taskName.contains("hometex114")) {
                    Hometex114Download hometex114Download = new Hometex114Download();
                    hometex114Download.newsInfo(taskName);
                }else if (taskName.contains("fengj")) {
                    FengjDownload fengjDownload = new FengjDownload();
                    fengjDownload.newsInfo(taskName);
                }else if (taskName.contains("pm8")) {
                    Pm8Download pm8Download = new Pm8Download();
                    pm8Download.newsInfo(taskName);
                }else if (taskName.contains("huajx")) {
                    HuajxDownload huajxDownload = new HuajxDownload();
                    huajxDownload.newsInfo(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
