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
                    chuandongToRedis.paging(taskName);
                } else if (taskName.contains("www.chinacrane.net/")) {
                    ChinacraneToRedis chinacraneToRedis = new ChinacraneToRedis();
                    chinacraneToRedis.homepage(taskName);
                } else if (taskName.contains("www.findzd.com/industry/")) {
                    FindzdToRedis findzdToRedis = new FindzdToRedis();
                    findzdToRedis.homepage(taskName);
                } else if (taskName.contains("www.fzfzjx.com/news/")) {
                    FzfzjxToRedis fzfzjxToRedis = new FzfzjxToRedis();
                    fzfzjxToRedis.homepage(taskName);
                } else if (taskName.contains("www.hometex114.com/News/")) {
                    Hometex114ToRedis hometex114ToRedis = new Hometex114ToRedis();
                    hometex114ToRedis.homepage(taskName);
                } else if (taskName.contains("china.fengj.com/news/")) {
                    FengjToRedis fengjToRedis = new FengjToRedis();
                    fengjToRedis.homepage(taskName);
                } else if (taskName.contains("www.pm8.cn/news/")) {
                    Pm8ToRedis pm8ToRedis = new Pm8ToRedis();
                    pm8ToRedis.homepage(taskName);
                } else if (taskName.contains("www.huajx.com/news/")) {
                    HuajxToRedis huajxToRedis = new HuajxToRedis();
                    huajxToRedis.homepage(taskName);
                } else if (taskName.contains("www.hooshong.com/news/")) {
                    HooshongToRedis hooshongToRedis = new HooshongToRedis();
                    hooshongToRedis.homepage(taskName);
                } else if (taskName.contains("www.cndianji.cn/article")) {
                    CndianjiToRedis CndianjiToRedis = new CndianjiToRedis();
                    CndianjiToRedis.homepage(taskName);
                } else if (taskName.contains("www.eapad.cn/")) {
                    EapadToRedis EapadToRedis = new EapadToRedis();
                    EapadToRedis.homepage(taskName);
                } else if (taskName.contains("www.ic37.com")) {
                    Ic37ToRedis Ic37ToRedis = new Ic37ToRedis();
                    Ic37ToRedis.homepage(taskName);
                } else if (taskName.contains("www.cnpv.com/News")) {
                    CnpvToRedis cnpvToRedis = new CnpvToRedis();
                    cnpvToRedis.homepage(taskName);
                } else if (taskName.contains("www.leiphone.com/category/iot")) {
                    LeiphoneToRedis leiphoneToRedis = new LeiphoneToRedis();
                    leiphoneToRedis.homepage(taskName);
                } else if (taskName.contains("www.cntma.com/news/")) {
                    CntmaToRedis CntmaToRedis = new CntmaToRedis();
                    CntmaToRedis.homepage(taskName);
                } else if (taskName.contains("http://www.gongkong.com/news/")) {
                    GongkongToRedis GongkongToRedis = new GongkongToRedis();
                    GongkongToRedis.homepage(taskName);
                } else if (taskName.contains("www.techweb.com.cn/")) {
                    TechwebToRedis techwebToRedis = new TechwebToRedis();
                    techwebToRedis.homepage(taskName);
                } else if (taskName.contains("www.199it.com/")) {
                    ItToRedis itToRedis = new ItToRedis();
                    itToRedis.homepage(taskName);
                } else if (taskName.contains("tech.163.com/telecom/")) {
                    Tech163ToRedis tech163ToRedis = new Tech163ToRedis();
                    tech163ToRedis.homePage(taskName);
                } else if (taskName.contains("www.pedaily.cn/all")) {
                    PedailyToRedis pedailyToRedis = new PedailyToRedis();
                    pedailyToRedis.homePage(taskName);
                } else if (taskName.contains("www.chinaz.com/news/")) {
                    ChinazToRedis chinazToRedis = new ChinazToRedis();
                    chinazToRedis.paging(taskName);
                } else if (taskName.contains("news.newseed.cn/")) {
                    NewSeedToRedis newSeedToRedis = new NewSeedToRedis();
                    newSeedToRedis.homepage(taskName);
                } else if (taskName.contains("cn.technode.com/")) {
                    TechnodeToRedis technodeToRedis = new TechnodeToRedis();
                    technodeToRedis.homepage(taskName);
                } else if (taskName.contains("news.mydrivers.com/")) {
                    MydriversToRedis mydriversToRedis = new MydriversToRedis();
                    mydriversToRedis.homepage(taskName);
                } else if (taskName.contains("www.cnmo.com/news/")) {
                    CnmoToRedis cnmoToRedis = new CnmoToRedis();
                    cnmoToRedis.homepage(taskName);
                } else if (taskName.contains("www.ccidnet.com")) {
                    CcidnetToRedis ccidnetToRedis = new CcidnetToRedis();
                    ccidnetToRedis.homepage(taskName);
                } else if (taskName.contains("www.tybaba.com/news/")) {
                    TybabaToRedis tybabaToRedis = new TybabaToRedis();
                    tybabaToRedis.homepage(taskName);
                } else if (taskName.contains("www.ledinside.cn/news.html")) {
                    LedinsideToRedis ledinsideToRedis = new LedinsideToRedis();
                    ledinsideToRedis.homepage(taskName);
                } else if (taskName.contains("www.pipew.com/news/index.asp")) {
                    PipewToRedis pipewToRedis = new PipewToRedis();
                    pipewToRedis.homepage(taskName);
                } else if (taskName.contains("www.ofweek.com")) {
                    OfWeekToRedis ofWeekToRedis = new OfWeekToRedis();
                    ofWeekToRedis.homepage(taskName);
                } else if (taskName.contains("product.dzsc.com/news")) {
                    DzscToRedis dzscToRedis = new DzscToRedis();
                    dzscToRedis.homepage(taskName);
                } else if (taskName.contains("www.cscsf.com/")) {
                    CscsfToRedis cscsfToRedis = new CscsfToRedis();
                    cscsfToRedis.homepage(taskName);
                } else if (taskName.contains("cinic.org.cn")) {
                    CinicToRedis cinicToRedis = new CinicToRedis();
                    cinicToRedis.homepage(taskName);
                } else if (taskName.contains("news.chemnet.com")) {
                    ChemNetToRedis chemNetToRedis = new ChemNetToRedis();
                    chemNetToRedis.homepage(taskName);
                } else if (taskName.contains("news.jc001.cn")) {
                    Jc001ToRedis jc001ToRedis = new Jc001ToRedis();
                    jc001ToRedis.homepage(taskName);
                } else if (taskName.contains("www.alliii.com/news/")) {
                    AlliiiToRedis alliiiToRedis = new AlliiiToRedis();
                    alliiiToRedis.homepage(taskName);
                } else if (taskName.contains("www.iyiou.com")) {
                    IyiouToRedis iyiouToRedis = new IyiouToRedis();
                    iyiouToRedis.homepage(taskName);
                } else if (taskName.contains("www.thepaper.cn")) {
                    ThepaperToRedis thepaperToRedis = new ThepaperToRedis();
                    thepaperToRedis.homepage(taskName);
                } else if (taskName.contains("www.sootoo.com/")) {
                    SooTooToRedis SooTooToRedis = new SooTooToRedis();
                    SooTooToRedis.homepage(taskName);
                } else if (taskName.contains("www.southmoney.com/")) {
                    SouthMoneyToRedis southMoneyToRedis = new SouthMoneyToRedis();
                    southMoneyToRedis.homepage(taskName);
                } else if (taskName.contains("www.qianzhan.com/indynews/")) {
                    QianZhanToRedis qianZhanToRedis = new QianZhanToRedis();
                    qianZhanToRedis.homepage(taskName);
                } else if (taskName.contains("http://www.qlmoney.com")) {
                    QlmoneyToRedis qlmoneyToRedis = new QlmoneyToRedis();
                    qlmoneyToRedis.homepage(taskName);
                } else if (taskName.contains("www.cena.com.cn")) {
                    CenaToRedis cenaToRedis = new CenaToRedis();
                    cenaToRedis.homepage(taskName);
                } else if (taskName.contains("www.kejixun.com")) {
                    KeJiXunToRedis keJiXunToRedis = new KeJiXunToRedis();
                    keJiXunToRedis.homepage(taskName);
                }else if (taskName.contains("www.jc35.com/news")) {
                    Jc35ToRedis jc35ToRedis = new Jc35ToRedis();
                    jc35ToRedis.homepage(taskName);
                }else if (taskName.contains("www.roboticschina.com/news/")) {
                    RoboticschinaToRedis roboticschinaToRedis = new RoboticschinaToRedis();
                    roboticschinaToRedis.homepage(taskName);
                }else if (taskName.contains("www.ilinki.net/")) {
                    IlinkiToRedis ilinkiToRedis = new IlinkiToRedis();
                    ilinkiToRedis.homepage(taskName);
                }else if (taskName.contains("www.ceeia.com/Index.aspx")) {
                    CeeiaToRedis ceeiaToRedis = new CeeiaToRedis();
                    ceeiaToRedis.homepage(taskName);
                }else if (taskName.contains("www.todayim.cn/news/3.html")) {
                    TodayimToRedis todayimToRedis = new TodayimToRedis();
                    todayimToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.jdw001.com/portal.php")) {
                    Jdw001ToRedis jdw001ToRedis = new Jdw001ToRedis();
                    jdw001ToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.dingkeji.com/")) {
                    DingKeJiToRedis dingKeJiToRedis = new DingKeJiToRedis();
                    dingKeJiToRedis.homepage(taskName);
                }else if (taskName.contains("http://news.rfidworld.com.cn/")) {
                    RfidworldToRedis rfidworldToRedis = new RfidworldToRedis();
                    rfidworldToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.ailab.cn/")) {
                    AilabToRedis ailabToRedis = new AilabToRedis();
                    ailabToRedis.homepage(taskName);
                }else if (taskName.contains("www.2025china.cn/sec/hotnews")) {
                    ZhiNengWangToRedis zhiNengWangToRedis = new ZhiNengWangToRedis();
                    zhiNengWangToRedis.listNews(taskName);
                }else if (taskName.contains("news.ca168.com")) {
                    Ca168ToRedis ca168ToRedis = new Ca168ToRedis();
                    ca168ToRedis.category(taskName);
                }else if (taskName.contains("www.zhiding.cn")) {
                    ZhiDingToRedis zhiDingToRedis = new ZhiDingToRedis();
                    zhiDingToRedis.homepage(taskName);
                }else if (taskName.contains("mma.vogel.com.cn")) {
                    MmaVogelToRedis mmaVogelToRedis = new MmaVogelToRedis();
                    mmaVogelToRedis.listNews(taskName);
                }else if (taskName.contains("www.cet.com.cn/")) {
                    CetToRedis cetToRedis = new CetToRedis();
                    cetToRedis.homepage(taskName);
                }else if (taskName.contains("www.lanjingtmt.com/index.php?act=category")) {
                    LanJingtmtToRedis lanJingtmtToRedis = new LanJingtmtToRedis();
                    lanJingtmtToRedis.homepage(taskName);
                }else if (taskName.contains("https://www.nkj.cn/zixun")) {
                    NkjToRedis nkjToRedis = new NkjToRedis();
                    nkjToRedis.homepage(taskName);
                }else if (taskName.contains("https://news.znds.com/")) {
                    ZNDSToRedis zNDSToRedis = new ZNDSToRedis();
                    zNDSToRedis.homepage(taskName);
                }else if (taskName.contains("www.cngold.com.cn/")) {
                    CngoldToRedis cngoldToRedis = new CngoldToRedis();
                    cngoldToRedis.homepage(taskName);
                }else if (taskName.contains("www.ijuejiang.cn/kuaixun")) {
                    JueJiangToRedis JueJiangToRedis = new JueJiangToRedis();
                    JueJiangToRedis.homepage(taskName);
                }else if (taskName.contains("www.dongchanet.com/")) {
                    DongChanetToRedis dongChanetToRedis = new DongChanetToRedis();
                    dongChanetToRedis.homepage(taskName);
                }else if (taskName.contains("www.hxny.com/guangfu/")) {
                    HxnyToRedis hxnyToRedis = new HxnyToRedis();
                    hxnyToRedis.homepage(taskName);
                }else if (taskName.contains("www.looktmt.com/")) {
                    LooktmtToRedis looktmtToRedis = new LooktmtToRedis();
                    looktmtToRedis.homepage(taskName);
                }else if (taskName.contains("http://finance.eastmoney.com/")) {
                    EastmoneyToRedis eastmoneyToRedis = new EastmoneyToRedis();
                    eastmoneyToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.chinaipo.com/")) {
                    ChinaipoToRedis chinaipoToRedis =new ChinaipoToRedis();
                    chinaipoToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.jsjxmhw.com/Html/News.asp?SortID=10&SortPath=0,10,")) {
                    JsjxmhwToRedis jsjxmhwToRedis = new JsjxmhwToRedis();
                    jsjxmhwToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.mccet.com/")) {
                    MccetToRedis mccetToRedis = new MccetToRedis();
                    mccetToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.yejinzg.com/list/28.html")) {
                    YejinzgToRedis yejinzgToRedis = new YejinzgToRedis();
                    yejinzgToRedis.homepage(taskName);
                }else if (taskName.contains("https://www.kq81.com/")) {
                    Kq81ToRedis kq81ToRedis = new Kq81ToRedis();
                    kq81ToRedis.home(taskName);
                }else if (taskName.contains("http://www.qiegeji.org/new_list.asp?id=2")) {
                    QiegejiToRedis qiegejiToRedis = new QiegejiToRedis();
                    qiegejiToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.iotworld.com.cn/")) {
                    IotworldToRedis IotworldToRedis = new IotworldToRedis();
                    IotworldToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.cctime.com/")) {
                    CctimeToRedis cctimeToRedis = new CctimeToRedis();
                    cctimeToRedis.homePage(taskName);
                }else if (taskName.contains("https://www.icloudnews.net/")) {
                    IcloudNewsToRedis icloudNewsToRedis = new IcloudNewsToRedis();
                    icloudNewsToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.jingmeiti.com/")) {
                    JingMeiTiToRedis jingMeiTiToRedis = new JingMeiTiToRedis();
                    jingMeiTiToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.laserfair.com/")) {
                    LaserfairToRedis laserfairToRedis = new LaserfairToRedis();
                    laserfairToRedis.homepage(taskName);
                }else if (taskName.contains("http://www.idacn.org/news")) {
                    IdacnToRedis idacnToRedis = new IdacnToRedis();
                    idacnToRedis.paging(taskName);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            LOGGER.info(taskName + " : 执行完毕");
        }
    }
}
