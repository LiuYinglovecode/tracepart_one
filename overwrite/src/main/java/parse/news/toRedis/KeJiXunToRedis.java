package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class KeJiXunToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeJiXunToRedis.class);
    private static String link = new String("http://www.kejixun.com");

    public static void main(String[] args) {
        KeJiXunToRedis zhiDingToRedis = new KeJiXunToRedis();
        zhiDingToRedis.homepage("http://www.kejixun.com/chanjing/");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "kejixun");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Element elements = document.select("div.paging a").last().previousElementSibling();
                String total = elements.text();
                String end = elements.attr("href");
                for (int i = 1; i <= Integer.parseInt(total); i++) {
                    String href = link.concat(end).replace(total,String.valueOf(i));
                    category(href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "kejixun");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("figcaption.title > a");
                if (!detailList.isEmpty()) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                    }
                }else {
                    LOGGER.info("该页面为空");
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
