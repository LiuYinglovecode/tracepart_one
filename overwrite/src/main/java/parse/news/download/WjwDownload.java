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
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WjwDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(WjwDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        WjwDownload wjwDownload = new WjwDownload();
        wjwDownload.detail("http://news.wjw.cn/NewsList-48/409983.xhtml");
    }

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "wjw");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("div.informationArticle_03 > h3").text().trim());
                Elements select = document.select("div.informationArticle_03 > div.informationArticle_05");
                if (select.size() != 0) {
                    info.put("time", ForMat.getDatetimeFormat(select.select("p.informationArticle_06").text().split(" ")[1].trim()));
                    info.put("source", select.select("p.informationArticle_06").text().split(" ")[0].replace("来源：", "").trim());
                }
                Elements text = document.select("#informationArticle_04");
                text.select("p > img").last().remove();
                text.select("p > img").remove();
                text.select("p.prvPage").remove();
                info.put("text", text.text().trim());
                info.put("html", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                Elements imgList = document.select("#informationArticle_04 > div > img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片
                }

                info.put("url", url);
                info.put("crawlerId", "72");
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
