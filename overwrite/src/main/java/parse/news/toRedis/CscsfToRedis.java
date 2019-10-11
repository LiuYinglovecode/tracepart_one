package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class CscsfToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CscsfToRedis.class);
    private static String baseUrl = new String("http://www.cscsf.com");

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.nav.main_block > ul > li:nth-child(1) > a,div.nav.main_block > ul > li:nth-child(2) > a,div.nav.main_block > ul > li:nth-child(5) > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        String href = e.attr("href");
                        paging(href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String href) {
        try {
            String htmlOne = HttpUtil.httpGetwithJudgeWord(href, "关于我们");
            if (!htmlOne.isEmpty()) {
                Document document = Jsoup.parse(htmlOne);
                String lastPage = document.select("div.page_block > a").last().attr("href");
                String pageCount = lastPage.split("&page=", 2)[1];
                int number;
                for (number = 1; number <= Integer.parseInt(pageCount) ; number++) {
                    String links = baseUrl.concat(lastPage.replace(pageCount, String.valueOf(number)));
                    newsList(links);
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
                Elements elements = document.select("div.news_Rlist > p.font14.Rlist_title > a");
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
