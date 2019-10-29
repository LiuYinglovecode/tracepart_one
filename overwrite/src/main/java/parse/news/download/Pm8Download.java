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

public class Pm8Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pm8Download.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "制药设备网");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                info.put("title", parse.select("td.post > table:nth-child(1) > tbody > tr > td > h1").text().trim());
                Elements select = parse.select("td.post > table:nth-child(2) > tbody > tr > td");
                if (select.text().contains("更新时间：")) {
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("更新时间：")[1].trim()));
                    info.put("source", select.text().split("更新时间：")[0].replace("来源：","").trim());
                }

                Element select1 = parse.select("td.post > table > tbody > tr > td > span").first();
                info.put("text", select1.text().trim());
                info.put("html", select1.html());
                String newsId = NewsMd5.newsMd5(select1.text().trim());
                info.put("newsId",newsId);
                Elements images = parse.select("p > img");
                for (Element image : images) {
                    if (image != null) {
                        if (!image.attr("src").contains("http")) {
                            String src = "http://www.pm8.cn/news/" + image.attr("src");
                            imgs.add(src);
                        } else {
                            String src = image.attr("src");
                            imgs.add(src);
                        }
                        info.put("images", imgs.toString());
                    }
                }

                info.put("crawlerId", "91");
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
