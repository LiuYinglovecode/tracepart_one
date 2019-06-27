package parse.news.download;

import Utils.MD5Util;
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
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LmjxDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(LmjxDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    /**
     * 新闻信息：解析获取信息，存入数据库及ES
     *
     * @param url
     */
    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "lmjx");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("div > h1:eq(0)").text();
                info.put("title", title);
                Elements time = parse.select("div.pinf.cl span.time");
                if (time.size() != 0) {
                    info.put("time", time.text().trim());
                }
                Elements time1 = parse.select("div.details-timer.left");
                if (time1.size() != 0) {
                    info.put("time", time1.text().trim());
                }
                String select = parse.select("div.contentbox div.info").text().trim();
                if (select.contains("来源：")) {
                    info.put("time", select.split("来源：")[0]);
                    info.put("source", select.split("来源：")[1].replace("，转载请标明出处", ""));
                }
                Elements images = parse.select("div.content p img");
                if (images.size() != 0) {
                    for (Element image : images) {
                        String src = image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                } else {
                    Elements text1 = parse.select("div.pageleft content p img");
                    for (Element image : text1) {
                        String src = image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                }
                Elements text = parse.select("div.content");
                if (text.size() != 0) {
                    String trim = text.text().trim();
                    info.put("text", trim);
                    String newId = MD5Util.getMD5String(trim);
                    info.put("newId", newId);
                    info.put("crawlerId", "67");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", newId);
                    esUtil.writeToES(info, "crawler-news-", "doc", newId);
                } else {
                    Elements text1 = parse.select("div.pageleft content");
                    String trim = text1.text().trim();
                    info.put("text", trim);
                    String newId = NewsMd5.newsMd5(trim);
                    info.put("newId", newId);
                    info.put("crawlerId", "67");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", newId);
                    esUtil.writeToES(info, "crawler-news-", "doc", newId);
                }

            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
