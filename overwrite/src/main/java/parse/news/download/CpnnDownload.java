package parse.news.download;

import Utils.NewsMd5;
import Utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CpnnDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpnnDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    /*
新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
        把链接放到集合中，在进行存储。
 */
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cpnn");
            if (html != null) {
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements href = document.select("div.cpnn-minnav a");
                if (href.attr("href").equals("../")) {
                    newsInfo.put("plate", href.text().trim());
                }
                if (href.attr("href").equals("./")) {
                    newsInfo.put("plate", href.text().trim());
                }

                newsInfo.put("title", document.select("div.cpnn-con-title h1").text().trim());//标题
                String select = document.select("div.cpnn-zhengwen-time p").text();
                newsInfo.put("time", select.split("日")[1].split("：")[1]);
                newsInfo.put("source", select.split("日期")[0].split("：")[1]);
                Elements font = document.select("font span font");
                for (Element element : font) {
                    if (element.text().contains("责任编辑：")) {
                        newsInfo.put("author", element.text().trim().split("：")[1]);
                    }
                }
                Elements img = document.select("div.Custom_UnionStyle img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片
                    }
                }
                Elements text = document.select("div.cpnn-con-zhenwen");//新闻内容
                if (text.size() != 0) {
                    if (text.select("div.cpnn-con-zhenwen div p b").text().contains("【稿件声明】")) {
                        text.select("div.cpnn-con-zhenwen div p b").remove();
                        String text1 = text.text().replace("附件：", "");
                        newsInfo.put("text", text1);
                        String newsId = NewsMd5.newsMd5(text1);
                        newsInfo.put("newsId", newsId);
                        newsInfo.put("crawlerId", "58");
                        newsInfo.put("timestamp", timestamp.format(new Date()));
                        timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                        newsInfo.put("@timestamp", timestamp2.format(new Date()));
                        newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                        mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
//                        esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
                        if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
                            RedisUtil.insertUrlToSet("catchedUrl", url);
                        }
                    }
                }
            } else {
                LOGGER.info("页面不存在！");
            }
//            newsInfo.put("plate",plate);//板块

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
