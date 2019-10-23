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
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ElecfansDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElecfansDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "elecfans");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
//                System.out.println(document);
                info.put("title",document.select("h1.article-title").text().trim());
                info.put("time", ForMat.getDatetimeFormat(document.select("div.article-info.art-share-layout.m-share-layout.clearfix span.time").text().trim()));
                info.put("amountOfReading",document.select("div.article-info.art-share-layout.m-share-layout.clearfix span.count span.art_click_count").text().trim());
                Elements text = document.select("div.article-content.simditor.clearfix div.simditor-body.clearfix");
                text.select("span.count span.art_click_count").remove();
                info.put("text",text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId",newsId);;
                Elements imgList = text.select("div.simditor-body.clearfix p img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }

                info.put("url", url);
                info.put("crawlerId", "76");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }

            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
