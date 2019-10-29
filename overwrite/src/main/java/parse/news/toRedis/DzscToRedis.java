package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class DzscToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(DzscToRedis.class);
    private static String baseUrl = new String("http://product.dzsc.com");

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "dzsc");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("body > div.nav > ul > li > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        if (!e.text().contains("资讯首页") && !e.text().contains("会展信息")
                                && !e.text().contains("产品图片") && !e.text().contains("资讯标签")) {
                            String href = baseUrl.concat(e.attr("href"));
                            for (int i = 1; i <= 100; i++) {
                                String links = href.replace("p1.html", "").concat("p").concat(String.valueOf(i)).concat(".html");
                                newsList(links);
                            }
                        }
                    }
                }
            } else {
                LOGGER.info("Page parsing failed...");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void newsList(String links) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(links, "dzsc");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("p.list-title.nowrap > a");
                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        RedisUtil.insertUrlToSet("toCatchUrl", element.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
