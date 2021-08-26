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
 * <a>https://news.lmjx.net/</a>
 * <p>中国路面机械网</p>
 *
 * @author chenyan
 */
public class lmjxNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(lmjxNews.class);
    private static Map<String, String> header = new HashMap();
    private static final String homepage = "https://news.lmjx.net/";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        lmjxNews lmjxNews = new lmjxNews();
        lmjxNews.homepage(homepage);
        LOGGER.info("lmjxNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.wp a");
                for (Element e : categoryList) {
                    if (e.text().contains("行业资讯")) {
                        String href = e.attr("href");
                        String link = "https:" + href;
                        more(link);
                    }
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
    private void more(String url) {
        try {
            String replace = url.replace("industry.html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "lmjx");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String Total = parse.select("a.nextprev[href]").prev().text();
                int total = Integer.valueOf(Total).intValue();
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = replace + "0_0_0_" + number + ".html";
                    newsList(nextPage);
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
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "lmjx");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements title = parse.select("div#i_clist_1.clist div.item h1 a");
                if (title != null) {
                    for (Element element : title) {
                        newsInfo(element.attr("href"));
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
    private void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "lmjx");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("div > h1:eq(0)").text();
                info.put("title", title);
                Elements time = parse.select("div.pinf.cl span.time");
                if (time.size() != 0) {
                    info.put("time", time.text().trim());
                }
                Elements time1 = parse.select("div.details-timer.left");
                if (time1.size() != 0) {
                    info.put("time", time1.text().trim());
                }
                String select = parse.select("div.contentbox div.info").text().trim();
                if (select.contains("来源：")) {
                    info.put("time", select.split("来源：")[0]);
                    info.put("source", select.split("来源：")[1].replace("，转载请标明出处", ""));
                }
                Elements text = parse.select("div.content");
                if (text.size() != 0) {
                    info.put("text", text.text().trim());
                } else {
                    Elements text1 = parse.select("div.pageleft content");
                    info.put("text", text1.text().trim());
                }
                Elements images = parse.select("div.content p img");
                if (images.size() != 0) {
                    for (Element image : images) {
                        String src = image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                } else {
                    Elements text1 = parse.select("div.pageleft content p img");
                    for (Element image : text1) {
                        String src = image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                }


                info.put("crawlerId", "67");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", title);
                esUtil.writeToES(info, "crawler-news-", "doc", null);
                System.out.println(info);

            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
