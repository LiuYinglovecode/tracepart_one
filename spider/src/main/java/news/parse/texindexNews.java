package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import util.ESUtil;
import util.mysqlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>http://www.texindex.com.cn/news/</a>
 * <a>news：纺织网</a>
 *
 * @author:chenyan
 */
public class texindexNews {
    private final static Logger LOGGER = LoggerFactory.getLogger(texindexNews.class);
    private static java.util.Map<String, String> header;
    //    private static SimpleDateFormat crawlerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +hh:mm", Locale.US);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        texindexNews texindexNews = new texindexNews();
        texindexNews.homePage("http://www.texindex.com.cn/news/");
        LOGGER.info("gkzhanNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    //    新闻网首页
    private void homePage(String url) {

        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document html = Jsoup.parse(new URL(url).openStream(), "GBK", get);
            Elements select = html.select("table[cellpadding=5] > tbody > tr > td.RightItemBody > a");
            for (Element element : select) {
                if (!element.attr("href").equals("/Articles/2018-5-21/430006.html") && !element.attr("href").equals("/Media/")) {
                    String href = "http://www.texindex.com.cn" + element.attr("href");
                    String text = element.text();
                    paging(href, text);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //    分页
    private void paging(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        int number = 1;
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            String Total = document.select("td[height=20]").text().split("共计 ")[1].split(" 个页面")[0];
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total + 1; number++) {
                String nextPage = url + "index" + number + ".html";
                list.add(nextPage);
            }
            for (String link : list) {
                System.out.println("下一页：" + link);
                newsList(link, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    //    每页新闻链接
    private void newsList(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            Elements titleList = document.select("td.RightItemBody table tbody tr td.InnerLink a");
            for (Element element : titleList) {
                list.add(element.attr("href"));
            }
            for (String link : list) {
                newsInfo(link, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //  新闻信息
    private void newsInfo(String url, String plate) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            String title = document.select("td.RightItemBody div h1").text().trim();
            newsInfo.put("title", title);
            Elements select = document.select("td.RightItemBody div.000000A");
            newsInfo.put("time", select.text().split("/ ")[1].split(" ")[0]);
            newsInfo.put("source", select.text().split("/ ")[1].split(" ")[2]);
            newsInfo.put("text", document.select("div#zoom").text().trim());
            Elements src = document.select("div#zoom p img");
            if (src.size() != 0) {
                for (Element element : src) {
                    if (element.attr("src").contains("http://")) {
                        imgsList.add(element.attr("src"));
                    }
                    if (!element.attr("src").contains("http://")) {
                        imgsList.add("http://www.texindex.com.cn" + element.attr("src"));
                    }

                }
            }
            Elements select1 = document.select("td.RightItemBody div");
            for (Element element : select1) {
                if (element.text().contains("编辑：")) {
                    newsInfo.put("author", element.text().split("辑：")[1]);
                }
                if (element.text().contains("点击数")) {
                    newsInfo.put("amountOfReading", element.text().split("点击数 ")[1].replace("( ", "").replace(" )", ""));
                }
            }
            newsInfo.put("images", imgsList.toString());
            newsInfo.put("plate", plate);
            newsInfo.put("crawlerId", "49");
//            newsInfo.put("crawlerDate", crawlerDate.format(new Date()));
////            newsInfo.put("timestamp",System.currentTimeMillis());
//            newsInfo.put("timestamp",timestamp.format(new Date()));
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            mysqlUtil.insertNews(newsInfo, "crawler_news", title);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
