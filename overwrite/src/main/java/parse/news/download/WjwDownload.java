package parse.news.download;

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
                    info.put("time", select.select("p.informationArticle_06").text().split(" ")[1]);
                    info.put("source", select.select("p.informationArticle_06").text().split(" ")[0].replace("来源：", ""));
                }
                Elements text = document.select("#informationArticle_04");
                text.select("p > img").last().remove();
                info.put("text", text.text().trim().replace("关注有惊喜", ""));
                String newsId = MD5Util.getMD5String(text.text().trim().replace("关注有惊喜", ""));
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
                mysqlUtil.insertNews(info, "crawler_news", newsId);
                esUtil.writeToES(info, "crawler-news-", "doc");
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
