package parse.news.download;

import Utils.ForMat;
import Utils.HttpUtil;
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
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Jc35Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoboticschinaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String link = new String("http://www.jc35.com");

    public static void main(String[] args) {
        Jc35Download jc35Download = new Jc35Download();
        jc35Download.newsInfo("http://www.jc35.com/news/detail/73387.html");
    }

    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jc35");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                String newsId = null;
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.newsShow > h1").text().trim());
                Elements time = document.select("div.newsTime > span");
                if (!html.isEmpty()) {
                    if (time.text().contains("年") && time.text().contains("来源：") && time.text().contains("点击：")) {
                        time.select("b").remove();
                        info.put("time", ForMat.getDatetimeFormat(time.text().split("来源：")[0].trim()));
                        info.put("source", time.text().split("来源：")[1].split("点击：")[0].trim());
                        info.put("amountOfReading", time.text().split("点击：")[1].trim());

                    }
                }

                /**
                 * 文本信息
                 */
                Elements text = document.select("#newsContent");
                if (!text.isEmpty()) {
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = text.select("p > img,div > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));

                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "141");
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
