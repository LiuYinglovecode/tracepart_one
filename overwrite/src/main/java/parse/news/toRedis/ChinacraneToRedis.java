package parse.news.toRedis;

import Utils.RedisUtil;
import org.elasticsearch.common.recycler.Recycler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class ChinacraneToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinacraneToRedis.class);


    public static void main(String[] args) {
        ChinacraneToRedis chinacraneToRedis = new ChinacraneToRedis();
        chinacraneToRedis.homepage("http://www.chinacrane.net/news/");
    }
    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chinacrane");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.nav_news > ul > li > a");
                for (Element element : categoryList) {
                    if (!element.text().contains("首页")) {
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
            Element tailPage = document.select("div.ms_content > div > a").last().previousElementSibling();
            String pages = tailPage.text();
            String links = tailPage.attr("href");
            int total = Integer.parseInt(pages);
            for (number = 1; number <= total ; number++) {
                String link = links.replace(String.valueOf(total),String.valueOf(number));
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
