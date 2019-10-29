package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class FzfzjxToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(FzfzjxToRedis.class);
    private static final String beasUrl = "http://www.fzfzjx.com";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nheadnav > p > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("新闻首页") && !e.text().contains("本站速递")) {
                        String href =beasUrl + e.attr("href");
//                        System.out.println(href);
                        paging(href);
//                        newsList(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String href) {
        try {
            String replace = href.replace(".html", "");
            String html = HttpUtil.httpGetwithJudgeWord(href, "关于我们");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                int number = 1;
                String attr = parse.select("#pagenum > a").last().attr("href");
                String p = attr.split("p")[1].replace(".html", "");
                int total = Integer.valueOf(p).intValue();//类型转换
                for (number = 1; number <= total; number++) {
                    String url = replace + "_p" + number + ".html";
                    System.out.println(url);
                    newsList(url);
              }
            } else {
                System.out.println("页面不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (html!=null) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("dl.newslist > dt > p > a");
                for (Element e : select) {
                        String link =beasUrl + e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
