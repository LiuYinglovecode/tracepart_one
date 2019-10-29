package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class CinnToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CinnToRedis.class);
    private static String baseUrl = "http://www.cinn.cn";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(".banner_inner a");
                for (Element e : categoryList) {
                    if (!"#".equals(e.attr("href")) && !e.attr("href").contains("html")) {
                        category(baseUrl + e.attr("href"));
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.cinn.cn  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void category(String url) {
        try {
            for (int i = 1; i < 66; i++) {
                String html = HttpUtil.httpGetwithJudgeWord(url + "/index_" + String.valueOf(i) + ".html", "中国工业报社");
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements detailList = document.select(".block_left .smallblock.pd_news");
                    for (Element e : detailList) {
                        if (null != e.select(".news_title a").attr("href")) {
                            String detailUrl = url + e.select(".news_title a").attr("href").split(".", 2)[1];
                            RedisUtil.insertUrlToSet("toCatchUrl", detailUrl);
                        }
                    }
                } else {
                    LOGGER.info("category null");
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
