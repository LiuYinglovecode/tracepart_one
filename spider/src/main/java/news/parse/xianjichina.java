package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import util.ESUtil;
import util.mysqlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import config.IConfigManager;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liyujie
 * https://www.xianjichina.com/news
 */
public class xianjichina {
    private static final Logger LOGGER = LoggerFactory.getLogger(xianjichina.class);

    private static java.util.Map<String, String> header;
    private static Map<String, String> Map = null;
    private static final String homepage = "https://www.xianjichina.com/news";
    private static String baseUrl = "https://www.xianjichina.com";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        xianjichina xianjichina = new xianjichina();
        xianjichina.homepage(homepage);
        LOGGER.info("xianjichina DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "媒体报道");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(".www_topb_c.widths a");
                for (Element e : categoryList) {
                    if (e.attr("href").contains("list")) {
                        String plate = e.text().trim();
                        category(baseUrl + e.attr("href"), plate);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void category(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "媒体报道");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select(".newl-left .main-info");
                for (Element e : detailList) {
                    if (null != e.select("h3 a").attr("href")) {
                        String detailUrl = baseUrl + e.select("h3 a").attr("href");
                        detail(detailUrl, plate);
                    }
                }
                if (!"#".equals(document.select(".next_page a").attr("href"))) {
                    category(document.select(".next_page a").attr("href"), plate);
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "贤集网");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                if (1 == (document.select(".newl-left").size())) {
                    String title = document.select(".list-main h1").text().trim();
                    String source = document.select(".public-time").text().trim().split("文章来源：", 2)[1].split("发布时间：", 2)[0];
                    String time = document.select(".public-time").text().trim().split("发布时间：", 2)[1];
                    String text = document.select(".main-text").text().trim();
                    Elements imgList = document.select(".main-text img");
                    if (!"0".equals(String.valueOf(imgList.size()))) {
                        for (Element e : imgList) {
                            imgs.add(baseUrl + e.attr("src"));
                        }
                    }
                    info.put("images", String.valueOf(imgs));
                    info.put("url", url);
                    info.put("text", text);
                    info.put("source", source);
                    info.put("time", time);
                    info.put("plate", plate);
                    info.put("title", title);
                    info.put("crawlerId", "29");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", title);
                    esUtil.writeToES(info, "crawler-news-", "doc");
                }
                if (1 == (document.select(".newconleft-top").size())) {
                    String title = document.select(".newconleft-top h1").text().trim();
                    String source = document.select(".public-time").text().trim().split("来源：", 2)[1];
                    String time = document.select(".public-time").text().trim().split("来源：", 2)[0];
                    String text = document.select(".newcon-list").text().trim();
                    Elements imgList = document.select(".newcon-list img");
                    if (!"0".equals(String.valueOf(imgList.size()))) {
                        for (Element e : imgList) {
                            imgs.add(e.attr("src"));
                        }
                    }
                    info.put("images", imgs);
                    info.put("url", url);
                    info.put("text", text);
                    info.put("source", source);
                    info.put("time", time);
                    info.put("plate", plate);
                    info.put("title", title);
                    info.put("crawlerId", "29");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", title);
                    esUtil.writeToES(info, "crawler-news-", "doc");
                }
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
