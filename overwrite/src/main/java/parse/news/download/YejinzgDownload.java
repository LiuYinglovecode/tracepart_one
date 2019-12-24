package parse.news.download;

import Utils.ForMat;
import Utils.HttpUtil;
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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class YejinzgDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(YejinzgDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        YejinzgDownload yejinzgDownload = new YejinzgDownload();
        yejinzgDownload.detail("http://www.yejinzg.com/show/41_144353_1.html");
    }

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "yejinzg");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title",document.select("div.left > div > h1").text().trim());

                String[] select = document.select("h3.show_info").text().split("\\|");
                for (String s : select) {
                    if (s.contains("时间：")){
                        info.put("time",ForMat.getDatetimeFormat(s.replace("时间：","").trim()));
                    }
                }

                Elements text = document.select(".show_content");
                info.put("text",text.text().trim());
                info.put("html",text.html());
                String newsId = MD5Util.getMD5String(text.text().trim());
                info.put("newsId",newsId);
                Elements imgList = document.select("p img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }

                info.put("url", url);
                info.put("crawlerId", "164");
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
