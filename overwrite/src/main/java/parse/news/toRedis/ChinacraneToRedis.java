package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.util.ArrayList;

public class ChinacraneToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinacraneToRedis.class);

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.nav_news > ul > li > a");
                for (Element element : categoryList) {
                    if (!element.text().contains("首 页")) {
                        String href = element.attr("href");
                        paging(href);
                    }
                }
            }
            LOGGER.info("网页不存在！");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {

        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document document = Jsoup.parse(html);
            String Total = document.select("cite").text().split("/")[1].replace("页", "");
            int total = Integer.parseInt(Total);
            System.out.println(total);
            for (number = 1; number <= total ; number++) {
                String link = url + number + ".html";
                list.add(link);
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
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("li.bt > a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
