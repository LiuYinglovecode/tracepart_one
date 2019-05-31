package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import news.utils.ESUtil;
import news.utils.mysqlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


/**
 * http://www.membranes.com.cn/xingyedongtai/gongyexinwen/index.html
 */
public class membranes {
    private static final Logger LOGGER = LoggerFactory.getLogger(membranes.class);
    private static String industryListUrl = "http://www.membranes.com.cn/xingyedongtai/gongyexinwen/index.html";
    private static String baseUrl = "http://www.membranes.com.cn";
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static String tableName = "original_news";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        membranes membranes = new membranes();
        membranes.industryNews(industryListUrl);
        LOGGER.info("membranes DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    private void industryNews(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国膜工业协会");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements industryList = document.select(".gyxw .new_t");
                for (Element e : industryList) {
                    String detailUrl = baseUrl + e.attr("href");
                    detail(detailUrl);
                }
                Elements nextPageList = document.select(".next");
                for (Element e : nextPageList) {
                    if ("下一页".equals(e.text().trim()) && !e.attr("href").contains("790")) {
                        industryNews(baseUrl + e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String detailUrl) {
        try {
            JSONObject info = new JSONObject();
            JSONArray imgs = new JSONArray();
            String html = HttpUtil.httpGetwithJudgeWord(detailUrl, "中国膜工业协会");
            if (null != html) {
                Document document = Jsoup.parse(html);
                String title = document.select(".title").text().trim();
                String author = document.select(".time").text().trim().split("/", 2)[0];
                String time = document.select(".time").text().trim().split("/", 2)[1].split("：", 2)[1];
                String text = document.select(".newstext").text().trim();
                String url = detailUrl;
                Elements imgList = document.select(".newstext img");
                for (Element e : imgList) {
                    imgs.add(baseUrl + e.attr("src"));
                }
                info.put("title", title);
                info.put("author", author);
                info.put("time", time);
                info.put("text", text);
                info.put("url", url);
                info.put("images", imgs.toString());
                info.put("crawlerId", "50");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", title);
                esUtil.writeToES(info, "crawler-news-", "doc");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
