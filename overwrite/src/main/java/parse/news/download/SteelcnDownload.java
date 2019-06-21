package parse.news.download;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
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
import java.util.*;


/**
 * <a>http://news.steelcn.cn/</a>
 * <a>news：中钢网</a>
 *
 * @author:chenyan
 */
public class SteelcnDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(SteelcnDownload.class);

    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    /*
    新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
            把链接放到集合中，在进行存储。
     */
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中钢网");
            Document document = Jsoup.parse(html);
            String title = document.select("div#divinfo.art h1").text().trim();
            newsInfo.put("title", title);//标题
            newsInfo.put("amountOfReading", document.select("i#hits").text().trim());//阅读量
            Elements doc = document.select("div.art_info");
            for (Element el : doc) {
                el.select("span,i,em,label").remove();
                newsInfo.put("amountOfReading", el.text().split("来源:")[0].split(":")[1]);//阅读量
                newsInfo.put("source", el.text().split("来源:")[1].replace(" 字体:", ""));//来源
            }
            Elements img = document.select("div.art_main div img");
            if (img.size() != 0) {
                for (Element element : img) {
                    imgsList.add(element.attr("src"));
                    newsInfo.put("images", imgsList.toString());//图片
                }
            }

            String text = document.select("div.art_main").text().trim();
            newsInfo.put("text", text);//新闻内容
            String newsId = MD5Util.getMD5String(text);
            newsInfo.put("newsId",newsId);
            newsInfo.put("crawlerId", "48");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
