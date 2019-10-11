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

public class CnmoDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MydriversDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        String newsId = null;
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cnmo");
            Document document = Jsoup.parse(html);
            newsInfo.put("title",document.select("#cleft > div.ctitle > h1").text().trim());
            Elements select = document.select("div.ctitle_spe > div.fl");
            if (0 != select.size()) {
                if (select.select("span.c333").text().contains("原创")) {
                    newsInfo.put("source", "cnmo");
                } else {
                    newsInfo.put("source", select.select("span.c333").text().replace("【", "").replace("】", "").trim());
                }
                newsInfo.put("author",select.select("span.text_auther").text().replace("作者：","").trim());
                newsInfo.put("time",select.select("span").eq(2).text().trim());
            }

            Elements text = document.select(".ctext");
            if (text!=null) {
                text.select("p.copyright").remove();
                if (text.text().contains("】")){
                    newsInfo.put("text", text.text().split("】")[1].trim());
                    newsId = MD5Util.getMD5String(text.text().trim());
                }else {
                    newsInfo.put("text", text.text().trim());
                    newsId = MD5Util.getMD5String(text.text().trim());
                }
                newsInfo.put("newsId", newsId);
                Elements src = text.select("p a img");
                if (src.size() != 0) {
                    for (Element element : src) {
                        imgsList.add("http:" + element.attr("src"));
                    }
                    newsInfo.put("images", imgsList.toString());
                }
            }

            newsInfo.put("crawlerId", "111");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
//            if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)) {
//                RedisUtil.insertUrlToSet("catchedUrl", url);
//            }
            if (mysqlUtil.insertNews(newsInfo, "crawler_news", newsId)){
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
