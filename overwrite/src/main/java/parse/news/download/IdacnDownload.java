package parse.news.download;

import Utils.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class IdacnDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(JingMeiTiDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String links = "http://www.idacn.org";


    public static void main(String[] args) {
        IdacnDownload IdacnDownload = new IdacnDownload();
        IdacnDownload.newsInfo("http://www.idacn.org/news/39871.html");
    }

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "idacn");
            Thread.sleep(SleepUtils.sleepMin());
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements elements = parse.select("div.article > h1");
                if (!elements.isEmpty()) {
                    newsInfo.put("title", elements.text().trim());
                }

                Elements time = parse.select("span.time");
                if (!html.isEmpty()) {
                    String trim = time.text().trim();
                    newsInfo.put("time", ForMat.getDatetimeFormat(trim));
                }

                Elements source = parse.select("small.extinfo");
                if (!html.isEmpty()) {
                    source.select("a").remove();
                    source.select("span").remove();
                    newsInfo.put("source",source
                            .text()
                            .replace("关键词","")
                            .replace("来源 ","")
                            .replace(",","")
                            .trim() );
                }

                Elements select = parse.select("div.content");
                if (!select.isEmpty()) {
                    String trim = select.text().trim();
                    String s = NewsMd5.newsMd5(trim);
                    newsInfo.put("text", trim);
                    newsInfo.put("newsId", s);
                    newsInfo.put("html", select.html());

                    Elements pImg = select.select("p > img");
                    if (!pImg.isEmpty()) {
                        String attr = links.concat(pImg.attr("src"));
                        imgsList.add(attr);

                    }
                }

                newsInfo.put("images",imgsList.toString());
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "175");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(newsInfo)) {
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
