package parse.news.download;

import Utils.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZhiDingDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZhiDingDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        ZhiDingDownload zhiDingDownload = new ZhiDingDownload();
        zhiDingDownload.detail("http://server.zhiding.cn/server/2019/1115/3122669.shtml");
    }

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "zhiding");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.articleBox_content > div > h1").text().trim());
                Elements author = document.select("div.zuo > p > span");
                if (!author.isEmpty()){
                    info.put("author",author.text().trim());
                }
                Elements select = document.select("div.zuo > p").eq(0);
                if (!select.isEmpty()) {
                    String re = "[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}";
                    Pattern compile = Pattern.compile(re);
                    Matcher matcher = compile.matcher(select.text());
                    String source = matcher.replaceAll("").trim();
                    info.put("time", ForMat.getDatetimeFormat(select.text().replace(source, "").trim()));
                }
                Elements text = document.select("div.zhao,div.ocn");
                info.put("text", text.text().trim());
                info.put("html", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                Elements imgList = document.select("p > a > img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片
                }

                info.put("url", url);
                info.put("crawlerId", "149");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info)){
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
