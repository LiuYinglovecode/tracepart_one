package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class maiNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(maiNews.class);
    private static Map<String, String> header = new HashMap();
    private static final String homepage = "http://www.86mai.com/news/";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141");
        maiNews maiNews = new maiNews();
        maiNews.homepage(homepage);
        LOGGER.info("maiNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.m_r.f_l > div.box_body > table[cellpadding] > tbody > tr > td > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            Document parse = Jsoup.parse(html);
            String pagesNumber = parse.select("div.pages cite").text().split("/")[1].replace("页", "");//获取总结数
            int total = Integer.valueOf(pagesNumber).intValue() + 1;//类型转换
            int number = 1;
            for (number = 1; number < total; number++) {
                String link = replace + "-" + number + ".html";//拼接链接地址
                System.out.println("下一页：" + link);
                newsList(link, plate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.catlist > ul > li> a");
            for (Element e : select) {
                String link = e.attr("href");
                newsInfo(link, plate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsInfo(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("#title").text().trim();
                info.put("title", title);
                Elements select = parse.select("div.info");
                if (select.size() != 0) {
                    if (!select.text().contains("来源：")) {
                        info.put("time", select.text().trim().split("浏览次数：")[0].split("：")[1]);
                        info.put("amountOfReading", select.select("#hits").text().trim());
                    } else {
                        info.put("time", select.text().split("来源：")[0].split("：")[1]);
                        info.put("source", select.text().split("来源：")[1].split("作者：")[0]);
                        info.put("author", select.text().split("来源：")[1].split("作者：")[1].split(" 浏览次数")[0]);
                        info.put("amountOfReading", select.select("#hits").text().trim());
                    }
                }
                info.put("text", parse.select("#article").text().trim());
                Elements images = parse.select("#article > div > p > img");
                if (images.size() != 0) {
                    for (Element image : images) {
                        String src = image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                }
                info.put("plate", plate);
                info.put("crawlerId", "65");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", title);
                esUtil.writeToES(info, "crawler-news-", "doc", null);
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
