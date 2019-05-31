package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
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
 * <a>http://news.cableabc.com/</a>
 * <p>电缆网</p>
 *
 * @author chenyan
 */
public class cableabcNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(cableabcNews.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static final String homepage = "http://news.cableabc.com/";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        cableabcNews cableabcNews = new cableabcNews();
        cableabcNews.homepage(homepage);
        LOGGER.info("gkzhanNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    //主页
    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.list ul li a");
                for (Element element : categoryList) {
                    if (!element.text().equals("资讯首页") && !element.text().equals("招标") && !element.text().equals("展会")) {
                        String href = element.attr("href");
//                        System.out.println(href);
                        String plate = element.text();
                        paging(href, plate);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 0;
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String href = document.select("#main_cont_ContentPlaceHolder1_pager.pager a").last().attr("href");
                String html1 = HttpUtil.httpGetwithJudgeWord(href, "电缆网");
                Document document1 = Jsoup.parse(html1);
                String Total = document1.select("#main_cont_ContentPlaceHolder1_pager.pager span").text().trim();
                int total = Integer.valueOf(Total).intValue();
                for (number = 0; number < total; number++) {
                    if (url.contains("0.html")) {
                        String replace = url.replace("0.html", "");
                        list.add(replace + number + ".html");
                    }
                    if (url.contains("http://special.cableabc.com/")) {
                        list.add(url + "speciallist_" + number + ".html");
                    }
                    if (url.contains("http://material.cableabc.com/")) {
                        list.add(url + "materIndex_" + number + ".html");
                    }
                }
                for (String link : list) {
//                    System.out.println("下一页：" + link);
                    newsList(link, plate);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("h2.list31_title1 a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
                newsInfo(link, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻信息
    private void newsInfo(String url, String plate) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String title = document.select("div.contentspage h1").text().trim();
                newsInfo.put("title", title);//标题
                newsInfo.put("time", document.select("div.addtime").text().trim());//发布时间
                Elements select = document.select("div.time.clearfix.mmbb span");
                for (Element element : select) {
                    if (element.text().contains("/")) {
                        newsInfo.put("time", element.text().trim());
                    } else if (element.text().contains("来源：")) {
                        newsInfo.put("source", element.text().trim().split("：")[1]);
                    }
                }
                Elements text = document.select("#divcontent");
                if (text.size() != 0) {
                    Elements select1 = text.select("#main_ContentPlaceHolder1_pnlContent.info_ltext.lhh.lll_content");
                    if (select1.size() != 0) {
                        if (select1.select("p").last().previousElementSibling().text().contains("转载请注明出处")) {
                            select1.select("p").last().previousElementSibling().remove();
                            newsInfo.put("text", select1.text());
                        } else {
                            select1.select("p").last().remove();
                            newsInfo.put("text", select1.text());
                        }
                    }
                    if (text.select("p").last().text().contains("转载请注明出处")) {
                        text.select("p").last().remove();
                        newsInfo.put("text", text.text());
                    } else {
                        newsInfo.put("text", text.text());
                    }
                    Elements img = text.select("#main_ContentPlaceHolder1_pnlContent.info_ltext.lhh.lll_content p img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            if (element.attr("src").contains("http://news.cableabc.com")) {
                                imgsList.add(element.attr("src"));
                            } else {
                                imgsList.add("http://news.cableabc.com" + element.attr("src"));
                            }
                            newsInfo.put("images", imgsList.toString());//图片链接
                        }
                    }
                }
                newsInfo.put("url", url);//链接地址
                newsInfo.put("plate", plate);//板块
                newsInfo.put("crawlerId", "53");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                insert(newsInfo);
                mysqlUtil.insertNews(newsInfo, "crawler_news", title);
                esUtil.writeToES(newsInfo, "crawler-news-", "doc");
            } else {
                LOGGER.info("页面不存在");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info) {
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
