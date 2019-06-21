package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class XianjichinaToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(XianjichinaToRedis.class);
    private static String baseUrl = "https://www.xianjichina.com";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "媒体报道");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(".www_topb_c.widths a");
                for (Element e : categoryList) {
                    if (e.attr("href").contains("list")) {
                        String plate = e.text().trim();
                        category(baseUrl + e.attr("href"), plate);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void category(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "媒体报道");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select(".newl-left .main-info");
                for (Element e : detailList) {
                    if (null != e.select("h3 a").attr("href")) {
                        String detailUrl = baseUrl + e.select("h3 a").attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", detailUrl);
                    }
                }
                if (!"#".equals(document.select(".next_page a").attr("href"))) {
                    category(document.select(".next_page a").attr("href"), plate);
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
