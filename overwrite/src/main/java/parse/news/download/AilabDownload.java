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

public class AilabDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CeeiaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        AilabDownload ailabDownload = new AilabDownload();
        ailabDownload.newsInfo("http://blockchain.ailab.cn/article-95420.html");
    }

    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ailab");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {

                String newsId = null;
                Document document = Jsoup.parse(html);
                String select = document.select("div.p").text();
                if (select.contains("来源：")&&select.contains("发布日期：")&&select.contains("浏览：")&&select.contains("值班编辑QQ：")){
                    //来源：互联网   发布日期：2019-11-11 14:08   浏览：224次  值班编辑QQ：281688302
                    info.put("source", select.split("发布日期：")[0].replace("来源：","").trim());
                    info.put("time", ForMat.getDatetimeFormat(select.split("发布日期：")[1].split("浏览：")[0].trim()));
                    info.put("amountOfReading", ForMat.getDatetimeFormat(select.split("浏览：")[1].split("值班编辑QQ：")[0].trim()));
                }

                Elements elements = document.select("h1.h1");
                if (!elements.isEmpty()) {
                    elements.select("div.p").remove();
                    info.put("title", document.select("h1.h1").text().trim());
                }



                /**
                 * 文本信息
                 */
                Elements text = document.select("#mainDiv");
                if (!text.isEmpty()) {
                    text.select("div").remove();
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = text.select("p > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("data-original"));
                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "142");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    System.out.println(info);
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
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
