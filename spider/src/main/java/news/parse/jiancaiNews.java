package news.parse;

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
import util.mysqlUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>http://www.jiancai.com/info/</a>
 * <p>盛丰建材网</p>
 * @author chenyan
 */
public class jiancaiNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(jiancaiNews.class);
    private static Map<String, String> header = new HashMap();
    private static final String homepage = "http://www.jiancai.com/info/";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        jiancaiNews jiancaiNews = new jiancaiNews();
        jiancaiNews.homepage(homepage);
        LOGGER.info("jiancaiNews :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jiancai");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("tbody tr td a.black");
                for (Element e : categoryList) {
                    String href ="http://www.jiancai.com" + e.attr("href");
//                    System.out.println(href);
                    String plate = e.text();
                    paging(href,plate);
                }

            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            String replace = url.replace(".html", "");
            int number = 1;
            int total = 1952;
            for (number = 1; number < total; number++) {
                String link = replace +"-p"+ number + ".html";//拼接链接地址
                newsList(link,plate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "jiancai");
            if (html!=null) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("li.liMainList a");
                for (Element e : select) {
                    String link = "http://www.jiancai.com" + e.attr("href");
                    newsInfo(link, plate);
                }
            }else {
                LOGGER.info("网页不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsInfo(String url,String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jiancai");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url",url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("div.midbox1 h1").text().trim();
                info.put("title",title);
                Elements select = parse.select("div.midbox1 > p");
                if (select.text().contains("来源:")){
                    info.put("time",select.text().split("来源")[0].replace("发布日期",""));
                    info.put("source",select.text().split("来源")[1].split(":")[1]);
                }
                info.put("text",parse.select("div.midboxcont").text().trim());
                Elements images = parse.select("div.midboxcont p img");
                for (Element image : images) {
                    if (!image.attr("src").contains("http://")){
                        String src = "http:" + image.attr("src");
                        imgs.add(src);
                    }else {
                        String src = image.attr("src");
                        imgs.add(src);
                    }
                    info.put("images", imgs.toString());
                }
                info.put("plate",plate);
                info.put("crawlerId", "69");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", title);
                esUtil.writeToES(info, "crawler-news-", "doc");
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
