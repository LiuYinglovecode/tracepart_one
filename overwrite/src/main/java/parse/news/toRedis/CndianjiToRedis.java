package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class CndianjiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CndianjiToRedis.class);
    private static String baseUrl = "http://www.cndianji.cn";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻资讯");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.rec_ntbox > span > a");
                for (Element e : categoryList) {
                    String href = baseUrl + e.attr("href");
                    ping(href);
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("news.ddc.net.cn DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void ping(String url) {
        String replace = url.replace(".html","");
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻资讯");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Element doc = document.select("div.pages.mt25.mb10 > a").last();
                String Total = doc.attr("href").replace("article-", "").split("-")[1].replace(".html","");
                int total = Integer.valueOf(Total).intValue();//转行类型
                int number = 1;
                for (number = 1; number <= total; number++) {
//                    http://www.cndianji.cn/article-9000058-2.html
                    String nextPage = replace + "-" + number + ".html";
//                    System.out.println(nextPage);
                    category(nextPage);
                }

            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻资讯");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select(".ullist3 li h3 a");
                for (Element e : detailList) {
                    String href =baseUrl + e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", href);
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
