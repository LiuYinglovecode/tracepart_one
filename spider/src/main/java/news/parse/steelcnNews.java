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

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <a>http://news.steelcn.cn/</a>
 * <a>news：中钢网</a>
 *
 * @author:chenyan
 */
public class steelcnNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(steelcnNews.class);
    private static java.util.Map<String, String> header;
    private static final String homepage = "http://news.steelcn.cn/";
    //    private static SimpleDateFormat crawlerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +hh:mm", Locale.US);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        steelcnNews steelcnNews = new steelcnNews();
        steelcnNews.homepage(homepage);
        LOGGER.info("gkzhanNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    //首页
    private void homepage(String url) {
        //因为使用了Jsoup组合选择器查找元素会存在重复元素，这里使用HashSet去重
        HashSet<String> hs = new HashSet<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中钢网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.nav ul li,div.fdc_jj div h2 a");//Jsoup组合选择器查找元素
                for (Element e : categoryList) {
                    if (!e.select("a").text().equals("首页")) {
                        String attr = e.select("a").attr("href");
                        String plate = e.select("a").text();
                        hs.add(homepage + attr.replace("/", ""));
                        Iterator<String> it = hs.iterator();
                        while (it.hasNext()) {
                            String link = it.next();
                            paging(link, plate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
    分页：获取下一页总数，进行url的链接拼接
     */
    private void paging(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        int number = 1;
        String replace = url.replace(".html", "");
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中钢网");
            Document document = Jsoup.parse(html);
            String Total = document.select("div#Fenye strong").text().replace("1/", "").replace(" 页", "");
            int total = Integer.valueOf(Total).intValue();//转行类型
            for (number = 1; number < total + 1; number++) {
                String nextPage = replace + "_p" + number + ".html";
                list.add(nextPage);
            }
            for (String link : list) {
                newsList(link, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("div.list ul li a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                newsInfo(href, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
    新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
            把链接放到集合中，在进行存储。
     */
    private void newsInfo(String url, String plate) {
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
                }
            }
            newsInfo.put("images", imgsList.toString());//图片
            newsInfo.put("text", document.select("div.art_main").text().trim());//新闻内容
            newsInfo.put("plate", plate);//板块
            newsInfo.put("crawlerId", "48");
//            newsInfo.put("crawlerDate", crawlerDate.format(new Date()));
//            newsInfo.put("timestamp",System.currentTimeMillis());
//            newsInfo.put("timestamp",timestamp.format(new Date()));
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertNews(newsInfo, "crawler_news", title);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
