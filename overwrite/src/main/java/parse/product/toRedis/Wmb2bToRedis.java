package parse.product.toRedis;


import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class Wmb2bToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(Wmb2bToRedis.class);

    //首页
    public void productPage(String url) {
        try {
            int number = 15434;
            int pages = 1;
            for (pages = 1; pages <= number; pages++) {
                String links = url + "index-htm-page-" + pages + ".html";
                String html = HttpUtil.httpGetwithJudgeWord(links, "wmb2b");
                if (html == null) {
                    Document document = Jsoup.parse(html);
                    Elements select = document.select(".list > table > tbody > tr > td > ul > li > a");
                    for (Element element : select) {
                        Elements select1 = element.select("strong.px17");
                        if (select1.size() != 0) {
                            String href = element.attr("href");
                            RedisUtil.insertUrlToSet("toCatchUrl-Product", href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
