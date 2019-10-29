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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FengjDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(FengjDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        FengjDownload FengjDownload = new FengjDownload();
        FengjDownload.newsInfo("http://china.fengj.com/html/news/1801/show_1801201.html");
    }

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "fengj");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title",document.select("div.main_le > span.title").text().trim());
                Elements select = document.select("div.from > span");
                for (Element element : select) {
                    if (element.text().contains("来源：")){
                        info.put("source",element.text().replace("来源：","").trim());
                    }else if (element.text().contains("发布时间：")){
                    info.put("time", ForMat.getDatetimeFormat(element.text().replace("发布时间：","").trim()));
                    }
                }


                Elements text = document.select(".content");
                text.select("div").remove();
                info.put("text",text.text().trim());
                info.put("html",text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId",newsId);
                Elements imgList = document.select("p img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }

                info.put("url", url);
                info.put("crawlerId", "89");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
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
