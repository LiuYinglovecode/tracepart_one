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

public class CinnDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CinnDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        CinnDownload cinnDownload = new CinnDownload();
        cinnDownload.detail("http://www.cinn.cn/headline/201910/t20191020_219835.html");
    }

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cinn");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                String plate = document.select(".index a:last-child").text().trim();
                info.put("url", url);
                info.put("plate", plate.trim());
                String title = document.select(".detail_title").text().trim();
                info.put("title", title);
                Elements source_time_list = document.select(".detail_abs.bdsharebuttonbox span");
                for (Element e : source_time_list) {
                    if (e.text().contains("文章来源")) {
                        String source = e.text().split("文章来源 :", 2)[1].trim();
                        info.put("source", source);
                    }
                    if (e.text().contains("发布时间")) {
                        String time = e.text().split("发布时间 ：", 2)[1].trim();
                        info.put("time", ForMat.getDatetimeFormat(time));
                    }
                }
                Elements imgList = document.select(".TRS_Editor img");
                if (!"0".equals(String.valueOf(imgList.size()))) {
                    for (Element e : imgList) {
                        if (e.attr("src").contains("http")) {
                            imgs.add(e.attr("src"));
                        } else if (e.attr("src").contains("./")) {
                            imgs.add(url.substring(0, url.lastIndexOf("/")) + e.attr("src").split("/", 2)[1]);
                        }
                    }
                    info.put("images", imgs.toString());
                }

                Elements text = document.select(".detail_content");
                info.put("text", text.text().trim());
                info.put("html", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                info.put("crawlerId", "28");
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
