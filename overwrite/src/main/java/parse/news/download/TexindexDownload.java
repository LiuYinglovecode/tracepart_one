package parse.news.download;

import Utils.ForMat;
import Utils.NewsMd5;
import Utils.RedisUtil;
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
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>http://www.texindex.com.cn/news/</a>
 * <a>news：纺织网</a>
 *
 * @author:chenyan
 */
public class TexindexDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(TexindexDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        TexindexDownload texindexDownload = new TexindexDownload();
        texindexDownload.newsInfo("http://www.texindex.com.cn/Articles/2019-9-17/445770.html");
    }
    //  新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document document = Jsoup.parse(html);
            String title = document.select("td.RightItemBody div h1").text().trim();
            newsInfo.put("title", title);
            Elements select = document.select("td.RightItemBody div.000000A,tbody > tr > td > table.RightItemBG > tbody > tr > td.RightItemBody");
            select.select("div").remove();
            String[] s = select.text().split(" ");
            for (String s1 : s) {
                if (s1.contains("年")){
                    newsInfo.put("time", ForMat.getDatetimeFormat(s1));
                }
            }


            newsInfo.put("source", select.text().split("/ ")[1].split(" ")[2].trim());
            Elements text = document.select("div#zoom");
            newsInfo.put("text", text.text().trim());
            newsInfo.put("html", text.html());
            String newsId = NewsMd5.newsMd5(text.text().trim());
            newsInfo.put("newsId", newsId);
            Elements src = document.select("div#zoom p img");
            if (src.size() != 0) {
                for (Element element : src) {
                    if (element.attr("src").contains("http://")) {
                        imgsList.add(element.attr("src"));
                    }
                    if (!element.attr("src").contains("http://")) {
                        imgsList.add("http://www.texindex.com.cn" + element.attr("src"));
                    }

                }
                newsInfo.put("images", imgsList.toString());
            }
            Elements select1 = document.select("td.RightItemBody div");
            for (Element element : select1) {
                if (element.text().contains("编辑：")) {
                    newsInfo.put("author", element.text().split("辑：")[1]);
                }
                if (element.text().contains("点击数")) {
                    newsInfo.put("amountOfReading", element.text().split("点击数 ")[1].replace("( ", "").replace(" )", ""));
                }
            }

            newsInfo.put("crawlerId", "49");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////            esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//            if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                RedisUtil.insertUrlToSet("catchedUrl", url);
//            }
            if (mysqlUtil.insertNews(newsInfo)){
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
