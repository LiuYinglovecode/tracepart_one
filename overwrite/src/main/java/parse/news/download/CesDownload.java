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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CesDownload {

    private static final Logger LOGGER = LoggerFactory.getLogger(CesDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    //新闻内容
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国节能网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select("#title.title").text().trim());//标题
                Elements select = document.select("div.fx_lyu span");
                if (select.size() != 0) {
                    for (Element element : select) {
                        if (element.text().contains("来源")) {
                            newsInfo.put("time", ForMat.getDatetimeFormat(select.text().split("来源")[0].trim()));
                            newsInfo.put("plate", select.text().split("：")[1].replace("0 0", "").trim());
                        }
                    }
                }

                Elements text = document.select("#article.content");//新闻内容
                if (text.size() != 0) {
                    newsInfo.put("text", text.text().trim());
                    newsInfo.put("html", text.html());
                    String newsId = NewsMd5.newsMd5(text.text().trim());
                    newsInfo.put("newsId", newsId);
                    Elements img = text.select("div img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                            newsInfo.put("images", imgsList.toString());//图片
                        }
                    }

                    newsInfo.put("crawlerId", "57");
                    newsInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    newsInfo.put("@timestamp", timestamp2.format(new Date()));
                    newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                    esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                    if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                        RedisUtil.insertUrlToSet("catchedUrl", url);
//                    }
//                    if (mysqlUtil.insertNews(newsInfo, "crawler_news", newsId)){
//                        RedisUtil.insertUrlToSet("catchedUrl", url);
//                    }
                    if (mysqlUtil.insertNews(newsInfo)){
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                }
            } else {
                LOGGER.info("页面不存在！");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
