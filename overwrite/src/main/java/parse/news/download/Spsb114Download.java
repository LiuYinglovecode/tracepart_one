package parse.news.download;

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
import util.HttpUtil;
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


    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "食品设备网");
            Document document = Jsoup.parse(html);
            if (html != null) {
                info.put("title",document.select("td.post table tbody tr td h1").text().trim());
                String select = document.select(".post > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1)").text();
                if (select.contains("来源：") && select.contains("更新时间：")){
                    info.put("source",select.split("更新时间：")[0].replace("来源：",""));
                    info.put("time",select.split("更新时间：")[1]);
                }
                if (select.contains("更新时间：")){
                    info.put("time",select.replace("更新时间：",""));
                }

                Elements select2 = document.select("td.f16.news_link");
                String text = select2.text();
                info.put("text", text);
                String newsId = MD5Util.getMD5String(text);
                info.put("newsId",newsId);
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
                mysqlUtil.insertNews(info, "crawler_news", newsId);
                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }

            } else {
                LOGGER.info("页面不存在！");
            }



        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
