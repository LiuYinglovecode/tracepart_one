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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CntmaDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CntmaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String baseUrl = "http://www.cntma.com";

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cntma");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title",document.select("tr:nth-child(1) > td > h1").first().text().trim());
//                info.put("author",document.select("td.aut").first().text().replace("本文作者：",""));
                info.put("time", ForMat.getDatetimeFormat(document.select("tr:nth-child(2) > td").text().split(" ")[0]));
                Elements select = document.select("td:nth-child(1) > table > tbody > tr:nth-child(4) > td");
                if (select.text().contains("编辑：")){
                    info.put("author",select.text().split("编辑：")[1].trim());
                    info.put("source",select.text().split("编辑：")[0]
                            .replace("转载本网专稿请注明 ","")
                            .replace("“","").replace("”","")
                            .trim());
                }

                Element text = document.select("#zoom").first();
                info.put("text",text.text().trim());
                info.put("html",text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId",newsId);
                Elements imgList = text.select("p img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(baseUrl+e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }
                info.put("url", url);
                info.put("crawlerId", "93");
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
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
