package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YejinzgToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(YejinzgToRedis.class);
    private static String baseUrl = "http://www.yejinzg.com";

    public static void main(String[] args) {
        YejinzgToRedis yejinzgToRedis = new YejinzgToRedis();
        yejinzgToRedis.homepage("http://www.yejinzg.com/list/28.html");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "yejinzg");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("em.more > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    ping(href);
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "yejinzg");
            Document document = Jsoup.parse(html);
            Elements tailPage = document.select("#pages > a.a1.n").prev();
            String total = tailPage.text();
            String attr = tailPage.attr("href");
            int number;
            for (number = 1; number <=Integer.parseInt( total ); number++) {
                String nextPage = baseUrl.concat( attr.replace(total,String.valueOf(number)) );
                category(nextPage);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "yejinzg");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.left.box.cat_box > ul > li > a");
                if (detailList.size()!=0) {
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
