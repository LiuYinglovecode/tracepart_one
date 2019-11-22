package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ca168ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ca168ToRedis.class);

    public static void main(String[] args) {
        Ca168ToRedis ca168ToRedis = new Ca168ToRedis();
        ca168ToRedis.category("http://news.ca168.com/News/");
    }

    public void category(String url) {
        try {
            for (int i = 1; i <= 1548; i++) {
                String s = url + i + ".html";
                String html = HttpUtil.httpGetwithJudgeWord(s, "ca168");
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements detailList = document.select("div.lb_left > dl > dd.titles > a");
                    if (detailList.size() != 0) {
                        for (Element e : detailList) {
                            RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                        }

                    } else {
                        LOGGER.info("该页面为空");
                    }
                } else {
                    LOGGER.info("category null");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
