package news.parse;

import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import config.IConfigManager;

import java.util.HashMap;
import java.util.Map;

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

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        xianjichina xianjichina = new xianjichina();
        xianjichina.homepage(homepage);
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
                Document document = Jsoup.parse(html);
                if (1 == (document.select(".newl-left").size())) {
                    String title = document.select(".list-main h1").text().trim();
                    String source = document.select(".public-time").text().trim().split("文章来源：", 2)[1].split("发布时间：", 2)[0];
                    String time = document.select(".public-time").text().trim().split("发布时间：", 2)[1];
                    String text = document.select(".main-text").text().trim();
                    info.put("text", text);
                    info.put("source", source);
                    info.put("time", time);
                    info.put("plate", plate);
                    info.put("title", title);
                    info.put("crawlerId", "29");
                    insert(info);
                }
                if (1 == (document.select(".newconleft-top").size())) {
                    String title = document.select(".newconleft-top h1").text().trim();
                    String source = document.select(".public-time").text().trim().split("来源：", 2)[1];
                    String time = document.select(".public-time").text().trim().split("来源：", 2)[0];
                    String text = document.select(".newcon-list").text().trim();
                    info.put("text", text);
                    info.put("source", source);
                    info.put("time", time);
                    info.put("plate", plate);
                    info.put("title", title);
                    info.put("crawlerId", "29");

//                    insert(info);
                }
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info) {
        try {
            Map = (Map) info;
            if (updateToMySQL.newsUpdate(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
