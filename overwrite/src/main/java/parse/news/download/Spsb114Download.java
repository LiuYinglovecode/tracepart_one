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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Spsb114Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(Spsb114Download.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        Spsb114Download spsb114Download = new Spsb114Download();
        spsb114Download.newsInfo("http://www.spsb114.com/tech/27748.html");

    }

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "食品设备网");
            if (html != null) {
            Document document = Jsoup.parse(html);
                info.put("title",document.select("td.post table tbody tr td h1").text().trim());
                String select = document.select(".post > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)").text();
                if (select.contains("来源：") && select.contains("更新时间：")){
                    info.put("source",select.split("更新时间：")[0].replace("来源：","").trim());
                    info.put("time", ForMat.getDatetimeFormat(select.split("更新时间：")[1].trim()));
                }
                if (select.contains("更新时间：")){
                    info.put("time",ForMat.getDatetimeFormat(select.replace("更新时间：","").trim()));
                }

                Elements select2 = document.select("td.f14.news_link");
                if (!select2.isEmpty()) {
                    info.put("text", select2.text().trim());
                    info.put("html", select2.html());
                    String newsId = NewsMd5.newsMd5(select2.text().trim());
                    info.put("newsId", newsId);
                    Elements img = select2.select("p img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                        }
                        info.put("images", imgsList.toString());//图片
                    }

                    info.put("url", url);
                    info.put("crawlerId", "77");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                    if (mysqlUtil.insertNews(info)) {
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                }
            } else {
                LOGGER.info("页面不存在！");
            }



        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
