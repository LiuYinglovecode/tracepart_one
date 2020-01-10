package parse.news.download;

import Utils.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JingMeiTiDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(JingMeiTiDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ESUtil esUtil = new ESUtil();
    private static String links = "http://www.jsjxmhw.com";


    public static void main(String[] args) {
        JingMeiTiDownload jingMeiTiDownload = new JingMeiTiDownload();
        jingMeiTiDownload.newsInfo("http://www.jingmeiti.com/archives/42172");
    }

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        Date date = new Date();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jingmeiti");
            Thread.sleep(SleepUtils.sleepMin());
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements elements = parse.select("div.post-title > h1");
                if (!elements.isEmpty()) {
                    newsInfo.put("title", elements.text().trim());
                }

                Elements time = elements.select("span.postclock");
                if (!html.isEmpty()) {
                    String trim = time.text().trim();
                     if (trim.contains("分钟前") && trim.contains("小时前")) {
                         newsInfo.put("time", ForMat.getDatetimeFormat(format.format(date)));
                     }else {
                         newsInfo.put("time", ForMat.getDatetimeFormat(trim));
                     }
                }


                Elements select = parse.select("div.post-content");
                if (!select.isEmpty()) {
                    String trim = select.text().trim();
                    String s = NewsMd5.newsMd5(trim);
                    newsInfo.put("text", trim);
                    newsInfo.put("newsId", s);
                    newsInfo.put("html", select.html());

                    Elements pImg = select.select("div > img");
                    if (!pImg.isEmpty()) {
                        String attr = pImg.attr("src");
                        imgsList.add(attr);

                    }
                }

                newsInfo.put("images",imgsList.toString());
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "171");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
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
