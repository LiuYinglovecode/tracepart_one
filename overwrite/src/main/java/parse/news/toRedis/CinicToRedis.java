package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class CinicToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CinicToRedis.class);
    private static String baseUrl = new String("http://cinic.org.cn");

    public static void main(String[] args) {
        CinicToRedis cinicToRedis = new CinicToRedis();
        cinicToRedis.homepage("http://cinic.org.cn");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.mainnav > li > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        if (!e.text().contains("首页")
                                &&!e.text().contains("特色小镇")
                                &&!e.text().contains("精准扶贫")
                                &&!e.text().contains("文化艺术")
                                &&!e.text().contains("党媒视点")) {
                            String href = baseUrl.concat(e.attr("href"));
                            newsList(href);
                            paging(href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "关于我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                String lastPage = document.select("div.pages.dc-more2.nextpageml > a").last().previousElementSibling().attr("href");
                if (!lastPage.isEmpty()) {
                    String pageCount = lastPage.split("index_", 2)[1].replace(".html", "");
                    int number;
                    for (number = 2; number <= Integer.parseInt(pageCount); number++) {
                        String links = baseUrl.concat(lastPage.replace(pageCount, String.valueOf(number)));
                        newsList(links);
                    }
                }else {
                    LOGGER.info("The page is empty...");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void newsList(String links) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(links, "关于我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select(" div.txt > h3 > a");
                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        RedisUtil.insertUrlToSet("toCatchUrl", baseUrl.concat(element.attr("href")));
                    }
                }else {
                    LOGGER.info("It's on the last page...");
                }
            }else {
                LOGGER.info("The page is empty...");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
