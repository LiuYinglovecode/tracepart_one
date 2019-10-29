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
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OfWeekDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfWeekDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        try {
            JSONObject info = new JSONObject();
            JSONArray imgs = new JSONArray();

//            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
//            httpUtils.setTimeout(30000);
//            httpUtils.setWaitForBackgroundJavaScript(30000);
//            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document document = Jsoup.parse(html);
            info.put("title", document.select("div.artical > p").text().trim());
            info.put("time", ForMat.getDatetimeFormat(document.select("div.artical-relative.clearfix > div").text().trim()));
            String trim = document.select("a.ofweek_artical_tag_source").text().trim();
            if (trim.contains("来源：")) {
                info.put("source", trim.replace("来源：", "").trim());
            }

            String newsId = null;
            Elements text = document.select("#articleC");
            if (!text.isEmpty()) {
                if (text.text().contains("＞＞＞点击投票＜＜＜")){
                    text.select("p").last().remove();
                }
                info.put("text", text.text().trim());
                info.put("html", text.html());
                newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                String regEx = "来源：[\u4e00-\u9fa5]*";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(text.text().trim());
                boolean rs = matcher.find();
                if (rs == true) {
                    String surce = matcher.group(0).replace("来源：", "");
                    info.put("source", surce);
                }

                Elements imgList = text.select("p > img");
                if (!imgList.isEmpty()) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片
                }
            }
            info.put("url", url);
            info.put("crawlerId", "120");
            info.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            info.put("@timestamp", timestamp2.format(new Date()));
            info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
            if (mysqlUtil.insertNews(info)) {
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
