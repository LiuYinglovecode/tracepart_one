package mainEntryNews;

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
public class NewsSeedMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsSeedMainEntry.class);

    public static void main(String[] args) {
        try {
            NewsSeedMainEntry mainEntry = new NewsSeedMainEntry();
            mainEntry.SeedUrlList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void SeedUrlList() {
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(15), new ThreadPoolExecutor.CallerRunsPolicy());
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
                } else if (taskName.contains("news.wjw.cn")) {
                    WjwToRedis wjwToRedis = new WjwToRedis();
                    wjwToRedis.homepage(taskName);
                } else if (taskName.contains("www.gbs.cn/info")) {
                    GbsToRedis gbsToRedis = new GbsToRedis();
                    gbsToRedis.homepage(taskName);
                } else if (taskName.contains("news.ddc.net.cn")) {
                    DdcToRedis ddcToRedis = new DdcToRedis();
                    ddcToRedis.homepage(taskName);
                } else if (taskName.contains("info.china.herostart.com")) {
                    HerostartToRedis herostartToRedis = new HerostartToRedis();
                    herostartToRedis.homepage(taskName);
                } else if (taskName.contains("news.zgw.com")) {
                    ZgwToRedis zgwToRedis = new ZgwToRedis();
                    zgwToRedis.homepage(taskName);
                } else if (taskName.contains("www.elecfans.com/news/hangye")) {
                    ElecfansToRedis elecfansToRedis = new ElecfansToRedis();
                    elecfansToRedis.homepage(taskName);
                } else if (taskName.contains("www.maijx.com/information")) {
                    MaijxToRedis maijxToRedis = new MaijxToRedis();
                    maijxToRedis.homepage(taskName);
                } else if (taskName.contains("www.famens.com")) {
                    FamensToRedis famensToRedis = new FamensToRedis();
                    famensToRedis.homepage(taskName);
                } else if (taskName.contains("www.spsb114.com")) {
                    Spsb114ToRedis spsb114ToRedis = new Spsb114ToRedis();
                    spsb114ToRedis.homepage(taskName);
                } else if (taskName.contains("www.chuandong.com/news/")) {
                    ChuandongToRedis chuandongToRedis = new ChuandongToRedis();
                    chuandongToRedis.homepage(taskName);
                } else if (taskName.contains("www.chinacrane.net/")) {
                    ChinacraneToRedis chinacraneToRedis = new ChinacraneToRedis();
                    chinacraneToRedis.homepage(taskName);
                } else if (taskName.contains("www.findzd.com/industry/")) {
                    FindzdToRedis findzdToRedis = new FindzdToRedis();
                    findzdToRedis.homepage(taskName);
                }else if (taskName.contains("www.fzfzjx.com/news/")) {
                    FzfzjxToRedis fzfzjxToRedis = new FzfzjxToRedis();
                    fzfzjxToRedis.homepage(taskName);
                }else if (taskName.contains("www.hometex114.com/News/")) {
                    Hometex114ToRedis hometex114ToRedis = new Hometex114ToRedis();
                    hometex114ToRedis.homepage(taskName);
                }else if (taskName.contains("china.fengj.com/news/")) {
                    FengjToRedis fengjToRedis = new FengjToRedis();
                    fengjToRedis.homepage(taskName);
                }else if (taskName.contains("www.pm8.cn/news/")) {
                    Pm8ToRedis pm8ToRedis = new Pm8ToRedis();
                    pm8ToRedis.homepage(taskName);
                }else if (taskName.contains("www.huajx.com/news/")) {
                    HuajxToRedis huajxToRedis = new HuajxToRedis();
                    huajxToRedis.homepage(taskName);
                }else if (taskName.contains("www.hooshong.com/news/")) {
                    HooshongToRedis hooshongToRedis = new HooshongToRedis();
                    hooshongToRedis.homepage(taskName);
                }else if (taskName.contains("www.cndianji.cn/article")) {
                    CndianjiToRedis CndianjiToRedis = new CndianjiToRedis();
                    CndianjiToRedis.homepage(taskName);
                }else if (taskName.contains("www.eapad.cn/")) {
                    EapadToRedis EapadToRedis = new EapadToRedis();
                    EapadToRedis.homepage(taskName);
                }else if (taskName.contains("www.ic37.com")) {
                    Ic37ToRedis Ic37ToRedis = new Ic37ToRedis();
                    Ic37ToRedis.homepage(taskName);
                }else if (taskName.contains("www.cnpv.com/News")) {
                    CnpvToRedis cnpvToRedis = new CnpvToRedis();
                    cnpvToRedis.homepage(taskName);
                }else if (taskName.contains("www.leiphone.com/category/iot")) {
                    LeiphoneToRedis leiphoneToRedis = new LeiphoneToRedis();
                    leiphoneToRedis.homepage(taskName);
                }else if (taskName.contains("www.cntma.com/news/")) {
                    CntmaToRedis CntmaToRedis = new CntmaToRedis();
                    CntmaToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.gongkong.com/news/")) {
                    GongkongToRedis GongkongToRedis = new GongkongToRedis();
                    GongkongToRedis.homepage(taskName);
                }else if (taskName.contains("www.techweb.com.cn/")) {
                    TechwebToRedis techwebToRedis = new TechwebToRedis();
                    techwebToRedis.homepage(taskName);
                }else if (taskName.contains("www.199it.com/")) {
                    ItToRedis itToRedis = new ItToRedis();
                    itToRedis.homepage(taskName);
                }else if (taskName.contains("tech.163.com/telecom/")) {
                    Tech163ToRedis tech163ToRedis = new Tech163ToRedis();
                    tech163ToRedis.homePage(taskName);
                }else if (taskName.contains("www.pedaily.cn/all")) {
                    PedailyToRedis pedailyToRedis = new PedailyToRedis();
                    pedailyToRedis.homePage(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
