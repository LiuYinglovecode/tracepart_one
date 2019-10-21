package parse.news.download;

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
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FamensDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(FamensDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "资讯");
            if (html != null) {
                Document document = Jsoup.parse(html);
                info.put("title",document.select("div.art-con > h1").text().trim());
                Elements select1 = document.select("div.info span");
                for (Element element : select1) {
                    if (element.text().contains("作者：")){
                        info.put("author",element.text().replace("作者：",""));
                    } else  if (element.text().contains("年")){
                        info.put("time",element.text().trim());
                    } else  if (element.text().contains("来源：")){
                        info.put("source",element.text().trim().replace("来源：",""));
                    }
                }


                Elements text = document.select("div#FrameContent");
                info.put("text", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId",newsId);
                Elements img = text.select("div#FrameContent p font img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        info.put("images", imgsList.toString());//图片
                    }
                }else {
                    Elements img1 = text.select("div#FrameContent p img");
                    for (Element element : img1) {
                        imgsList.add(element.attr("src"));
                        info.put("images", imgsList.toString());//图片
                    }
                }
                info.put("url", url);
                info.put("crawlerId", "85");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
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
