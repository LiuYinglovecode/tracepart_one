package parse.news.download;

import Utils.ForMat;
import Utils.NewsMd5;
import Utils.RedisUtil;
import Utils.SleepUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import Utils.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SooTooDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(SooTooDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "sootoo");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                String newsId = null;
                Document document = Jsoup.parse(html);
                info.put("title", document.select("#post-705312 > div > div.entry-head > h1").text().trim());
                Elements plate = document.select("#post-705312 > div > div.entry-head > div > span");
                if (!html.isEmpty()){
                    info.put("time", ForMat.getDatetimeFormat(plate.eq(1).text().trim()));
                    info.put("source",plate.eq(3).text().trim());
                }


                /**
                 * 文本信息
                 */
                Elements text = document.select("div.entry-content.clearfix");
                if (!text.isEmpty()) {
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = text.select("div > figure > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));

                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "129");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    System.out.println(info);
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
