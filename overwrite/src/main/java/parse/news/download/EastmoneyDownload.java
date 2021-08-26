package parse.news.download;

import Utils.ForMat;
import Utils.HttpUtil;
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
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EastmoneyDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(EastmoneyDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        EastmoneyDownload eastmoneyDownload = new EastmoneyDownload();
        eastmoneyDownload.detail("http://finance.eastmoney.com/a/201912091317078286.html");
    }
    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "eastmoney");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
//                System.out.println(document);
                info.put("title",document.select("div.newsContent > h1").text().trim());
                info.put("time", ForMat.getDatetimeFormat(document.select("div.time").text().trim()));
                Elements select = document.select("div.source.data-source");
                if (!select.isEmpty()) {
                    String s = select.text().replace("来源：", "").trim();
                    info.put("source", s);
                }
                Elements text = document.select("#ContentBody");
                text.select("div.reading").remove();
                info.put("text",text.text().trim());
                info.put("html",text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId",newsId);;
                Elements imgList = text.select("center img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }

                info.put("url", url);
                info.put("crawlerId", "159");
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
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
