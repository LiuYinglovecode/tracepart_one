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

public class CenaDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CenaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        CenaDownload cenaDownload = new CenaDownload();
        cenaDownload.newsInfo("http://www.cena.com.cn/ssxw/20190705/101394.html");
    }
    //新闻内容
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国电子报");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select(".content > h1").text().trim());//标题
                newsInfo.put("time", ForMat.getDatetimeFormat(document.select("div.midline > span.time").text().replace("发布时间：","").trim()));
                newsInfo.put("source", document.select("div.midline > span.laiyuan").text().replace("来源：","").trim());
                newsInfo.put("author", document.select("div.midline > span.zuozhe").text().replace("作者：","").trim());

                Elements text = document.select("#art_body");//新闻内容
                if (!text.isEmpty()) {
                    newsInfo.put("text", text.text().trim());
                    newsInfo.put("html", text.html());
                    String newsId = NewsMd5.newsMd5(text.text());
                    newsInfo.put("newsId", newsId);
                    Elements img = text.select("p img");
                    if (!img.isEmpty()) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                            newsInfo.put("images", imgsList.toString());//图片
                        }
                    }
                    newsInfo.put("crawlerId", "134");
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
                    if (mysqlUtil.insertCompany(newsInfo)){
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
