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

public class JdzjDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdzjDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jiancai");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("div.midbox1 h1").text().trim();
                info.put("title", title);
                Elements select = parse.select("div.midbox1 > p");
                if (select.text().contains("来源:")) {
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("来源")[0].replace("发布日期", "")));
                    info.put("source", select.text().split("来源")[1].split(":")[1]);
                }
                Elements text = parse.select("div.midboxcont");
                info.put("text", text.html());
                String newsId = NewsMd5.newsMd5(text.text());
                info.put("newId", newsId);
                Elements images = parse.select("div.midboxcont p img");
                for (Element image : images) {
                    if (!image.attr("src").contains("http://")) {
                        String src = "http:" + image.attr("src");
                        imgs.add(src);
                    } else {
                        String src = image.attr("src");
                        imgs.add(src);
                    }
                    info.put("images", imgs.toString());
                }
                info.put("crawlerId", "69");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
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
