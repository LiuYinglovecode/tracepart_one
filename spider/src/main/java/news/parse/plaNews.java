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

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>https://www.51pla.com/info/</a>
 * <p>全球塑胶网</p>
 *
 * @author chenyan
 */
public class plaNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(lmjxNews.class);
    private static Map<String, String> header = new HashMap();
    private static final String homepage = "https://www.51pla.com/info/";
    private static final String urllink = "https://www.51pla.com";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        plaNews plaNews = new plaNews();
        plaNews.homepage(homepage);
//        lmjxNews.newsList("https://news.lmjx.net/lmjx/lmjx.xhtml?p=265&p=266");
        LOGGER.info("plaNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    /**
     * 新闻首页：解析页面，拿到新闻分类的url
     *
     * @param url
     */
    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.type-list li a,div.tech-right div a,div.market-content a,a.page-label");
                for (Element e : categoryList) {
                    String href = urllink + e.attr("href");
                    String plate = e.text();
                    more(href, plate);
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * 分页：获取到总页数，对url进行拼接，得到完整的下一页新闻列表url。
     *
     * @param url
     */
    private void more(String url, String plate) {
        try {
            String replace = url.replace("1.htm", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "帮助中心");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String Total = parse.select("li.total").text().replace("共", "").replace("页", "");
                int total = Integer.valueOf(Total).intValue();
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = replace + number + ".htm";
//                    System.out.println(nextPage);
                    newsList(nextPage, plate);
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新闻列表 ：解析网页一次获取全部想要的新闻信息url
     *
     * @param url
     */
    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements title = parse.select("div.title a");
                if (title != null) {
                    for (Element element : title) {
                        newsInfo(urllink + element.attr("href"), plate);
                    }
                }
            } else {
                LOGGER.info("网页不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 新闻信息：解析获取信息，存入数据库及ES
     *
     * @param url
     */
    private void newsInfo(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                //链接地址
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                //标题
                String title = parse.select("div.title h2").text();
                info.put("title", title);
                Elements time = parse.select("div.meta > span");
                //发布时间及来源
                for (Element element : time) {
                    if (element.text().contains("发布时间：")) {
                        info.put("time", element.select("i").text().trim());
                    } else if (element.text().contains("来源：")) {
                        info.put("source", element.select("i").text().trim());
                    }
                }
                //正文
                Elements text = parse.select("div.content");
                if (text.size() != 0) {
                    info.put("text", text.text().trim());
                }
                //图片
                Elements images = parse.select("div.content p img");
                if (images.size() != 0) {
                    for (Element image : images) {
                        String src = urllink + image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                }
                info.put("plate", plate);
                info.put("crawlerId", "68");
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
