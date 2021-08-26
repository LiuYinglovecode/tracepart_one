package parse.news.download;

import Utils.ForMat;
import Utils.RedisUtil;
import Utils.SleepUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaijxDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaijxDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "机械网");
            Thread.sleep(SleepUtils.sleepMax());
            Document document = Jsoup.parse(html);
            if (html != null) {
                info.put("time", ForMat.getDatetimeFormat(document.select("p.time").text().trim()));
                Elements select = document.select("div#hyzx-p-right div.title");
                select.select("p").remove();
                info.put("title",select.text().trim());
                Elements select2 = document.select("div#hyzx-p-right div.main");
                info.put("text", select2.text().trim());
                info.put("html", select2.html());
                String newsId = MD5Util.getMD5String(select2.text().trim());
                info.put("newsId",newsId);
                String regEx = "责任编辑：[\u4e00-\u9fa5]*";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(select2.text());
                boolean rs = matcher.find();
                if (rs == true) {
                    String surce = matcher.group(0).split("：", 2)[1].replace("推荐","");
                    info.put("source", surce);
                }
                Elements img = select2.select("div#FrameContent p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                    }
                    info.put("images", imgsList.toString());//图片
                }

                info.put("url", url);
                info.put("crawlerId", "78");
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
                LOGGER.info("页面不存在！");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
