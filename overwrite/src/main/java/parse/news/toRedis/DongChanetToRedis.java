package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DongChanetToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(DongChanetToRedis.class);
    private static String links = "http://www.dongchanet.com";

    public static void main(String[] args) {
        DongChanetToRedis dongChanetToRedis = new DongChanetToRedis();
        dongChanetToRedis.homepage("http://www.dongchanet.com/");
    }

    //主页
    public void homepage(String url) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(url, "dongchanet");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.nav li h2 a");
                for (Element e : categoryList) {
                    if (e.text().contains("汽车") || e.text().contains("财经") || e.text().contains("科技")) {
                        String link = e.attr("href");
//                            newsList(link);
                        paging(link);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "dongchanet");
            Thread.sleep(SleepUtils.sleepMin());

            if (null != html) {
                Document document = Jsoup.parse(html);
                Element tailPage = document.select("a.a1").last().previousElementSibling();
                String text = tailPage.text().trim();
                String attr = tailPage.attr("href");
                for (int i = 2; i <= Integer.parseInt(text); i++) {
                    String url = links.concat(attr.replace(text, String.valueOf(i)));
                    newsList(url);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "dongchanet");
            Thread.sleep(SleepUtils.sleepMin());

            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("p.title > a");
                for (Element e : categoryList) {
                    String link = e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
