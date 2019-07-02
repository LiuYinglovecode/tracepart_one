package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
import util.ESUtil;
import util.mysqlUtil;
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
 * <a>http://www.chinahightech.com</a>
 * <p>中国高新网</p>
 *
 * @author chenyan
 */
public class chinahightechNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(chinahightechNews.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static final String homepage = "http://www.chinahightech.com";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        chinahightechNews chinahightechNews = new chinahightechNews();
        chinahightechNews.homepage(homepage);
        LOGGER.info("machine365 DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    //主页
    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国高新网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nav li ul li a");
                for (Element element : categoryList) {
                    String href = homepage + element.attr("href");
                    String plate = element.text();
                    newsList(href, plate);
                    paging(href, plate);
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
            int number = 2;
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国高新网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String Total = document.select("#pages.text-c a.a1").last().previousElementSibling().text();
                System.out.println(Total);
                int total = Integer.valueOf(Total).intValue();
                for (number = 2; number < total + 1; number++) {
                    String nextPage = url + number + ".html";
                    list.add(nextPage);
                }
                for (String link : list) {
                    System.out.println("下一页：" + link);
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国高新网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("#index-gz.list-a ul li h3 a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
                newsInfo(link, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻信息
    private void newsInfo(String url, String plate) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国高新网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String title = document.select("div.title_nr h1").text().trim();
                newsInfo.put("title", title);//标题
                newsInfo.put("time", document.select("div.addtime").text().trim());//发布时间
                Elements source = document.select("div.source");
                if (source.text().contains("作者：")) {
                    newsInfo.put("source", source.text().split("作者：")[0].split("：")[1]);//来源
                } else {
                    newsInfo.put("source", source.text().split("评论：")[0].split("：")[1]);//来源
                }
                Elements author = document.select("div.source");
                if (author.text().contains("作者：")) {
                    newsInfo.put("author", author.text().split("作者：")[1].split("评论：")[0]);//作者
                } else {
                    newsInfo.put("author", document.select("div.content p").last().text().split("：")[1].replace(")", ""));//作者
                }
                newsInfo.put("text", document.select("div.content").text().trim());//新闻内容
                Elements img = document.select("div.content p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                }
                newsInfo.put("url", url);//链接地址
                newsInfo.put("plate", plate);//板块
                newsInfo.put("crawlerId", "54");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                insert(newsInfo);
                mysqlUtil.insertNews(newsInfo, "crawler_news", title);
                esUtil.writeToES(newsInfo, "crawler-news-", "doc", null);
            } else {
                LOGGER.info("页面不存在");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info) {
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
