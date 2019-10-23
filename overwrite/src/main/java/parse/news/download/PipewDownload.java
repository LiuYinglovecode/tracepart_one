package parse.news.download;

import Utils.*;
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

public class PipewDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipewDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        try {
            JSONObject info = new JSONObject();
            JSONArray imgs = new JSONArray();

            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);


            info.put("title", document.select("#ttitle").text().trim());
            Elements select = document.select("#timefont");
            if (!select.isEmpty()) {
                if (select.text().contains("来源:")) {
                    info.put("time", ForMat.getDatetimeFormat(select.text().split("来源:")[0].replace("时间:", "").split("【字号：")[0].trim()));
                    info.put("source", select.text().split("来源:")[1].split("【字号：")[0].trim());
                }else {
                    info.put("source", select.select("a[target=_blank]").text().trim());
                    select.select("a").remove();
                    info.put("time", ForMat.getDatetimeFormat(select.text().replace("时间:", "").split("【字号：")[0].trim()));
                }
            }


            Elements text = document.select("#zoom");
            info.put("text", text.html());
            String newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
            info.put("newsId", newsId);
            Elements imgList = document.select("p > img");
            if (!imgList.isEmpty()) {
                for (Element e : imgList) {
                    imgs.add(e.attr("src"));
                }
                info.put("images", imgs.toString());//图片
            }
            info.put("url", url);
            info.put("crawlerId", "119");
            info.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            info.put("@timestamp", timestamp2.format(new Date()));
            info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
            if (mysqlUtil.insertNews(info, "crawler_news", newsId)) {
                RedisUtil.insertUrlToSet("catchedUrl", url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
