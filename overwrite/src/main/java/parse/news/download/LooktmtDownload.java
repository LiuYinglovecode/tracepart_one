package parse.news.download;

import Utils.*;
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

public class LooktmtDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(LooktmtDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        LooktmtDownload looktmtDownload = new LooktmtDownload();
        looktmtDownload.newsInfo("http://www.looktmt.com/0092/329/15600820437.html");
    }

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "looktmt");
            Thread.sleep(SleepUtils.sleepMin());

            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements title = document.select("div.hl_c_title > h2");
                title.select("i").remove();
                newsInfo.put("title", title.text().trim());//标题

                newsInfo.put("time", ForMat.getDatetimeFormat(document.select("span.news_time").text().trim()));//发布时间

                Elements text = document.select("div.summary,div.hl_body");
                newsInfo.put("text", text.text().trim());//新闻内容
                newsInfo.put("html", text.html());//新闻内容
                String newsId = NewsMd5.newsMd5(text.text().trim());
                newsInfo.put("newsId", newsId);
                Elements img = document.select("p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                }
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "157");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(newsInfo)){
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
