package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
import news.utils.ESUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>http://www.ca800.com/news/all/l_1.html</a>
 * <P>News：中国自动化网</P>
 * @author chenyan
 */
public class ca800News {
    private static final Logger LOGGER = LoggerFactory.getLogger(nengyuanjieNews.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static final String homepage = "http://www.ca800.com/news/all/l_1.html";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        ca800News ca800News = new ca800News();
        ca800News.homepage(homepage);
        LOGGER.info("gkzhanNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    //首页
    private void homepage(String url) {
        try {
            String replace = url.replace("1.html", "");
            int number = 1;
            int total = 11341;
            for (number = 1; number < total; number++) {
                String nextPage = replace + number + ".html";
                String html = HttpUtil.httpGetwithJudgeWord(nextPage, "中国自动化网");
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements categoryList =document.select("div.newslist_title a");
                    for (Element element : categoryList) {
                        String href ="http://www.ca800.com"+element.attr("href");
                        newsInfo(href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
    新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
            把链接放到集合中，在进行存储。
     */
    private void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url",url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url,"中国自动化网");
            if (html!=null) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select("div.newsdetail.border.fl h1").text().trim());//标题
                Elements select = document.select("div.title_bar.f12.h25");
                if (select.size() != 0) {
                    if (select.text().contains("新闻类型")) {
                        newsInfo.put("time", select.text().split("新闻类型")[0].split("：")[1]);
                        newsInfo.put("plate", document.select("div.title_bar.f12.h25 a").text().trim());
                    }
                } else {
                    Elements select1 = document.select("div.title_bar");
                    if (select1.text().contains("来源：")) {
                        newsInfo.put("time", select1.text().split("来源：")[0].split("：")[1]);
                        newsInfo.put("plate", document.select("div.title_bar a").text().trim());
                    }
                }

                Elements text = document.select("div.newsdetail_con");//新闻内容
                if (text.size() != 0) {
                    newsInfo.put("text", text.text());
                    Elements img = text.select("div > img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                            newsInfo.put("images", imgsList.toString());//图片
                        }
                    }
                } else {
                    Elements text1 = document.select("div.newsdetail.border.fl div.detail");//新闻内容
                    newsInfo.put("text", text1.text());
                    Elements img = text1.select("p.MsoNormal img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                            newsInfo.put("images", imgsList.toString());//图片
                        }
                    }
                }
            }else {
                LOGGER.info("页面不存在！");
            }
            newsInfo.put("crawlerId", "59");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            System.out.println(newsInfo);
            insert(newsInfo);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private static void insert(JSONObject info) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.newsInsert(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
