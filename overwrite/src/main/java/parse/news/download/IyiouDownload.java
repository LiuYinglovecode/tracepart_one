package parse.news.download;

import Utils.ForMat;
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

public class IyiouDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(IyiouDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        Date date = new Date();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "iyiou");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                String newsId = null;
                Document document = Jsoup.parse(html);
                info.put("title", document.select("#post_title").text().trim());
                Elements plate = document.select("#post_industry");
                if (!html.isEmpty()){
                    info.put("plate",plate.text().trim());
                }
                Elements source = document.select("#post_source");
                if (!html.isEmpty()){
                    info.put("source",source.text().trim());
                }
                Elements author = document.select("#post_author");
                if (!html.isEmpty()){
                    info.put("author",author.text().trim());
                }
                Elements time = document.select("#post_date");
                if (!html.isEmpty()) {
                    String trim = time.text().trim();
                    if (trim.contains(" · ")) {
                        String replace = trim.replace(" · ", "");
                        info.put("time", ForMat.getDatetimeFormat(replace.trim()));
                    } else if (trim.contains("分钟前") && trim.contains("小时前")) {
                        info.put("time", ForMat.getDatetimeFormat(format.format(date)));
                    }
                }

                /**
                 * 文本信息
                 */
                Elements text = document.select("#post_brief,#post_thumbnail,#post_description");
                if (!text.isEmpty()) {
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = document.select("#post_description > p > img,#post_thumbnail > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            if (!e.attr("src").contains("https://imgcache.iyiou.com/Picture/2019-08-03/5d455d21a2df4.png")) {
                                imgs.add(e.attr("src"));
                            }
                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "128");
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
                    if (mysqlUtil.insertNews(info)) {
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                } else {
                    LOGGER.info("detail null");
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
