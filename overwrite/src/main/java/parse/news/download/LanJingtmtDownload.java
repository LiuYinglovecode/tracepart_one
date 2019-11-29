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

public class LanJingtmtDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(LedinsideDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd ", Locale.ENGLISH);
    private static SimpleDateFormat currentTime1 = new SimpleDateFormat("yyyy年", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        LanJingtmtDownload lanJingtmtDownload = new LanJingtmtDownload();
        lanJingtmtDownload.newsInfo("http://www.lanjingtmt.com/news/detail/45044.shtml");
    }

    public void newsInfo(String url) {
        try {

            String html = HttpUtil.httpGetWithProxy(url, "lanjingtmt");
            if (!html.isEmpty()) {
                String newsId = null;
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("#leftCont > div.newsTitle > h1").text().trim());
                Elements select = document.select("#leftCont > div.newsTitle > div.scd-title > em");
                if (!select.isEmpty()) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                        if (select.text().contains("今天")) {
                        info.put("time", ForMat.getDatetimeFormat(select.text().replace("今天", currentTime.format(calendar.getTime())).trim()));
                    } else if (select.text().contains("月")) {
                        info.put("time", ForMat.getDatetimeFormat(currentTime1.format(calendar.getTime()) + select.text()).trim());
                    } else if (select.text().contains("-")) {
                        info.put("time", ForMat.getDatetimeFormat(select.text()).trim());
                    }
                }

                Elements amountOfReading = document.select("div.scd-title > span");
                if (!amountOfReading.isEmpty()) {
                    for (Element element : amountOfReading) {
                        if (element.text().contains("阅读量：")) {
                            info.put("amountOfReading", element.text().replace("阅读量：", "").trim());
                        }
                    }
                }


                /**
                 * 文本信息,图片
                 */
                Elements text = document.select("#pageTxt");
                if (!text.isEmpty()) {

                    text.select("p img").last().remove();
                    String html1 = text.html();
                    info.put("html", html1);
                    String pureText = text.text().trim();
                    info.put("text", pureText);
                    newsId = NewsMd5.newsMd5(pureText.trim());
                    info.put("newsId", newsId);
                    Elements imgList = text.select("p > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            if (!e.attr("src").contains("https://tmtimg.lanjinger.com/ueditor/upload1/20150806/1438828721808210.jpg")) {
                                imgs.add(e.attr("src"));
                            }
                        }
                        info.put("images", imgs.toString());//图片
                    }

                }

                info.put("url", url);
                info.put("crawlerId", "151");
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
