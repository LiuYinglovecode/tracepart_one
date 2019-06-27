package parse.news.toRedis;

import Utils.RedisUtil;
import news.parse.nengyuanjieNews;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.net.URL;
import java.util.ArrayList;

public class CpnnToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpnnToRedis.class);
    private static final String homepage = "http://www.cpnn.com.cn";

    //首页
    public void homepage(String url) {
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
                            paging(link);
                        } else {
                            more(href);
                        }
                    }
                }
            }
            LOGGER.info("www.cpnn.com.cn  DONE");
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
                    RedisUtil.insertUrlToSet("toCatchUrl", href);

                }
            } else {
                String html = HttpUtil.httpGetwithJudgeWord(url, "cpnn");
                Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
                Elements select = document.select("div.cpnn-content-left-list ul li h1 a");
                for (Element element : select) {
                    String href = url + element.attr("href").replace("./", "");
                    RedisUtil.insertUrlToSet("toCatchUrl", href);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
