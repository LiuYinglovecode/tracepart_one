package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanJingtmtToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanJingtmtToRedis.class);
    private static String baseUrl = "http://www.lanjingtmt.com";

    public static void main(String[] args) {
        LanJingtmtToRedis lanJingtmtToRedis = new LanJingtmtToRedis();
        lanJingtmtToRedis.homepage("http://www.lanjingtmt.com/index.php?act=category");
    }

//分页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "lanjingtmt");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements pages = document.select("#chsPageBtn > a.no_hide").prev();
                String s = pages.text().replace("..", "");
                String attr = pages.attr("href");

                for (int i = 1; i <=Integer.parseInt(s) ; i++) {
                    String links = baseUrl.concat(attr.replace(s, String.valueOf(i)));
                    newsList(links);
                }
            }else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "lanjingtmt");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("#newsListUl > li > dt > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
