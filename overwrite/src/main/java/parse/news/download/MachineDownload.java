package parse.news.download;

import Utils.MD5Util;
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
import util.mysqlUtil;

import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MachineDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MachineDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    //    新闻信息
    public void newsinfo(String url) {
        JSONObject newsInfo = new JSONObject();
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            if (get != null) {
                Document gbk = Jsoup.parse(new URL(url).openStream(), "GBK", get);
                Elements plate = gbk.select("span:nth-child(3)");
                if (plate.size() == 0) {
                    newsInfo.put("plate", gbk.select("body > div.yrhere > a:nth-child(2)").text().trim());
                } else {
                    newsInfo.put("plate", plate.text());

                }

                Elements title = gbk.select("div.newliIn_ti");
                if (title.size() == 0) {
                    String title2 = gbk.select("div.left > div > h1").text().trim();
                    newsInfo.put("title", gbk.select("div.left > div > h1").text().trim());
                } else {
                    newsInfo.put("title", title.text());
                }

                Elements time = gbk.select("div.newliIn_Sti");
                if (time.size() == 0) {
                    newsInfo.put("time", gbk.select("div.box1 > h4").text().trim().split("：", 2)[1]);
                } else {
                    newsInfo.put("time", time.text().trim());
                }
                String trim = gbk.select("#ArticleCnt").text().trim();
                newsInfo.put("text", trim);
                String newId = NewsMd5.newsMd5(trim);
                newsInfo.put("newId", newId);
                String text = gbk.select("#ArticleCnt").text();
                String regEx = "来源：[\u4e00-\u9fa5]*";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(text);
                boolean rs = matcher.find();
                if (rs == true) {
                    String surce = matcher.group(0).split("：", 2)[1];
                    newsInfo.put("source", surce);
                }
                newsInfo.put("crawlerId", "32");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(newsInfo, "crawler_news", newId);
//                esUtil.writeToES(newsInfo, "crawler-news-", "doc", newId);
                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            } else {
                LOGGER.info("页面不存在");
            }
        } catch (Exception e) {
            if (e.getClass() != FileNotFoundException.class) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
