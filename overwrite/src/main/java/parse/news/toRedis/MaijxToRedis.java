package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class MaijxToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaijxToRedis.class);
    private static final String baseUrl = "http://www.maijx.com/information/";

    public static void main(String[] args) {
        MaijxToRedis maijxToRedis = new MaijxToRedis();
        maijxToRedis.homepage("http://www.maijx.com/information/record.html");

    }

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "机械网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.in div.f div.left div.nav div a");
                for (Element e : categoryList) {
                    if (!e.text().contains("平台事记")) {
                        String link = baseUrl + e.attr("href");
                        paging(link);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //分页
    private void paging(String url) {
        String replace = url.replace("http://www.maijx.com/information/search-1", "");
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            int total = 31908;
            for (number = 1; number <= total ; number++) {
                String nextPage = "http://www.maijx.com/information/search-" + number + replace;
                list.add(nextPage);
            }
            for (String link : list) {
                newsList(link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "机械网");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("ul#gb-pages-ul li a");
            if (newsListInfo!=null) {
                for (Element e : newsListInfo) {
                    String href =baseUrl+ e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", href);
                }
            }else {
                LOGGER.info("最后一页！");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
