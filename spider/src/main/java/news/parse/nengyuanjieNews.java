package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;


import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>http://www.nengyuanjie.net/</a>
 * <a>News：能源界</a>
 *
 * @author:chenyan
 */
public class nengyuanjieNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(nengyuanjieNews.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static final String homepage = "http://www.nengyuanjie.net/";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        nengyuanjieNews nengyuanjieNews = new nengyuanjieNews();
        nengyuanjieNews.homepage(homepage);
        LOGGER.info("nengyuanjieNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    //首页
    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("a.sub");
                for (Element e : categoryList) {
                    if (!e.text().equals("大讲堂") && !e.text().equals("访谈") && !e.text().equals("会议之声")) {
                        String link = e.attr("href");
                        String plate = e.text();
//                        Thread.sleep(7000);
                        paging(link, plate);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //分页
    private void paging(String url, String plate) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            Document document = Jsoup.parse(html);
            String Total = document.select("a.a1").prev().text();
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total + 1; number++) {
                String nextPage = url + "?&page=" + number;
                list.add(nextPage);
            }
            for (String link : list) {
                System.out.println("下一页：" + link);
                Thread.sleep(7000);
                newsList(link, plate);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("div.info h3 a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                Thread.sleep(7000);
                newsInfo(href, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
    新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
            把链接放到集合中，在进行存储。
     */
    private void newsInfo(String url, String plate) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            if (html != null) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select("h1.art-title").text().trim());//标题
                String select = document.select("span.desc.mt15").text();
                newsInfo.put("time", select.split("来源：")[0]);
                newsInfo.put("source", select.split("来源：")[1].split("浏览：")[0]);
                newsInfo.put("amountOfReading", select.split("浏览：")[1]);
                Elements text = document.select("div.content");//新闻内容
                if (text.size() != 0) {
                    newsInfo.put("text", text.text());
                }
                Elements img = document.select("div.content p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片
                    }
                }
            } else {
                LOGGER.info("页面不存在！");
            }

            newsInfo.put("plate", plate);//板块
            newsInfo.put("crawlerId", "60");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            insert(newsInfo);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc", null);
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
