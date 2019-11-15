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

public class CeeiaDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CeeiaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        CeeiaDownload ceeiaDownload = new CeeiaDownload();
        ceeiaDownload.newsInfo("http://www.ceeia.com/News_View.aspx?classId=1&newsid=73206");
    }

    public void newsInfo(String url) {
        JSONObject info = new JSONObject();
        JSONArray imgs = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ceeia");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                String newsId = null;
                Document document = Jsoup.parse(html);
                info.put("title", document.select("#ctl00_MainContent_CNTitle").text().trim());

                Elements time = document.select("#ctl00_MainContent_LastTime");
                if (!time.isEmpty()){
                    info.put("time", ForMat.getDatetimeFormat(time.text().trim()));
                }
                Elements author = document.select("#ctl00_MainContent_Label5");
                if (!time.isEmpty()&&!author.equals("")){
                    info.put("author",author.text().trim());
                }
                Elements source = document.select("#ctl00_MainContent_NewsFrom");
                if (!source.isEmpty()){
                    info.put("source",source.text().trim());
                }

                Elements plate = document.select("#ctl00_MainContent_Title2");
                if (!plate.isEmpty()){
                    info.put("plate",plate.text().trim());
                }



                /**
                 * 文本信息
                 */
                Elements text = document.select("#pager_content");
                if (!text.isEmpty()) {
                    text.select("#ctl00_MainContent_NewsContent > p > a[href=http://www.ceeia.com/]").remove();
                    info.put("text", text.text().trim());
                    info.put("html", text.html());
                    newsId = NewsMd5.newsMd5(text.text().replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = text.select("#ctl00_MainContent_NewsContent > p > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));
                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "139");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    System.out.println(info);
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
