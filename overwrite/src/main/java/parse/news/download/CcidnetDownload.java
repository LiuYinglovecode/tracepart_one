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
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CcidnetDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CcidnetDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        String newsId = null;
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ccidnet");
            Document document = Jsoup.parse(html);

            newsInfo.put("title", document.select("div.nr_cont1.F_Left > h2").text().trim());


            Elements select = document.select("div.tittle_j");
            if (0 != select.size() && select.text().contains("发布时间：") && select.text().contains("来源：") && select.text().contains("作者：")) {
                newsInfo.put("time", ForMat.getDatetimeFormat(select.text().split("来源：")[0].replace("发布时间：", "").trim()));
                newsInfo.put("source", select.text().split("来源：")[1].split("作者：")[0].trim());
                newsInfo.put("author", select.text().split("作者：")[1].trim());
            }

            Elements text = document.select("div.main_content");
            if (text != null) {
                newsInfo.put("text", text.text().trim());
                newsInfo.put("html", text.html());
                newsId = MD5Util.getMD5String(text.text().trim());
            }

            Elements pImg = text.select("p img");
            if (0 != pImg.size()) {
                for (Element el : pImg) {
                    imgsList.add(el.attr("src"));
                }
                newsInfo.put("images", imgsList.toString());

            }

            newsInfo.put("newsId", newsId);
            newsInfo.put("crawlerId", "112");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
//            if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)) {
//                RedisUtil.insertUrlToSet("catchedUrl", url);
//            }
//            if (mysqlUtil.insertNews(newsInfo, "crawler_news", newsId)){
//                RedisUtil.insertUrlToSet("catchedUrl", url);
//            }
            if (mysqlUtil.insertCompany(newsInfo)){
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
