package parse.company.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class SoleToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoleToRedis.class);

    public void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "51sole");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.category_l > dl > dd > a");
            for (Element e : select) {
                String href = "http:" + e.attr("href");
                company(href);

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void company(String url) {
        try {
            int totalPage = 50;
            int number;
            String html = HttpUtil.httpGetwithJudgeWord(url, "51sole");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.hy_include > ul > li > a");
            for (Element link : select) {
                for (number = 1; number <= totalPage; number++) {
                    String href = "http:" + link.attr("href") + "p" + number + "/";
//                    System.out.println(href);
                    companyList(href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "51sole");
            if (null != html) {
                Document document = Jsoup.parse(html);
                if (null != document) {
                    Elements select = document.select("span.fl > a");
                    for (Element link : select) {
                        if (link.attr("href").contains("http")) {
                            String attr = link.attr("href");
                            RedisUtil.insertUrlToSet("toCatchUrl-Company", attr);
                        } else {
                            String attr = "http:" + link.attr("href");
                            RedisUtil.insertUrlToSet("toCatchUrl-Company", attr);
                        }

                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
