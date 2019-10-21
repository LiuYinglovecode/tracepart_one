package mainEntryNews;

import Utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parse.news.download.*;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class NewsDownloadMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsDownloadMainEntry.class);

    public static void main(String[] args) {
        NewsDownloadMainEntry downloadMainEntry = new NewsDownloadMainEntry();
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
                }else if (taskName.contains("www.ccoalnews.com")) {
                    CcoalnewsDownload CcoalnewsDownload = new CcoalnewsDownload();
                    CcoalnewsDownload.detailDownload(taskName);
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
                }else if (taskName.contains("hooshong")) {
                    HooshongDownload hooshongDownload = new HooshongDownload();
                    hooshongDownload.newsInfo(taskName);
                }else if (taskName.contains("ic37")) {
                    Ic37Download ic37Download = new Ic37Download();
                    ic37Download.newsInfo(taskName);
                }else if (taskName.contains("cndianji.cn")) {
                    CndianjiDownload CndianjiDownload = new CndianjiDownload();
                    CndianjiDownload.newsInfo(taskName);
                }else if (taskName.contains("eapad")) {
                    EapadDownload eapadDownload = new EapadDownload();
                    eapadDownload.newsInfo(taskName);
                }else if (taskName.contains("cnpv")) {
                    CnpvDownload cnpvDownload = new CnpvDownload();
                    cnpvDownload.detail(taskName);
                }else if (taskName.contains("leiphone")) {
                    LeiphoneDownload leiphoneDownload = new LeiphoneDownload();
                    leiphoneDownload.detail(taskName);
                }else if (taskName.contains("cntma")) {
                    CntmaDownload cntmaDownload = new CntmaDownload();
                    cntmaDownload.detail(taskName);
                }else  if (taskName.contains("gongkong")) {
                    GongkongDownload gongkongDownload = new GongkongDownload();
                    gongkongDownload.detail(taskName);
                }else if (taskName.contains("techweb")) {
                    TechwebDownload techwebDownload = new TechwebDownload();
                    techwebDownload.detail(taskName);
                }else if (taskName.contains("199it.com")) {
                    ItDownload itDownload = new ItDownload();
                    itDownload.detail(taskName);
                }else if (taskName.contains("tech.163")) {
                    Tech163Download tech163Download = new Tech163Download();
                    tech163Download.newsInfo(taskName);
                }else if (taskName.contains("pedaily")) {
                    PedailyDownload pedailyDownload = new PedailyDownload();
                    pedailyDownload.newsInfo(taskName);
                }else if (taskName.contains("chinaz")) {
                    ChinazDownload chinazDownload = new ChinazDownload();
                    chinazDownload.newsInfo(taskName);
                }else if (taskName.contains("newseed")) {
                    NewSeedDownload newSeedDownload = new NewSeedDownload();
                    newSeedDownload.newsInfo(taskName);
                }else if (taskName.contains("technode")) {
                    TechnodeDownload technodeDownload = new TechnodeDownload();
                    technodeDownload.newsInfo(taskName);
                }else  if (taskName.contains("news.mydrivers.com")) {
                    MydriversDownload mydriversDownload = new MydriversDownload();
                    mydriversDownload.newsInfo(taskName);
                }else if (taskName.contains("cnmo")) {
                    CnmoDownload cnmoDownload = new CnmoDownload();
                    cnmoDownload.newsInfo(taskName);
                }else if (taskName.contains("ccidnet")) {
                    CcidnetDownload ccidnetDownload = new CcidnetDownload();
                    ccidnetDownload.newsInfo(taskName);
                }else if (taskName.contains("www.tybaba.com")) {
                    TybabaDownload tybabaDownload = new TybabaDownload();
                    tybabaDownload.newsInfo(taskName);
                }else if (taskName.contains("www.ledinside.cn/news")) {
                    LedinsideDownload LedinsideDownload = new LedinsideDownload();
                    LedinsideDownload.newsInfo(taskName);
                }else if (taskName.contains("www.pipew")) {
                    PipewDownload pipewDownload = new PipewDownload();
                    pipewDownload.newsInfo(taskName);
                }else if (taskName.contains("ofweek.com")) {
                    OfWeekDownload ofWeekDownload = new OfWeekDownload();
                    ofWeekDownload.newsInfo(taskName);
                }else if (taskName.contains("product.dzsc.com/news")) {
                    DzscDownload dzscDownload = new DzscDownload();
                    dzscDownload.newsInfo(taskName);
                }else if (taskName.contains("www.cscsf.com")) {
                    CscsfDownload cscsfDownload = new CscsfDownload();
                    cscsfDownload.newsInfo(taskName);
                }else if (taskName.contains("cinic.org.cn")) {
                    CinicDownload cinicDownload = new CinicDownload();
                    cinicDownload.newsInfo(taskName);
                }else if (taskName.contains("news.chemnet.com")) {
                    ChemNetDownload chemNetDownload = new ChemNetDownload();
                    chemNetDownload.newsInfo(taskName);
                }else if (taskName.contains("news.jc001.cn")) {
                    Jc001Download jc001Download = new Jc001Download();
                    jc001Download.newsInfo(taskName);
                }else if (taskName.contains("www.alliii.com/news/")) {
                    AlliiiDownload alliiiDownload = new AlliiiDownload();
                    alliiiDownload.newsInfo(taskName);
                }else if (taskName.contains("www.iyiou.com")) {
                    IyiouDownload IyiouDownload = new IyiouDownload();
                    IyiouDownload.newsInfo(taskName);
                }else if (taskName.contains("www.thepaper.cn")) {
                    ThepaperDownload thepaperDownload = new ThepaperDownload();
                    thepaperDownload.newsInfo(taskName);
                }else if (taskName.contains("36kr.com")) {
                    KrDownload krDownload = new KrDownload();
                    krDownload.newsInfo(taskName);
                }else if (taskName.contains("www.sootoo.com")) {
                    SooTooDownload sooTooDownload = new SooTooDownload();
                    sooTooDownload.newsInfo(taskName);
                }else if (taskName.contains("www.southmoney.com/")) {
                    SouthMoneyDownload southMoneyDownload = new SouthMoneyDownload();
                    southMoneyDownload.newsInfo(taskName);
                }else if (taskName.contains("www.qianzhan.com")) {
                    QianZhanDownload qianZhanDownload = new QianZhanDownload();
                    qianZhanDownload.newsInfo(taskName);
                }else if (taskName.contains("xuexi.huize.com")) {
                    HuiZeDownload huiZeDownload = new HuiZeDownload();
                    huiZeDownload.newsInfo(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
