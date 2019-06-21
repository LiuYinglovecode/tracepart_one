package parse.news.download;

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

public class CnmnDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CesDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    //新闻信息
    private void newsInfo(String url, String plate) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "有色");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String title = document.select("#content > div > h4").text().trim();
                newsInfo.put("title", title);//标题
                newsInfo.put("time", document.select("span > span.time").text().trim());//发布时间
                newsInfo.put("amountOfReading", document.select("span > span.view").text().trim().replace("次浏览", ""));//阅读量
                newsInfo.put("source", document.select("p.info.clearfix.text-center > span:nth-child(1)").text().split("分类：")[0].split("来源： ")[1]);//来源
                String text = document.select("#txtcont").text().trim();
                newsInfo.put("text", text);//新闻内容
                String newsId = MD5Util.getMD5String(text);
                newsInfo.put("newsId",newsId);
                Elements split = document.select("p.info.clearfix.text-center > span:nth-child(1)");
                if (split.text().contains("作者：")) {
                    newsInfo.put("author", split.text().split("作者：")[1]);//作者
                } else {
                    newsInfo.put("author", document.select("#content > div > p.actor").text().split("：")[1]);//作者
                }
                Elements img = document.select("#txtcont > p > img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                }
                newsInfo.put("url", url);//链接地址
                newsInfo.put("plate", plate);//板块
                newsInfo.put("crawlerId", "52");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
                esUtil.writeToES(newsInfo, "crawler-news-", "doc");
            } else {
                LOGGER.info("页面不存在");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


}