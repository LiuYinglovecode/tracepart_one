package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class CnmnToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CnmnToRedis.class);
    private static final String homepage = "http://www.cnmn.com.cn";

    public static void main(String[] args) {
        CnmnToRedis cnmnToRedis = new CnmnToRedis();
        cnmnToRedis.homepage("https://www.cnmn.com.cn/");
    }

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国有色网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nav > li > ul > li > a");
                for (Element e : categoryList) {
                    if (!e.attr("href").contains("/")
                            &&!e.text().contains("专题")
                            &&!e.text().contains("公告")) {
                        paging(homepage + "/" + e.attr("href"), e.text().trim());
                    } else {
                        paging(homepage + e.attr("href"), e.text().trim());
                    }
                }
            }
            LOGGER.info("www.cnmn.com.cn DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "有色");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String Total = document.select("#flickrpager > a[target=_self]").last().text();
                int total = Integer.valueOf(Total).intValue();
                for (number = 1; number < total + 1; number++) {
                    String nextPage = url + "&pageindex=" + number;
                    list.add(nextPage);
                }
                for (String link : list) {
                    newsList(link, plate);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "有色");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("#tab11-1 > h4 > a");
                for (Element e : newsListInfo) {
                    String href = homepage + e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
