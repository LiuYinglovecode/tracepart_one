package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import mysql.updateToMySQL;
import news.utils.ESUtil;
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
 * <a>http://www.ces.cn/news/</a>
 * <p>News：中国节能网</p>
 * @author chenyan
 */
public class cesNews {
    private static final Logger LOGGER = LoggerFactory.getLogger(nengyuanjieNews.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static final String homepage = "http://www.ces.cn/news/";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "10.153.40.117:2181");
        cesNews cesNews = new cesNews();
        cesNews.homepage(homepage);
        LOGGER.info("gkzhanNews DONE :" + String.format("%tF", new Date()) + String.format("%tT", new Date()));

    }

    //首页
    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国节能网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.Nav ul li a");
                for (Element element : categoryList) {
                    if (!element.text().contains("新闻首页")) {
                        String href = element.attr("href");
                        String plate = element.text().trim();
                        paging(href,plate);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        String replace = url.replace(".html", "");
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国节能网");
            Document document = Jsoup.parse(html);
            String Total = document.select("#page-nav.pagination cite").text().split("/")[1].replace("页","");
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total+1; number++) {
                String nextPage = replace+"-"+number+".html";
                list.add(nextPage);
            }
            for (String link : list) {
                System.out.println("下一页：" + link);
                newsList(link,plate);

            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国节能网");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("div.fl.name a");
            for (Element e : newsListInfo){
                String href = e.attr("href");
                newsInfo(href,plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }


    //新闻内容
    private void newsInfo(String url,String plate) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url",url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url,"中国节能网");
            if (html!=null) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select("#title.title").text().trim());//标题
                Elements select = document.select("div.fx_lyu span");
                if (select.size() != 0) {
                    for (Element element : select) {
                        if (element.text().contains("来源")) {
                            newsInfo.put("time", select.text().split("来源")[0]);
                            newsInfo.put("plate", select.text().split("：")[1].replace("0 0", ""));
                        }
                    }
                }

                Elements text = document.select("#article.content");//新闻内容
                if (text.size() != 0) {
                    newsInfo.put("text", text.text());
                    Elements img = text.select("div img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                            newsInfo.put("images", imgsList.toString());//图片
                        }
                    }
                }
            }else {
                LOGGER.info("页面不存在！");
            }
            newsInfo.put("crawlerId", "57");
            newsInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            newsInfo.put("@timestamp", timestamp2.format(new Date()));
            newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
            System.out.println(newsInfo);
            insert(newsInfo);
            esUtil.writeToES(newsInfo, "crawler-news-", "doc");
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
