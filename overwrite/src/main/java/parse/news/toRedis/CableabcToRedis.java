package parse.news.toRedis;

import Utils.RedisUtil;
import news.parse.cableabcNews;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class CableabcToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CableabcToRedis.class);

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.list ul li a");
                for (Element element : categoryList) {
                    if (!element.text().equals("资讯首页") && !element.text().equals("招标") && !element.text().equals("展会")) {
                        String href = element.attr("href");
                        String plate = element.text();
                        paging(href, plate);
                    }
                }
            }
            LOGGER.info("news.cableabc.com  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 0;
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String href = document.select("#main_cont_ContentPlaceHolder1_pager.pager a").last().attr("href");
                String html1 = HttpUtil.httpGetwithJudgeWord(href, "电缆网");
                Document document1 = Jsoup.parse(html1);
                String Total = document1.select("#main_cont_ContentPlaceHolder1_pager.pager span").text().trim();
                int total = Integer.valueOf(Total).intValue();
                for (number = 0; number < total; number++) {
                    if (url.contains("0.html")) {
                        String replace = url.replace("0.html", "");
                        list.add(replace + number + ".html");
                    }
                    if (url.contains("http://special.cableabc.com/")) {
                        list.add(url + "speciallist_" + number + ".html");
                    }
                    if (url.contains("http://material.cableabc.com/")) {
                        list.add(url + "materIndex_" + number + ".html");
                    }
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("h2.list31_title1 a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
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
