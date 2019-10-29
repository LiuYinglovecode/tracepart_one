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

public class FindzdToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(FindzdToRedis.class);
    private static String baseUrl = "http://www.findzd.com";

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("td.ItemFocus1 a,td.RightItemHead a");
                for (Element e : categoryList) {
                    String link =baseUrl + e.attr("href");
                    paging(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //分页
    private void paging(String url) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            Document document = Jsoup.parse(html);
            Element pages = document.select("div#lopage.pagenavi a").first();
            if (pages!=null && pages.text().contains("页次：")){
                String Total = pages.text().split("/")[1];
                int total = Integer.valueOf(Total).intValue();
                for (number = 1; number < total + 1; number++) {
                    if (url.contains("?")) {
                        String nextPage = url + "&page="+ number;
                        list.add(nextPage);
                    }else {
                        String nextPage = url + "?page=" + number;
                        list.add(nextPage);
                    }
                }
            }

            for (String link : list) {
                System.out.println("下一页：" + link);
                newsList(link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("tr td.font14 a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl",href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
