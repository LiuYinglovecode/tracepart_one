package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class MydriversToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(MydriversToRedis.class);

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "mydrivers");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#cid_menu a,div.product_box > a");
                for (Element e : categoryList) {
                    if (e.attr("href").contains("mydrivers")) {
                        String href = "http:"+e.attr("href");
                        paging(href);
                    }else {
                        String href = "http://news.mydrivers.com" + e.attr("href");
                        paging(href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "mydrivers");
            if (null!=html) {
                Document parse = Jsoup.parse(html);
                Elements pagesNumber = parse.select("#newsleft > div.postpage > a");//获取总结数
                if (0 != pagesNumber.size()) {
                    String attr = pagesNumber.last().previousElementSibling().attr("href");
                    int total = 20;
                    for (int number = 1; number <= total; number++) {
                        String link = "http://news.mydrivers.com" + attr.replace("2.htm", number + ".htm");//拼接链接地址
                        newsList(link);
                    }
                }else {
                    newsList(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "mydrivers");
            if (null!=html) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("#newsleft > li > h3 > a");
                if (0!=select.size()) {
                    for (Element e : select) {
                        String link = "http:" + e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
