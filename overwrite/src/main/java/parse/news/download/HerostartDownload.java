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

public class HerostartDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(HerostartDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "herostart");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("h1#title.title").text().trim());
                String select = document.select("div.info").text();
                if (select.contains("发布日期：") && select.contains("来源：") && select.contains("作者：") && select.contains("浏览次数：")) {
//                    发布日期：2019-06-26  来源：潍坊浩宇环保设备有限公司  作者：王世刚17861220657  浏览次数：1
                    info.put("time", select.split("来源：")[0].replace("发布日期：", ""));
                    info.put("source", select.split("来源：")[1].split("作者：")[0]);
                    info.put("author", select.split("作者：")[1].split("浏览次数：")[0]);
                    info.put("amountOfReading", select.split("浏览次数：")[1]);
                } else if (select.contains("发布日期：") && select.contains("作者：") && select.contains("浏览次数：")) {
//                    发布日期：2008-10-04  作者：王世刚17861220657 浏览次数：22
                    info.put("time", select.split("作者：")[0].replace("发布日期：", ""));
                    info.put("author", select.split("作者：")[1].split("浏览次数：")[0]);
                    info.put("amountOfReading", select.split("浏览次数：")[1]);
                } else if (select.contains("发布日期：") && select.contains("浏览次数：")) {
//                    发布日期：2008-10-04  浏览次数：22
                    info.put("author", select.split("浏览次数：")[0].replace("发布日期：", ""));
                    info.put("amountOfReading", select.split("浏览次数：")[1]);
                }
                Elements text = document.select("div#article.content");
                if (text.size() != 0) {
                    info.put("text", text.text().trim());
                    String newsId = MD5Util.getMD5String(text.text().trim());
                    info.put("newsId", newsId);
                    Elements imgList = text.select("p > img");
                    if (imgList.size() != 0) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));
                        }
                        info.put("images", imgs.toString());//图片
                    } else {
                        Elements img = text.select("div img");
                        for (Element e : img) {
                            imgs.add(e.attr("src"));
                        }
                        info.put("images", imgs.toString());//图片
                    }
                    info.put("url", url);
                    info.put("crawlerId", "75");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", newsId);
//                    esUtil.writeToES(info, "crawler-news-", "doc", newsId);
                    if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                }
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
