package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QiegejiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ca800ToRedis.class);
    private static String link = "http://www.qiegeji.org/";

    public static void main(String[] args) {
        QiegejiToRedis qiegejiToRedis = new QiegejiToRedis();
        qiegejiToRedis.homepage("http://www.qiegeji.org/new_list.asp?id=2");
    }

    public void homepage(String url) {
        try {
            for (int i = 1; i <= 4543; i++) {
                String s = url.concat("&page=").concat(String.valueOf(i));
                String html = HttpUtil.httpGetwithJudgeWord(s, "qiegeji");
                Document document = Jsoup.parse(html);
                Elements select = document.select("div.box-blue > ul > li > a");
                for (Element element : select) {
                    String attr = link.concat(element.attr("href"));
                    RedisUtil.insertUrlToSet("toCatchUrl", attr);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
