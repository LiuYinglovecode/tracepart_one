package parse.news.download;

import Utils.NewsMd5;
import Utils.RedisUtil;
import Utils.SleepUtils;
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

public class ThepaperDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThepaperDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "thepaper");
//            Thread.sleep(SleepUtils.sleepMax());
            if (!html.isEmpty()) {
                String newsId = null;
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div.newscontent > h1");
                if (elements.isEmpty()) {
                    LOGGER.info("此文章已下线");
                } else {
                    info.put("title", elements.text().trim());

                    String time = document.select("div.news_about > p").eq(1).text().trim();
                    if (time.contains("来源：")){
                        info.put("time", time.split("来源：")[0].trim());
                        info.put("source",time.split("来源：")[1].trim());
                    }

                    /**
                     * 文本信息
                     */
                    Elements text = document.select("div.news_txt");
                    if (!text.isEmpty()) {

                        info.put("text",text.html());
                        newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                        info.put("newsId", newsId);

                        /**
                         * 图片
                         */
                        Elements imgList = text.select("img");
                        if (!imgList.isEmpty()) {
                            for (Element e : imgList) {
                                imgs.add(e.attr("src"));
                            }
                            info.put("images", imgs.toString());//图片
                        }

                        Elements author = document.select(" div.clearfix > div.infor_item");
                        if (!author.isEmpty()){
                            info.put("author",author.text().replace("责任编辑：","").trim());
                        }

                        info.put("url", url);
                        info.put("crawlerId", "127");
                        info.put("timestamp", timestamp.format(new Date()));
                        timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                        info.put("@timestamp", timestamp2.format(new Date()));
                        info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
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
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
