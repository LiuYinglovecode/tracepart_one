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
import Utils.RedisUtil;
import util.ESUtil;
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CcoalnewsDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CcoalnewsDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public void detailDownload() {
        try {
            while (RedisUtil.getCountFromKey("toCatchUrl") > 0) {
                try {
                    String url = RedisUtil.getUrlFromeSet("toCatchUrl");
                    if (null != url) {
                        JSONObject info = new JSONObject();
                        JSONArray img = new JSONArray();
                        String html = HttpUtil.httpGetwithJudgeWord(url, "中国煤炭网");
                        if (null != html) {
                            Document document = Jsoup.parse(html);
                            info.put("title", document.select(".text-article h1").text().trim());
                            info.put("time", document.select(".date").text().trim());
                            info.put("author", document.select(".author").text().trim());
                            String text = document.select(".content").text().trim();
                            info.put("text", text);
                            String newsId = NewsMd5.newsMd5(text);
                            info.put("newsId", newsId);
                            Elements imgList = document.select(".content img");
                            for (Element e : imgList) {
                                img.add(e.attr("src"));
                            }
                            info.put("images", img.toString());
                            info.put("crawlerId", "61");
                            info.put("timestamp", timestamp.format(new Date()));
                            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                            info.put("@timestamp", timestamp2.format(new Date()));
                            info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                            mysqlUtil.insertNews(info, "crawler_news", newsId);
                            esUtil.writeToES(info, "crawler-news-", "doc");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}

