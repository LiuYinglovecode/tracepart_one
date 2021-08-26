package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class LeiphoneToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeiphoneToRedis.class);

    public static void main(String[] args) {
        LeiphoneToRedis leiphoneToRedis = new LeiphoneToRedis();
        leiphoneToRedis.homepage("https://www.leiphone.com/category/iot");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.wrapper > ul > li > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("GAIR")) {
                        String href = e.attr("href");
                        Thread.sleep(2000);
                        ping(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void ping(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "加入我们");
            Document doc = Jsoup.parse(html);
            Element select = doc.select("div.pages > a").last();
            if (null!=select) {
                int count = Integer.parseInt(select.text());
                int number;
                for (number = 1; number <= count; number++) {
                    String replace = url+"/page/"+number;
                    Thread.sleep(2000);
                    category(replace);
                }
            }else {
                category(url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            Thread.sleep(2000);
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.word > h3 > a");
                if (0 != detailList.size()) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl",(e.attr("href")));
                    }
                }else {
                    LOGGER.info("该页面为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
