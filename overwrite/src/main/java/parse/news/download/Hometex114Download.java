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
import Utils.HttpUtil;
import util.mysqlUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Hometex114Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hometex114Download.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "hometex114");
            if (html != null) {
                Document document = Jsoup.parse(html);
//                System.out.println(document);
                info.put("title", document.select("div > h1").text().trim());
                String time = document.select("#main > div > div > div[style=padding:20px; text-align:center]").text();
                if (time.contains("作者：")){
//                    　作者：Milou Ket　文　倪海洋 译　2009-3-10
//                    info.put("author",time.split("　")[0].replace("作者：",""));
                    info.put("author",time.split("　2")[0].replace("作者：","").trim());
                    info.put("time", ForMat.getDatetimeFormat("2"+(time.split("　2")[1]).trim()));
                }else {
                    info.put("time",ForMat.getDatetimeFormat(time.replace(" ","").trim()));
                }

                Elements textInfo = document.select(".Content");
                info.put("text", textInfo.text().trim());
                info.put("html", textInfo.html());
                String newsId = NewsMd5.newsMd5(textInfo.text().trim());
                info.put("newsId",newsId);
                Elements imgs = textInfo.select("p img");
                if (imgs.size() != 0) {
                    for (Element element : imgs) {
                        imgsList.add(element.attr("src"));
                    }
                    info.put("images", imgsList.toString());//图片
                }
                info.put("url", url);
                info.put("crawlerId", "88");
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
