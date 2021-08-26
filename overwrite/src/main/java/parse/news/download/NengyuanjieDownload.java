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

public class NengyuanjieDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(NengyuanjieDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        NengyuanjieDownload nengyuanjieDownload = new NengyuanjieDownload();
        nengyuanjieDownload.newsInfo("http://www.nengyuanjie.net/article/12226.html");
    }
    /**
     * @param url 新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
     *            把链接放到集合中，在进行存储。
     */
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            if (html != null) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select("h1.art-title").text().trim());//标题
                Elements select = document.select("span.desc.mt15");
                newsInfo.put("amountOfReading", select.select("#hits").text().trim());
                select.select("#hits").remove();
                if (select.text().contains("来源：") && select.text().contains("浏览：")) {
                    newsInfo.put("time", ForMat.getDatetimeFormat(select.text().split("来源：")[0].trim()));
                    newsInfo.put("source", select.text().split("来源：")[1].replace("浏览：","").trim());
                }else if (select.text().contains("浏览：")){
                    newsInfo.put("time", ForMat.getDatetimeFormat(select.text().replace(" 浏览：","").trim()));

                }

                Elements img = document.select("div.content p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片
                    }
                }
                Elements text = document.select("div.content");//新闻内容
                if (text.size() != 0) {
                    newsInfo.put("text", text.text().trim());
                    newsInfo.put("html", text.html());
                    String newsId = NewsMd5.newsMd5(text.text().trim());
                    newsInfo.put("newsId", newsId);

                    newsInfo.put("crawlerId", "60");
                    newsInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    newsInfo.put("@timestamp", timestamp2.format(new Date()));
                    newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                    esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                    if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
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
