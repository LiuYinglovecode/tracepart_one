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
import parse.news.toRedis.QlmoneyToRedis;
import util.ESUtil;
import Utils.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class QlmoneyDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(QlmoneyDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        QlmoneyDownload qlmoneyDownload = new QlmoneyDownload();
        qlmoneyDownload.newsInfo("http://www.qlmoney.com/content/20191022-355600.html");
    }

    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        String newsId = null;
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "qlmoney");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                info.put("title", document.select("#content_l > div > h1").text().trim());
                Elements elements = document.select("#content_l > div.main_box.content_info > span");
                for (Element element : elements) {
                    if (element.text().contains("-")){
                        info.put("time", ForMat.getDatetimeFormat(element.text().trim()));
                    }else if (element.text().contains("?????????")){
                        info.put("source", element.text().replace("?????????","").trim());
                    }else if (element.text().contains("?????????")){
                        if (element.text().replace("?????????","").equals("")){
                            info.put("author",document.select("div.main_box.f_r.content_editor > span")
                                    .text()
                                    .replace("???????????????","").trim());
                        }else {
                            info.put("author", element.text().replace("?????????","").trim());
                        }
                    }else if (element.text().contains("?????????")){
                        info.put("amountOfReading", element.text().replace("?????????","").trim());
                    }
                }

                /**
                 * ????????????
                 */
                Elements text = document.select("div.main_box.descripition,div.main_box.content_show");
                if (!text.isEmpty()) {
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);
                    /**
                     * ??????
                     */
                    Elements imgList = text.select("p img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));
                        }
                        info.put("images", imgs.toString());//??????
                    }

                    info.put("url", url);
                    info.put("crawlerId", "133");
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
