package news.parse;

import com.alibaba.fastjson.JSONObject;
import ipregion.ProxyDao;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.IpProxyUtil;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Set;

/**
 * <a>https://www.gkzhan.com/</a>
 *智能制造新闻网
 * author:chenyan
 */
public class gkzhanNews {
    private final static Logger LOGGER = LoggerFactory.getLogger(gkzhanNews.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    //    新闻网首页
    private void homePage(String url) {

        try {
            String get = httpGet(url, "news");
            Document html = Jsoup.parse(get);
            Elements select = html.select("#nav > div > ul > li > a");
            for (Element element : select) {
                if (!"新闻首页".equals(element.text())) {
                    String href = element.attr("href");
                    newsList(href);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    新闻列表及分页
    private void newsList(String url) {

        try {
            String get = httpGet(url, "news");
            Document html = Jsoup.parse(get);
            Elements select = html.select("div.listLeft > div > h3 > a");
            for (Element element : select) {
                String href = element.attr("href");
                newsinfo(href);
            }

            Elements nextPage = html.select("a.lt");
            String href = nextPage.attr("href");
            if (!"#".equals(href)) {
                System.out.println("下一页：" + "https://www.gkzhan.com" + href);
                newsList("https://www.gkzhan.com" + href);
            }
            return;


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    新闻信息
    private void newsinfo(String url) {
        JSONObject newsInfo = new JSONObject();
        try {
            String get = httpGet(url, "news");
            Document parse = Jsoup.parse(get);
            newsInfo.put("plate", parse.select("div.position > p > span").text().trim());
            newsInfo.put("title", parse.select("div.leftTop.clearfix > h2").text().trim());
            newsInfo.put("time", parse.select("div.leftTop.clearfix > p > span:nth-child(1)").text().trim());
            newsInfo.put("text", parse.select("#newsContent").text().trim());
            Elements list = parse.select("div.leftTop.clearfix > p > span");
            for (Element element : list) {
                if (element.text().contains("来源：")) {
                    newsInfo.put("source", element.text().trim().split("：", 2)[1]);
                } else if (element.text().contains("编辑：")) {
                    newsInfo.put("author", element.text().trim().split("：", 2)[1]);
                } else if (element.text().contains("阅读量：")) {
                    newsInfo.put("amount_of_reading", element.text().trim().split("：", 2)[1]);
                }
            }

            newsInfo.put("crawlerId", "28");
            insert(newsInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void insert(JSONObject newsInfo) {
        Map = (java.util.Map) newsInfo;
        if (updateToMySQL.newsUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.136:2181");
        gkzhanNews gkzhanNews = new gkzhanNews();
        gkzhanNews.homePage("https://www.gkzhan.com/news/");
//        gkzhanNews.newsList("https://www.gkzhan.com/news/t14/list_p100.html");
        LOGGER.info("------完成了------");


    }

    private String httpGetWithProxy(String url, String judgeWord) {
        String ipProxy = null;
        try {
            if (ipProxyList.isEmpty()) {
                LOGGER.info("ipProxyList is empty");
                Set<String> getProxy = getProxy();
                ipProxyList.addProxyIp(getProxy);
            }
            ipProxy = ipProxyList.getProxyIp();
            String html = null;
            for (int i = 0; i < 5; i++) {
                if (url != null && ipProxy != null) {
                    html = HttpUtil.httpGetWithProxy(url, header, ipProxy);
                }
                if (html != null && html.contains(judgeWord)) {
                    return html;
                }
                ipProxyList.removeProxyIpByOne(ipProxy);
                ProxyDao.delectProxyByOne(ipProxy);
                ipProxy = ipProxyList.getProxyIp();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private String httpGet(String url, String judgeWord) {
        try {
            String html = null;
            for (int i = 0; i < 5; i++) {
                if (null != url) {
                    html = HttpUtil.httpGet(url, header);
                }
                if (html != null && html.contains(judgeWord)) {
                    return html;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private static Set<String> getProxy() {
        return ProxyDao.getProxyFromRedis();
    }

    private void write(String file) throws Exception {
        try {
            FileWriter out = new FileWriter(savePage, true);
            out.write(String.valueOf(file));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
