package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class MembranesToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(MembranesToRedis.class);
    private static String baseUrl = "http://www.membranes.com.cn";

    public void industryNews(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国膜工业协会");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements industryList = document.select(".gyxw .new_t");
                for (Element e : industryList) {
                    String detailUrl = baseUrl + e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", detailUrl);
                }
                Elements nextPageList = document.select(".next");
                for (Element e : nextPageList) {
                    if ("下一页".equals(e.text().trim()) && !e.attr("href").contains("790")) {
                        industryNews(baseUrl + e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
