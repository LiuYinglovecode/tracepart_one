package news.parse;

import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import news.utils.ESUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a>http://news.machine365.com/</a>
 * <a>News：垂直机械网</a>
 *
 * @author:chenyan
 */
public class machine365News {
    private final static Logger LOGGER = LoggerFactory.getLogger(machine365News.class);
    private static java.util.Map<String, String> Map = null;
    private static java.util.Map<String, String> header;
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    //    新闻网首页
    private void homePage(String url) {

        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("div.nav ul li a");
            for (Element element : select) {
                if (!"首页".equals(element.text()) && !"技术动态".equals(element.text()) && !"高端访谈".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    nextPage(href);
                }

                if ("技术动态".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    String html = HttpUtil.httpGetwithJudgeWord(href, "news");
                    Document doc = Jsoup.parse(html);
                    Elements list = doc.select("div.more.tdchnique_more > a");
                    for (Element el : list) {
                        String link = "http://news.machine365.com" + el.attr("href");
                        newsList(link);
                    }
                }
                if ("高端访谈".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    String html = HttpUtil.httpGetwithJudgeWord(href, "news");
                    Document doc = Jsoup.parse(html);
                    Elements list = doc.select("div.more.interview_M > a");
                    for (Element el : list) {
                        String link = "http://news.machine365.com" + el.attr("href");
                        newsList(link);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //    下一页
    private void nextPage(String href) {
        try {
            String replace = href.replace(".shtml", "");
            int beginPag = 1;
            for (beginPag = 1; beginPag < 5361; beginPag++) {
                String beginpag = replace + "-" + beginPag + ".shtml";
                newsList(beginpag);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //    新闻列表
    private void newsList(String url) {

        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("div.guonei_l div div ul li a");
            for (Element element : select) {
                String href = element.attr("href");
                newsinfo(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //    新闻信息
    private void newsinfo(String url) {
        JSONObject newsInfo = new JSONObject();
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "news");
            if (get != null) {
                Document gbk = Jsoup.parse(new URL(url).openStream(), "GBK", get);
                Elements plate = gbk.select("span:nth-child(3)");
                if (plate.size() == 0) {
                    newsInfo.put("plate", gbk.select("body > div.yrhere > a:nth-child(2)").text().trim());
                } else {
                    newsInfo.put("plate", plate.text());

                }

                Elements title = gbk.select("div.newliIn_ti");
                if (title.size() == 0) {
                    newsInfo.put("title", gbk.select("div.left > div > h1").text().trim());
                } else {
                    newsInfo.put("title", title.text());
                }

                Elements time = gbk.select("div.newliIn_Sti");
                if (time.size() == 0) {
                    newsInfo.put("time", gbk.select("div.box1 > h4").text().trim().split("：", 2)[1]);
                } else {
                    newsInfo.put("time", time.text().trim());
                }
                newsInfo.put("text", gbk.select("#ArticleCnt").text().trim());
                String text = gbk.select("#ArticleCnt").text();
                String regEx = "来源：[\u4e00-\u9fa5]*";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(text);
                boolean rs = matcher.find();
                if (rs == true) {
                    String surce = matcher.group(0).split("：", 2)[1];
                    newsInfo.put("source", surce);
                }
            } else {
                LOGGER.info("页面不存在");
            }
            newsInfo.put("crawlerId", "27");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            esUtil.writeToES(newsInfo, "crawler-news-", "doc");
        } catch (Exception e) {
            if (e.getClass() != FileNotFoundException.class) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        machine365News machine365 = new machine365News();
        machine365.homePage("http://news.machine365.com/");
        LOGGER.info("machine365 DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }
}
