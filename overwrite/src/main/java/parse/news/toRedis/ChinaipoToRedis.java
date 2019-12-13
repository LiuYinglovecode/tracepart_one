package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ChinaipoToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinaipoToRedis.class);
    private static final String homepage = "http://www.chinaipo.com";

    public static void main(String[] args) {
        ChinaipoToRedis chinaipoToRedis =new ChinaipoToRedis();
        chinaipoToRedis.homepage("http://www.chinaipo.com/");
    }
    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chinaipo");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.botnav_left > li > a");
                for (Element element : categoryList) {
                    if (!element.text().contains("首页")) {
                        String href = homepage + element.attr("href");
                        paging(href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url ) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number ;
            String html = HttpUtil.httpGetwithJudgeWord(url, "chinaipo");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements tailPage = document.select("a.next").prev();
                String total = tailPage.text().replace("..","").trim();
                String attr = tailPage.attr("href");
                for (number = 1; number <= Integer.parseInt(total); number++) {
                    String nextPage = attr.replace(total,String.valueOf(number));
                    list.add(nextPage);
                }
                for (String link : list) {
                    newsList(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chinaipo");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.htn-de > h3 > a");
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
