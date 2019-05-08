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

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class machine365News {
    private final static Logger LOGGER = LoggerFactory.getLogger(machine365News.class);
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
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("div.nav ul li a");
            for (Element element : select) {
                if (!"首页".equals(element.text()) && !"技术动态".equals(element.text()) && !"高端访谈".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
//                    System.out.println(href);
                    nextPage(href);
                }

                if ("技术动态".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    String html = httpGet(href, "news");
                    Document doc = Jsoup.parse(html);
                    Elements list = doc.select("div.more.tdchnique_more > a");
                    for (Element el : list) {
                        String link = "http://news.machine365.com" + el.attr("href");
                        newsList(link);
                    }
                }
                if ("高端访谈".equals(element.text())) {
                    String href = "http://news.machine365.com" + element.attr("href");
                    String html = httpGet(href, "news");
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
                String beginpag = replace+ "-" + beginPag+".shtml";
                System.out.println("下一页："+beginpag);
                newsList(beginpag);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }


//    新闻列表
    private void newsList(String url) {

        try {
            String get = httpGet(url, "news");
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
            String get = httpGet(url, "news");
            if (get!=null) {
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
            }
            newsInfo.put("crawlerId", "27");
            insert(newsInfo);
        } catch (Exception e) {
            if (e.getClass() != FileNotFoundException.class) {
                LOGGER.error(e.getMessage());
            }
        }
    }


    private void insert(JSONObject newsInfo) {
        Map = (java.util.Map) newsInfo;
        if (updateToMySQL.newsUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    public static void main(String[] args) {
        machine365News machine365 = new machine365News();
        machine365.homePage("http://news.machine365.com/");
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
