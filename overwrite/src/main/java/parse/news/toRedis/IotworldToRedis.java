package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IotworldToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(IotworldToRedis.class);
private static String link = "http://www.iotworld.com.cn";

    public static void main(String[] args) {
        IotworldToRedis IotworldToRedis = new IotworldToRedis();
        IotworldToRedis.homepage("http://www.iotworld.com.cn/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "iotworld");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.appli > ul > li > a");
                for (Element e : categoryList) {
                    String href =link.concat(e.attr("href"));
                    ping(href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void ping(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "iotworld");
            if (null != html) {
                Document doc = Jsoup.parse(html);
                Elements select = doc.select("div.IndexCompLT.clearfix > ul > li > a");
                String links = link.concat(select.attr("href")) ;
                category(links);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "iotworld");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("#dataPageList > li > div > h4 > a");
                if (0 != detailList.size()) {
                    for (Element e : detailList) {
                        String attr = e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl",attr);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
