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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Kq81Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(Kq81Download.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        Kq81Download kq81Download = new Kq81Download();
        kq81Download.newsInfo("https://www.kq81.com/AspCode/NewShow.asp?ArticleId=380333");
    }

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "kq81");
            if (!html.isEmpty()) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                info.put("title", parse.select("td.tl22 > h2").text().trim());
                Elements elements = parse.select("tr:nth-child(1) > td > div > table > tbody > tr > td.text > div");
//                时间：2019/8/29 8:53:13 来源：澎湃新闻 点击次数:148
                if (elements.text().contains("时间：")&&elements.text().contains("来源：")&&elements.text().contains("点击次数:")){
                    info.put("time", ForMat.getDatetimeFormat(elements.text().split("时间：")[1].split("来源：")[0]));
                    info.put("source", elements.text().split("来源：")[1].split("点击次数:")[0]);
                    info.put("amountOfReading", elements.text().split("来源：")[1].split("点击次数:")[0]);
                }


                Elements text = parse.select("#Content");
                info.put("text", text
                        .text()
                        .replace("中国矿权资源网http://www.kq81.com/全球领先的","")
                        .replace("门户网站","")
                        .trim());
                info.put("html", text
                        .html()
                        .replace("中国矿权资源网http://www.kq81.com/全球领先的","")
                        .replace("门户网站",""));
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                Elements images = text.select("p > img");
                if (!images.isEmpty()) {
                    for (Element image : images) {
                        imgs.add(image.attr("src"));
                    }
                    info.put("images", imgs.toString());
                }



                info.put("crawlerId", "165");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
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
