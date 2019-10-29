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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FzfzjxDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(EapadDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {

        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        try {
            Thread.sleep(1000);
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (html != null) {
                Document document = Jsoup.parse(html);
                info.put("title",document.select("div.caption p").text().trim());
                Elements element = document.select("div.newstime > dl > dt");
                if (element.text().contains("纺织服装机械网")){
                    info.put("time", ForMat.getDatetimeFormat(element.text().split("纺织服装机械网")[0].trim()));
                    info.put("amountOfReading", element.text().split("纺织服装机械网")[1].replace("点击","").trim());
                }
                Elements textInfo = document.select(".newshow_fontshow");
                info.put("text", textInfo.text().trim());
                info.put("html", textInfo.html());
                String newsId = NewsMd5.newsMd5(textInfo.text().trim());
                info.put("newsId",newsId);
                info.put("source",textInfo.select("p.ly").text().replace("来源：","").replace("(","").replace(")",""));
                info.put("url", url);
                info.put("crawlerId", "90");
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
