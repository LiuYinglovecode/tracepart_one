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


public class JueJiangToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(JueJiangToRedis.class);
    private static final String homepage = "http://www.ijuejiang.cn/kuaixun";

    public static void main(String[] args) {
        JueJiangToRedis JueJiangToRedis = new JueJiangToRedis();
        JueJiangToRedis.homepage("http://www.ijuejiang.cn/kuaixun");
    }

    //主页
    public void homepage(String url) {
        try {
            for (int i = 1; i <=520 ; i++) {
                String link = url.concat("/").concat(String.valueOf(i));
                String html = HttpUtil.httpGetwithJudgeWord(link, "ijuejiang");
                Thread.sleep(SleepUtils.sleepMin());
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements categoryList = document.select("div.hlgd-box > dl > dd > h3 > a");
                    for (Element e : categoryList) {
                        String links = e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", links);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
