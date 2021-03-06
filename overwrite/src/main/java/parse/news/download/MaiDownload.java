package parse.news.download;

import Utils.ForMat;
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
import Utils.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MaiDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaiDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("#title").text().trim();
                info.put("title", title);
                Elements select = parse.select("div.info");
                if (select.size() != 0) {
                    if (!select.text().contains("来源：")) {
                        info.put("time", ForMat.getDatetimeFormat(select.text().trim().split("浏览次数：")[0].split("：")[1].trim()));
                        info.put("amountOfReading", select.select("#hits").text().trim());
                    } else {
                        info.put("time", ForMat.getDatetimeFormat(select.text().split("来源：")[0].split("：")[1].trim()));
                        info.put("source", select.text().split("来源：")[1].split("作者：")[0].trim());
                        info.put("author", select.text().split("来源：")[1].split("作者：")[1].split(" 浏览次数")[0].trim());
                        info.put("amountOfReading", select.select("#hits").text().trim());
                    }
                }
                Elements text = parse.select("#article");
                info.put("text", text.text().trim());
                info.put("html", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newId", newsId);
                Elements images = parse.select("#article > div > p > img");
                if (images.size() != 0) {
                    for (Element image : images) {
                        String src = image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                }

                info.put("crawlerId", "65");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info)){
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
