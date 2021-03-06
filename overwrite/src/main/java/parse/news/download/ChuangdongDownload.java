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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChuangdongDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChuangdongDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        ChuangdongDownload chuangdongDownload = new ChuangdongDownload();
        chuangdongDownload.newsInfo("https://www.chuandong.com/news/news236437.html");
    }

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (html != null) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select("div.newsDetail-title > h1").text().trim());//标题
                Elements select = document.select("div.newsDetail-title > p > span");
                for (Element element : select) {
                    if (element.text().contains("时间：")){
                        newsInfo.put("time", ForMat.getDatetimeFormat(element.text().replace("时间：","").trim()));//发布时间
                    }else if (element.text().contains("来源：")){
                        newsInfo.put("source", element.text().replace("来源：","").trim());//发布时间
                    }
                }

                Elements textInfo = document.select("div.newsDetail-title > div,#article-con");
//                if (textInfo.select("p").text().contains("声明：本文为转载类文章")){
//                    textInfo.select("p").remove();
                newsInfo.put("text", textInfo.text().trim());//新闻内容
                newsInfo.put("html", textInfo.html());//新闻内容
                String newsId = NewsMd5.newsMd5(textInfo.text().trim());
                newsInfo.put("newsId", newsId);
                Elements img = textInfo.select("center p img,p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                } else if (textInfo.select("p > span > o:p > img").size() != 0) {
                    Elements img1 = textInfo.select("p > span > o:p > img");
                    for (Element element : img1) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }
                } else if (textInfo.select("p > span > span > img").size() != 0) {
                    Elements img2 = textInfo.select("p > span > span > img");
                    for (Element element : img2) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片链接
                    }

                }
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "80");
//                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));

//                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)) {
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
