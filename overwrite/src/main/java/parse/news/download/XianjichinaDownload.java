package parse.news.download;

import Utils.NewsMd5;
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
import java.util.*;

/**
 * @author liyujie
 * https://www.xianjichina.com/news
 */
public class XianjichinaDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(XianjichinaDownload.class);
    private static String baseUrl = "https://www.xianjichina.com";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "贤集网");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                if (1 == (document.select(".newl-left").size())) {
                    String title = document.select(".list-main h1").text().trim();
                    String source = document.select(".public-time").text().trim().split("文章来源：", 2)[1].split("发布时间：", 2)[0];
                    String time = document.select(".public-time").text().trim().split("发布时间：", 2)[1];
                    String text = document.select(".main-text").text().trim();
                    String newsId = NewsMd5.newsMd5(text);
                    Elements imgList = document.select(".main-text img");
                    if (!"0".equals(String.valueOf(imgList.size()))) {
                        for (Element e : imgList) {
                            imgs.add(baseUrl + e.attr("src"));
                        }
                    }
                    info.put("images", String.valueOf(imgs));
                    info.put("url", url);
                    info.put("text", text);
                    info.put("newsId", newsId);
                    info.put("source", source);
                    info.put("time", time);
                    info.put("title", title);
                    info.put("crawlerId", "29");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", newsId);
                    esUtil.writeToES(info, "crawler-news-", "doc");
                }
                if (1 == (document.select(".newconleft-top").size())) {
                    String title = document.select(".newconleft-top h1").text().trim();
                    String source = document.select(".public-time").text().trim().split("来源：", 2)[1];
                    String time = document.select(".public-time").text().trim().split("来源：", 2)[0];
                    String text = document.select(".newcon-list").text().trim();
                    String newsId = NewsMd5.newsMd5(text);
                    Elements imgList = document.select(".newcon-list img");
                    if (!"0".equals(String.valueOf(imgList.size()))) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));
                        }
                    }
                    info.put("images", imgs);
                    info.put("url", url);
                    info.put("text", text);
                    info.put("newsId", newsId);
                    info.put("source", source);
                    info.put("time", time);
                    info.put("title", title);
                    info.put("crawlerId", "29");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", newsId);
                    esUtil.writeToES(info, "crawler-news-", "doc");
                }
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
