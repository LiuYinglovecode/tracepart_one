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
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TybabaDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(TybabaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "tybaba");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.hd > h1").text().trim());
                Elements select = document.select("div.hd > div.titBar");
                if (select.text().contains("发布日期：")&&select.text().contains("我来投稿")&&select.text().contains("浏览次数：")) {
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("我来投稿")[0].replace("发布日期：","").trim()));
                    info.put("amountOfReading", select.text().split("我来投稿")[1].replace("浏览次数：", "").trim());
                    info.put("source", "金属制品网");
                }
                if (select.text().contains("发布日期：")&&select.text().contains("来源：")&&select.text().contains("作者：")&&select.text().contains("我来投稿")&&select.text().contains("浏览次数：")){
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("来源：")[0].replace("发布日期：","").trim()));
                    info.put("amountOfReading", select.text().split("浏览次数：")[1].trim());
                    info.put("source", select.text().split("来源：")[1].split("作者：")[0].trim());
                    info.put("author", select.text().split("作者：")[1].split("我来投稿")[0].trim());
                }
                if (select.text().contains("发布日期：")&&select.text().contains("来源：")&&select.text().contains("我来投稿")&&select.text().contains("浏览次数：")){
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("来源：")[0].replace("发布日期：","").trim()));
                    info.put("source", select.text().split("来源：")[1].split("我来投稿")[0].trim());
                    info.put("amountOfReading", select.text().split("浏览次数：")[1].trim());
                }
                if (select.text().contains("发布日期：")&&select.text().contains("作者：")&&select.text().contains("我来投稿")&&select.text().contains("浏览次数：")){
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("作者：")[0].replace("发布日期：","").trim()));
                    info.put("author", select.text().split("作者：")[1].split("我来投稿")[0].trim());
                    info.put("amountOfReading", select.text().split("浏览次数：")[1].trim());
                }



                Elements text = document.select("#article");
                info.put("text", text.html());
                String newsId = NewsMd5.newsMd5(text.text().replace("", "").trim());
                info.put("newsId", newsId);
                Elements imgList = document.select("p > img");
                if (!imgList.isEmpty()) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片
                }
                info.put("url", url);
                info.put("crawlerId", "117");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
