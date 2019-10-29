package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import java.net.URL;


public class FengjToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(FengjToRedis.class);

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "fengj");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.ltit > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    paging(href);
                }

            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            String replace = url.replace("1.html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "fengj");
            Document parse = Jsoup.parse(html);
            String pagesNumber = parse.select("span.xxts").text();//获取总条数
            int ceil = (int) Math.ceil(Double.parseDouble(pagesNumber) / 10);//获取总页数
            int number = 1;
            for (number = 1; number < ceil; number++) {
                String link = replace + number + ".html";//拼接链接地址
                newsList(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "fengj");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.ml_topic > a");
            for (Element e : select) {
                String link = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
