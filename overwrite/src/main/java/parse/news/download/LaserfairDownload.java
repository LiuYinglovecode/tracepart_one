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

public class LaserfairDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaserfairDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final String baseUrl = new String("https://www.ledinside.cn");
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        LaserfairDownload laserfairDownload = new LaserfairDownload();
        laserfairDownload.newsInfo("http://www.laserfair.com/fangtan/201211/26/180.html");
    }
    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "laserfair");
            if (!html.isEmpty()) {
                String newsId = null;
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.news-details.mt15 > h3").text().trim());
                Elements select = document.select("div.news-details-info > div > p");
                if (!select.isEmpty() && select.text().contains("来源：")&&select.text().contains("关键词")&&select.text().contains("发布时间")) {
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("发布时间：")[1].trim()));
                    info.put("source", select.text().split("关键词：")[0].replace("来源：","").trim());
                }


                /**
                 * 文本信息
                 */
                Elements t = document.select("#arti-main");
                if (!t.isEmpty()) {
                    String html1 = t.html();
                    info.put("html", html1);
                    String text = t.text().trim();
                    info.put("text", text);
                    newsId = NewsMd5.newsMd5(text.replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = t.select("p > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));
                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "174");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
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
