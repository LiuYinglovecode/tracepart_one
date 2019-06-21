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
 * <a>http://www.cnmn.com.cn</a>
 * <p>中国有色网</p>
 *
 * @author chenyan
 */
public class cnmnNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(cnmnNews.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static final String homepage = "http://www.cnmn.com.cn";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        cnmnNews cnmnNews = new cnmnNews();
        cnmnNews.homepage(homepage);
        LOGGER.info("gkzhanNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    //主页
    private void homepage(String url) {
//        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国有色网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nav > li > ul > li > a");
                for (Element e : categoryList) {
                    if (!e.attr("href").contains("/")) {
                        paging(homepage + "/" + e.attr("href"), e.text().trim());
                    } else {
                        paging(homepage + e.attr("href"), e.text().trim());
                    }
                }
//                Elements plate = document.select("#nav > li > ul > li > a");
//                for (Element element : plate) {
//                    for (String link : list) {
//                        paging(link, element.text().trim());
//                    }
//                }
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "有色");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String title = document.select("#content > div > h4").text().trim();
                newsInfo.put("title", title);//标题
                newsInfo.put("time", document.select("span > span.time").text().trim());//发布时间
                newsInfo.put("amountOfReading", document.select("span > span.view").text().trim().replace("次浏览", ""));//阅读量
                newsInfo.put("source", document.select("p.info.clearfix.text-center > span:nth-child(1)").text().split("分类：")[0].split("来源： ")[1]);//来源
                newsInfo.put("text", document.select("#txtcont").text().trim());//新闻内容
                Elements split = document.select("p.info.clearfix.text-center > span:nth-child(1)");
                if (split.text().contains("作者：")) {
                    newsInfo.put("author", split.text().split("作者：")[1]);//作者
                } else {
                    newsInfo.put("author", document.select("#content > div > p.actor").text().split("：")[1]);//作者
                }
                Elements img = document.select("#txtcont > p > img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                }
                newsInfo.put("url", url);//链接地址
                newsInfo.put("plate", plate);//板块
                newsInfo.put("crawlerId", "52");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                insert(newsInfo);
                mysqlUtil.insertNews(newsInfo, "crawler_news", title);
                esUtil.writeToES(newsInfo, "crawler-news-", "doc");
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
