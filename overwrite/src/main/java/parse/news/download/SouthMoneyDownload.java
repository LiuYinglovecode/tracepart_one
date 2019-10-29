package parse.news.download;

import Utils.ForMat;
import Utils.NewsMd5;
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
import util.mysqlUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SouthMoneyDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(SouthMoneyDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "southmoney");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                String newsId = null;
                Document document = Jsoup.parse(html);
                info.put("title", document.select("h1.artTitle").text().trim());
                String plate = document.select("p.artDate").text();
                if (!plate.isEmpty()){
                    String regEx = "[\u4e00-\u9fa5]*";
                    Pattern compile = Pattern.compile(regEx);
                    Matcher matcher = compile.matcher(plate);
                    String time = matcher.replaceAll("").trim();
                    info.put("time", ForMat.getDatetimeFormat(time.trim()));
                    info.put("source",plate.replace(time,"").trim());
                }


                /**
                 * 文本信息
                 */
                Elements text = document.select("div.articleCon");
                if (!text.isEmpty()) {
                    text.select("div").remove();
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);


                    info.put("url", url);
                    info.put("crawlerId", "130");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    System.out.println(info);
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                    if (mysqlUtil.insertNews(info)) {
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                } else {
                    LOGGER.info("detail null");
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
