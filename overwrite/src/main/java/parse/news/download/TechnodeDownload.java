package parse.news.download;

import Utils.ForMat;
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
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TechnodeDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(TechnodeDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        String newsId = null;
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "相关站点");
            Document document = Jsoup.parse(html);
            newsInfo.put("title", document.select("h1.header-title.fontsize-155944.font-weight-700 > span").text().trim());
            newsInfo.put("time", ForMat.getDatetimeFormat(document.select("div.date-info").text().trim()));
            newsInfo.put("author", document.select("div.author-info > a").text().trim());

            Elements text = document.select("div.post-content.style-light.double-bottom-padding");
            if (0 != text.size()) {
                text.select("div.widget-container.post-tag-container.uncont.text-left").remove();
                newsInfo.put("text", text.text().trim());
                newsInfo.put("html", text.html());
                newsId = MD5Util.getMD5String(text.text().trim());
                Elements img = text.select("p img");
                if (img.size()!=0){
                    for (Element element : img) {
                        String src = element.attr("src");
                        imgsList.add(src);
                    }
                    newsInfo.put("images",imgsList.toString());
                }
            }
            newsInfo.put("newsId",newsId);
            newsInfo.put("crawlerId", "110");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
//            if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)) {
//                RedisUtil.insertUrlToSet("catchedUrl", url);
//            }
            if (mysqlUtil.insertNews(newsInfo)){
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
