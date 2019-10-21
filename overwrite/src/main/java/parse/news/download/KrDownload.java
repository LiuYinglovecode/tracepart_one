package parse.news.download;

import Utils.HtmlUnitUnits;
import Utils.NewsMd5;
import Utils.RedisUtil;
import Utils.SleepUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import news.parse.ca800News;
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

public class KrDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(KrDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static final String homepage = "https://36kr.com/p/5255806";


    public static void main(String[] args) {
        KrDownload KrDownload = new KrDownload();
        KrDownload.newsInfo(homepage);

    }

    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "36Kr");
            Thread.sleep(SleepUtils.sleepMax());
            if (!html.isEmpty()) {
                String newsId = null;
                Document document = Jsoup.parse(html);
                Elements title = document.select("div > h1.article-title.margin-bottom-20.common-width");
                if (!title.isEmpty()) {
                    info.put("title", title.text().trim());
                    Elements time = document.select("div.article-title-icon.common-width.margin-bottom-40 > span");
                    if (time.text().contains("分钟") || time.text().contains("小时")) {
                        info.put("time", timestamp);
                    }
                    /**
                     * 文本信息
                     */
                    Elements text = document.select("div.common-width.margin-bottom-20");
                    if (!text.isEmpty()) {
                        String trim = text.html();
                        info.put("text", trim);
                        newsId = NewsMd5.newsMd5(trim.replace(" ", "").trim());
                        info.put("newsId", newsId);

                        /**
                         * 图片
                         */
                        Elements imgList = document.select("p > img");
                        if (!imgList.isEmpty()) {
                            for (Element e : imgList) {
                                imgs.add(e.attr("src"));
                            }
                            info.put("images", imgs.toString());//图片
                        }
                    }
                    info.put("url", url);
                    info.put("crawlerId", "129");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    System.out.println(info);
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                    if (mysqlUtil.insertNews(info, "crawler_news", newsId)) {
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                } else {
                    LOGGER.info("detail null");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info(e.getMessage());
        }

    }
}