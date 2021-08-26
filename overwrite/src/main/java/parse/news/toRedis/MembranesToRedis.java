package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class MembranesToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(MembranesToRedis.class);
    private static String baseUrl = "http://www.membranes.com.cn";


    public static void main(String[] args) {
        MembranesToRedis membranesToRedis = new MembranesToRedis();
        membranesToRedis.homepage("http://www.membranes.com.cn/xingyedongtai/xiehuidongtai/");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国膜工业协会");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.sidenav > ul > li > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        String link =baseUrl + e.attr("href");
                        industryNews(link);
                        paging(link);
                    }
                }
            }else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "中国膜工业协会");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                String categoryList = document.select("div.page.clearfix > a").last().attr("href");
                String next = categoryList.split("index_")[1].replace(".html", "");
                for (int i = 2; i <= Integer.parseInt(next) ; i++) {
                    String replace = baseUrl+categoryList.replace(next, String.valueOf(i));
                    industryNews(replace);
                }
            }else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


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
            }
            LOGGER.info("www.membranes.com.cn DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
