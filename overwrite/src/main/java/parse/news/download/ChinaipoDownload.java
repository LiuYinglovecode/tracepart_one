package parse.news.download;

import Utils.ForMat;
import Utils.HttpUtil;
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
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChinaipoDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinaipoDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String links = "http://www.chinaipo.com";

    public static void main(String[] args) {
        ChinaipoDownload chinaipoDownload = new ChinaipoDownload();
        chinaipoDownload.newsInfo("http://www.chinaipo.com/kechuangban/102589.html");
    }

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chinaipo");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String title = document.select("div.crumb > h1").text().trim();
                newsInfo.put("title", title);//标题

                String[] select = document.select("div.crumb > p").text().split("·");
                for (String s : select) {
                    if (s.contains("-")) {
                        newsInfo.put("time", ForMat.getDatetimeFormat(s.trim()));//发布时间
                    } else if (s.contains("浏览")) {
                        newsInfo.put("amountOfReading", s.replace("浏览", "").trim());//来源
                    }
                }
                String newsId = "";
                String s = "";
                Elements text = document.select("div.newscont");
                text.select("p span").remove();
                String trim = text.text().trim();
                if (trim.contains("转载声明：")) {
                    s = trim.split("转载声明：")[0].trim();
                    newsId = NewsMd5.newsMd5(s);
                }else {
                     s = trim.trim();
                    newsId = NewsMd5.newsMd5(s);

                }

                String replace = text.html().replace("转载声明：", "")
                        .replace("本文为资本邦原创稿件，转载需注明出处和作者，否则视为侵权。", "");
                newsInfo.put("html", replace);//新闻内容

                newsInfo.put("newsId", newsId);
                Elements img = document.select("p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(links+element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                }

                newsInfo.put("text", s);//新闻内容
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "160");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
//                if (mysqlUtil.insertNews(newsInfo, "crawler_news", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(newsInfo)) {
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }

            } else {
                LOGGER.info("页面不存在");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
