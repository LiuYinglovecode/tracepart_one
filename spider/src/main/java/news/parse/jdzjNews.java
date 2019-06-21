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

/**
 * <a>http://www.jdzj.com/news/</a>
 * <p>机电之家</p>
 * @author chenyan
 */
public class jdzjNews {

    private static final Logger LOGGER = LoggerFactory.getLogger(jiancaiNews.class);
    private static Map<String, String> header = new HashMap();
    private static final String homepage = "http://www.jdzj.com/news/";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        jdzjNews ic98News = new jdzjNews();
        ic98News.homepage(homepage);
        LOGGER.info("jdzjNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.eif-header-nav ul li");
                for (Element e : categoryList) {
                    if (!e.text().contains("首　页")) {
                        String href = "http://www.jdzj.com/news/zx" + e.attr("zx")+".html";
//                        System.out.println(href);
                    String plate = e.text();
                    paging(href,plate);
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
    private void paging(String url, String plate) {
        try {
            String replace = url.replace(".html", "").replace("zx","");
            int number = 1;
            int total = 3000;
            for (number = 1; number < total; number++) {
                String link = replace +"_0__"+ number + ".html";//拼接链接地址
                System.out.println("下一页："+link);
                newsList(link,plate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "站内导航");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String text = parse.select("div.pageNav a").last().text().trim();
                if (text.equals("下一页")) {
                    Elements select = parse.select("div.singleNews h3 a");
                    for (Element e : select) {
                        String link = "http://www.jdzj.com" + e.attr("href");
                        newsInfo(link, plate);
                    }
                }else {
                    LOGGER.info("最后一页！");
                }
            } else {
                LOGGER.info("网页不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsInfo(String url,String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新手入门");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url",url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("div.newsInfo > h3").text().trim();
                info.put("title",title);
                info.put("source",parse.select("span.laiyuan").text().trim().replace("来源：",""));
                info.put("time",parse.select("span.time").text().trim().replace("时间：",""));
                info.put("amountOfReading",parse.select("span.times").text().trim().replace("访问：","").replace("次",""));
                info.put("text",parse.select("div.newsContent").text().trim());
                Elements images = parse.select("div.newsContent p img");
                for (Element image : images) {
                    String src = image.attr("src");
                    imgs.add(src);
                    info.put("images", imgs.toString());
                }
                info.put("plate",plate);
                info.put("crawlerId", "66");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", title);
                esUtil.writeToES(info, "crawler-news-", "doc");
                System.out.println(info);
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
