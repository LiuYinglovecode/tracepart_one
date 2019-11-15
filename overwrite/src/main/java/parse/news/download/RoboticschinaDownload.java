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

public class RoboticschinaDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoboticschinaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String link = new String("https://www.roboticschina.com");

    public static void main(String[] args) {
        RoboticschinaDownload roboticschinaDownload = new RoboticschinaDownload();
        roboticschinaDownload.newsInfo("https://www.roboticschina.com/news/2018062210dragon.html");
    }

    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "roboticschina");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                String newsId = null;
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.profile > h1").text().trim());
                Elements time = document.select("div.author_date > span.fl");
                if (!html.isEmpty()) {
                    info.put("time", ForMat.getDatetimeFormat(time.text().trim()));
                }
                Elements source = document.select("div.author_date > span");
                if (!html.isEmpty()) {
                    for (Element element : source) {
                        if (element.text().contains("作者："))
                        info.put("source", element.text().replace("作者：","").trim());
                    }
                }


                /**
                 * 文本信息
                 */
                Elements text = document.select("div.articnt");
                if (!text.isEmpty()) {
                    text.select("p.pageLink").remove();
                    text.select("script").remove();
                    text.select("div.relative-tag").remove();
                    text.select("div.note").remove();
                    text.select("div.cl").remove();
                    text.select("div.diggtop").remove();
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = text.select("p > img,img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(link.concat(e.attr("src")));

                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "137");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    System.out.println(info);
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                    if (mysqlUtil.insertNews(info)) {
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                } else {
                    LOGGER.info("detail null");
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
