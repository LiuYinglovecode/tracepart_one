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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LedinsideToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(LedinsideToRedis.class);
    private static String baseUrl = "https://www.ledinside.cn";

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "媒体中心");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#primary1 > a,#primary2 > a,#primary4 > a,#primary5 > a,#primary6 > a,#primary7 > a,#primary8 > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        String link =baseUrl + e.attr("href");
//                        System.out.println(link);
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


    //分页
    private void paging(String url) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number;
            String html = HttpUtil.httpGetwithJudgeWord(url, "媒体中心");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                String pages = document.select("ul.pagination > li > a").last().attr("href");
                String regEx = "[^0-9]";
                Pattern compile = Pattern.compile(regEx);
                Matcher matcher = compile.matcher(pages);
                String trim = matcher.replaceAll("").trim();
                for (number = 1; number <= Integer.parseInt(trim); number++) {
                    String link = pages.replace(trim, String.valueOf(number));
                    list.add(link);
                }
                for (String links : list) {
                    newsList(links);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "媒体中心");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("td.title > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        RedisUtil.insertUrlToSet("toCatchUrl", baseUrl + e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
