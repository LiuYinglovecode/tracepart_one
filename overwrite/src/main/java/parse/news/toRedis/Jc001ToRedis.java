package parse.news.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;



public class Jc001ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jc001ToRedis.class);
    private static String baseUrl = "https://www.ledinside.cn";

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "九正");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.subMenuCon > ul > li > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        if (e.text().contains("行业新闻") || e.text().contains("行业知识")) {
                            String href = new String(e.attr("href"));
                            for (int i = 1; i <=625 ; i++) {
                                newsList(href.concat("?p=").concat(String.valueOf(i)));
                            }
                        }
                    }
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "九正");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.box > ul > li > a");
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
