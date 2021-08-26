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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NkjDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(NkjDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd ", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        NkjDownload nkjDownload = new NkjDownload();
        nkjDownload.newsInfo("https://www.nkj.cn/140516.html");
    }

    public void newsInfo(String url) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(url, "nkj");
            if (!html.isEmpty()) {
                String newsId = null;
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.post-title > h1").text().trim());
                Elements select = document.select("span.postclock > i");
                if (!select.isEmpty()) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    if (select.text().contains("前")) {
                        info.put("time", ForMat.getDatetimeFormat(currentTime.format(calendar.getTime()).trim()));
                    } else if (select.text().contains("-")) {
                        info.put("time", ForMat.getDatetimeFormat(select.text().trim()));
                    }
                }

                /**
                 * 文本信息,图片
                 */
                Elements text = document.select("div.post-content");
                if (!text.isEmpty()) {
                    text.select("img[alt=牛科技-科技创新媒体]").remove();
                    String html1 = text.html();
                    info.put("html", html1);
                    String pureText = text.text().trim();
                    info.put("text", pureText);
                    newsId = NewsMd5.newsMd5(pureText.trim());

                    Elements imgList = text.select("p > img,div > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));
                        }
                        info.put("images", imgs.toString());//图片
                    }
                }
                info.put("newsId", newsId);
                info.put("url", url);
                info.put("crawlerId", "152");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
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

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
