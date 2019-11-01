package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class PedailyToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(PedailyToRedis.class);

    public static void main(String[] args) {
        PedailyToRedis pedailyToRedis = new PedailyToRedis();
        pedailyToRedis.homePage("https://www.pedaily.cn/all");
    }
    public void homePage(String url) {
        try {
            for (int i = 1; i <= 5473; i++) {
                String s = url + "/" + i + "/";
                String get = HttpUtil.httpGetwithJudgeWord(s, "pedaily");
                Thread.sleep(1000);
                Document html = Jsoup.parse(get);
                Elements select = html.select("div.txt > h3 > a");
                for (Element element : select) {
                    String href = element.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", href);

                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
