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

public class CetDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CetDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String links = new String("http://www.cet.com.cn");

    public static void main(String[] args) {
        CetDownload cetDownload = new CetDownload();
        cetDownload.newsInfo("http://www.cet.com.cn/cjpd/hg/gj/2351623.shtml");
    }

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cet");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.threeContentLeft > h2[align=center]").text().trim());
                Elements select = document.select("div.threeContentLeft > p.time");
                if (!select.isEmpty()) {
                    select.select("a").remove();
                    info.put("time", ForMat.getDatetimeFormat(select.text().trim()));
                }


                Elements source = document.select("div.threeContentLeft > div[align=right]");
                if (!source.isEmpty()) {
                    source.select("div").remove();
                    source.select("script").remove();
                    if (source.text().contains("来源：") && source.text().contains("作者：") && source.text().contains("编辑：")) {
                        info.put("source", select.text().split("作者：")[0].replace("来源：", "").trim());
                    } else if (source.text().contains("来源：") && source.text().contains("编辑：")) {
                        info.put("source", select.text().split("编辑：")[0].replace("来源：", "").trim());
                    }
                }


                Elements text = document.select("div.article_content");
                info.put("text", text.text().trim());
                info.put("html", text.html());
                String newsId = NewsMd5.newsMd5(text.text().replace("", "").trim());
                info.put("newsId", newsId);
                Elements imgList = document.select("p img,div img,p a img");
                if (!imgList.isEmpty()) {
                    for (Element e : imgList) {
                        imgs.add(links.concat(e.attr("src")));
                    }
                    info.put("images", imgs.toString());//图片
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
