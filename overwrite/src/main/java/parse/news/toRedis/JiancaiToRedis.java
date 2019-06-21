package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class JiancaiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiancaiToRedis.class);

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jiancai");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("tbody tr td a.black");
                for (Element e : categoryList) {
                    String href = "http://www.jiancai.com" + e.attr("href");
//                    System.out.println(href);
                    String plate = e.text();
                    paging(href, plate);
                }

            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            String replace = url.replace(".html", "");
            int number = 1;
            int total = 1952;
            for (number = 1; number < total; number++) {
                String link = replace + "-p" + number + ".html";//拼接链接地址
                newsList(link, plate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jiancai");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("li.liMainList a");
                for (Element e : select) {
                    String link = "http://www.jiancai.com" + e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", link);
                }
            } else {
                LOGGER.info("网页不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
