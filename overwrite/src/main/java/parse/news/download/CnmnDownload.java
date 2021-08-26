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

public class CnmnDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CnmnDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "有色");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String title = document.select("#content > div > h4").text().trim();
                newsInfo.put("title", title.trim());//标题
                newsInfo.put("time", ForMat.getDatetimeFormat(document.select("span > span.time").text().trim()));//发布时间
                newsInfo.put("amountOfReading", document.select("span > span.view").text().replace("次浏览", "").trim());//阅读量
                newsInfo.put("source", document.select("p.info.clearfix.text-center > span:nth-child(1)").text().split("分类：")[0].split("来源： ")[1].trim());//来源
                Elements text = document.select("#txtcont");
                newsInfo.put("text", text.text().trim());//新闻内容
                newsInfo.put("html", text.html());//新闻内容
                String newsId = NewsMd5.newsMd5(text.text().trim());
                newsInfo.put("newsId", newsId);
                Elements split = document.select("p.info.clearfix.text-center > span:nth-child(1)");
                if (split.text().contains("作者：")) {
                    newsInfo.put("author", split.text().split("作者：")[1].trim());//作者
                } else {
                    newsInfo.put("author", document.select("#content > div > p.actor").text().split("：")[1].trim());//作者
                }
                Elements img = document.select("#txtcont > p > img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                }
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "52");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(newsInfo)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            } else {
                LOGGER.info("页面不存在");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


}
