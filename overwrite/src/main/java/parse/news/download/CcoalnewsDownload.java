package parse.news.download;

import Utils.ForMat;
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
import Utils.HttpUtil;
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

    public void detailDownload(String url) {
        try {
            JSONObject info = new JSONObject();
            JSONArray img = new JSONArray();
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国煤炭网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                info.put("title", document.select(".text-article h1").text().trim());
                info.put("time", ForMat.getDatetimeFormat(document.select(".date").text().trim()));
                info.put("author", document.select(".author").text().replace("作者：","").trim());
                Elements text = document.select(".content");
                text.select("div").remove();
                info.put("text", text.text().trim());
                info.put("html", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                Elements imgList = document.select(".content img");
                if (!imgList.isEmpty()){
                    for (Element e : imgList) {
                        img.add(e.attr("src"));
                    }
                    info.put("images", img.toString());
                }

                info.put("url",url);
                info.put("crawlerId", "61");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                            mysqlUtil.insertNews(info, "crawler_news", newsId);
////                            esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                            if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                                RedisUtil.insertUrlToSet("catchedUrl", url);
//                            }
//                if (mysqlUtil.insertNews(info, "crawler_news", newsId)) {
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}

