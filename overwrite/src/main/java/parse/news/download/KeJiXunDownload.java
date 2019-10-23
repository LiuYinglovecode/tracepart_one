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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeJiXunDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeJiXunDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        KeJiXunDownload keJiXunDownload = new KeJiXunDownload();
        keJiXunDownload.newsInfo("http://www.kejixun.com/article/181024/447370.shtml");
    }

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "kejixun");
            if (!html.isEmpty()) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                info.put("title", parse.select("div.main.fl > h1").text().trim());
                Elements elements = parse.select("div.writer");
                if (!elements.isEmpty()){
                    /**
                     * 用正则获取来源
                     */
                    String re = "[^x00-xff]";
                    Pattern compile = Pattern.compile(re);
                    Matcher matcher = compile.matcher(elements.text());
                    String time = matcher.replaceAll("").replaceAll(":","").trim();
                    info.put("time",ForMat.getDatetimeFormat(time));
                }

                Elements author = parse.select("div.big-man > h3 > a");
                if (!author.isEmpty()){
                    info.put("author",author.text());
                }

                Elements text = parse.select("div.article-content");
                info.put("text", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                Elements images = text.select("p > img");
                if (!images.isEmpty()) {
                    for (Element image : images) {
                        imgs.add(image.attr("src"));
                    }
                    info.put("images", imgs.toString());
                }



                info.put("crawlerId", "132");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
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
