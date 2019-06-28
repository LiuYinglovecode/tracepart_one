package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <a>http://www.cpnn.com.cn</a>
 * <a>News：中国电力新闻网</a>
 *
 * @author:chenyan
 */
public class cpnnNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(nengyuanjieNews.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static final String homepage = "http://www.cpnn.com.cn";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        cpnnNews cpnnNews = new cpnnNews();
        cpnnNews.homepage(homepage);
        LOGGER.info("cpnnNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    //首页
    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cpnn");
            if (null != html) {
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements categoryList = document.select("div.more-gy a");
                for (Element e : categoryList) {
                    if (!e.attr("href").equals("#")) {
                        String href = e.attr("href");
                        if (!href.contains("http://yq") && !href.contains("/fdpd/default.htm") && !href.contains("/2014xny/default.htm")) {
                            String link = homepage + href;
//                            System.out.println(link);
                            paging(link);
                        } else {
                            more(href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void more(String url) {
        try {
            if (!url.contains("http://")) {
                String link = "http://www.cpnn.com.cn" + url;
                String html = HttpUtil.httpGetwithJudgeWord(link, "cpnn");
                Document document = Jsoup.parse(new URL(link).openStream(), "GBK", html);
                Elements select = document.select("div.fa_navlist ul li a");
                if (select.size() != 0) {
                    for (Element element : select) {
                        if (element.attr("href").contains("#")) {
                            String href = element.attr("href");
                            paging(href);
                        }
                    }
                } else {
                    Elements select1 = document.select("p.ner_nav span a");
                    for (Element elements : select1) {
                        String href = "http://www.cpnn.com.cn" + elements.attr("href");
                        paging(href);
                    }
                }
            } else {
                String html = HttpUtil.httpGetwithJudgeWord(url, "cpnn");
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements select = document.select("div.tit h3 a");
                for (Element element : select) {
                    String href = "http://yq.cpnn.com.cn" + element.attr("href");
                    paging(href);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //分页
    private void paging(String url) {
        try {
            if (!url.contains("/wh/default.htm")) {
                ArrayList<String> list = new ArrayList<>();
                int number = 1;
                for (number = 1; number < 68; number++) {
                    String nextPage = url.replace(".htm", "") + "_" + number + ".htm";
                    list.add(nextPage);
                }
                for (String link : list) {
                    System.out.println("下一页：" + link);
                    newsList(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //新闻列表
    private void newsList(String url) {
        try {
            if (url.contains("default")) {
                String aDefault = url.split("default")[0];
                String html = HttpUtil.httpGetwithJudgeWord(url, "cpnn");
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements select = document.select("div.cpnn-content-left-list ul li h1 a");
                for (Element element : select) {
                    String href = aDefault + element.attr("href").replace("./", "");
                    newsInfo(href);

                }
            } else {
                String html = HttpUtil.httpGetwithJudgeWord(url, "cpnn");
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements select = document.select("div.cpnn-content-left-list ul li h1 a");
                for (Element element : select) {
                    String href = url + element.attr("href").replace("./", "");
                    newsInfo(href);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
            把链接放到集合中，在进行存储。
     */
    private void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cpnn");
            if (html != null) {
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements href = document.select("div.cpnn-minnav a");
                if (href.attr("href").equals("../")) {
                    newsInfo.put("plate", href.text().trim());
                }
                if (href.attr("href").equals("./")) {
                    newsInfo.put("plate", href.text().trim());
                }

                newsInfo.put("title", document.select("div.cpnn-con-title h1").text().trim());//标题
                String select = document.select("div.cpnn-zhengwen-time p").text();
                newsInfo.put("time", select.split("日")[1].split("：")[1]);
                newsInfo.put("source", select.split("日期")[0].split("：")[1]);
                Elements font = document.select("font span font");
                for (Element element : font) {
                    if (element.text().contains("责任编辑：")) {
                        newsInfo.put("author", element.text().trim().split("：")[1]);
                    }
                }
                Elements text = document.select("div.cpnn-con-zhenwen");//新闻内容
                if (text.size() != 0) {
                    if (text.select("div.cpnn-con-zhenwen div p b").text().contains("【稿件声明】")) {
                        text.select("div.cpnn-con-zhenwen div p b").remove();
                        newsInfo.put("text", text.text().replace("附件：", ""));
                    }
                }
                Elements img = document.select("div.Custom_UnionStyle img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                        newsInfo.put("images", imgsList.toString());//图片
                    }
                }
            } else {
                LOGGER.info("页面不存在！");
            }
//            newsInfo.put("plate",plate);//板块
            newsInfo.put("crawlerId", "58");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            System.out.println(newsInfo);
            insert(newsInfo);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private static void insert(JSONObject info) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.newsInsert(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
