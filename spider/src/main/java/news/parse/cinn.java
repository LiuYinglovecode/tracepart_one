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
 * http://www.cinn.cn/
 */
public class cinn {
    private static final Logger LOGGER = LoggerFactory.getLogger(cinn.class);
    private static Map<String, String> header = new HashMap();
    private static final String homepage = "http://www.cinn.cn/";
    private static String baseUrl = "http://www.cinn.cn";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        cinn cinn = new cinn();
        cinn.homepage(homepage);
        LOGGER.info("cinn DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(".banner_inner a");
                for (Element e : categoryList) {
                    if (!"#".equals(e.attr("href")) && !e.attr("href").contains("html")) {
                        category(baseUrl + e.attr("href"));
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void category(String url) {
        try {
            for (int i = 1; i < 66; i++) {
                String html = HttpUtil.httpGetwithJudgeWord(url + "/index_" + String.valueOf(i) + ".html", "中国工业报社");
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements detailList = document.select(".block_left .smallblock.pd_news");
                    for (Element e : detailList) {
                        if (null != e.select(".news_title a").attr("href")) {
                            String detailUrl = url + e.select(".news_title a").attr("href").split(".", 2)[1];
                            detail(detailUrl);
                        }
                    }
                } else {
                    LOGGER.info("category null");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国工业报社");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                String plate = document.select(".index a:last-child").text().trim();
                info.put("url", url);
                info.put("plate", plate);
                String title = document.select(".detail_title").text().trim();
                info.put("title", title);
                Elements source_time_list = document.select(".detail_abs.bdsharebuttonbox span");
                for (Element e : source_time_list) {
                    if (e.text().contains("文章来源")) {
                        String source = e.text().trim().split("文章来源 :", 2)[1];
                        info.put("source", source);
                    }
                    if (e.text().contains("发布时间")) {
                        String time = e.text().trim().split("发布时间 ：", 2)[1];
                        info.put("time", time);
                    }
                }
                Elements imgList = document.select(".TRS_Editor img");
                if (!"0".equals(String.valueOf(imgList.size()))) {
                    for (Element e : imgList) {
                        if (e.attr("src").contains("http")) {
                            imgs.add(e.attr("src"));
                        } else if (e.attr("src").contains("./")) {
                            imgs.add(url.substring(0, url.lastIndexOf("/")) + e.attr("src").split("/", 2)[1]);
                        }
                    }
                }
                info.put("images", imgs.toString());
                String text = document.select(".detail_content").text().trim();
                info.put("text", text);
                info.put("crawlerId", "28");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", title);
                esUtil.writeToES(info, "crawler-news-", "doc", null);
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


}
