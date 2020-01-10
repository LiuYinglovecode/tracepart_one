package parse.news.toRedis;

import Utils.HtmlUnitUnits;
import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaserfairToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaserfairToRedis.class);

    public static void main(String[] args) {
        LaserfairToRedis laserfairToRedis = new LaserfairToRedis();
        laserfairToRedis.homepage("http://www.laserfair.com/");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "laserfair");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.menu.wrap3 > ul > li > a");
                for (Element e : categoryList) {
                    if (e.text().contains("行业新闻") || e.text().contains("行业应用") || e.text().contains("激光器件") || e.text().contains("高端访谈")) {
                        String href = e.attr("href");
                        ping(href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void ping(String url) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "laserfair");
            Document document = Jsoup.parse(html);
            Element tailPage = document.select("div.turn-page > a").last().previousElementSibling();
            String attr = tailPage.attr("href");
            String total = tailPage.text();
            int number;
            for (number = 1; number <= Integer.parseInt(total); number++) {
                String nextPage = attr.replace(total, String.valueOf(number));
//                System.out.println(nextPage);
                category(nextPage);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "laserfair");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("ul.sub-news-list > li > h5 > a");
                for (Element e : detailList) {
                    RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                }
            } else {
                LOGGER.info("该页面为空");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
