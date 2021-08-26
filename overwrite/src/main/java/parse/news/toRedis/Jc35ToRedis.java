package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jc35ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jc35ToRedis.class);
    private static String link = new String("http://www.jc35.com");

    public static void main(String[] args) {
        Jc35ToRedis jc35ToRedis = new Jc35ToRedis();
        jc35ToRedis.homepage("http://www.jc35.com/news/");

    }



    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jc35");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nav > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        if (!e.text().contains("资讯首页")) {
                            String href = link.concat(e.attr("href"));
                            paing(href);
                        }
                    }
                }
            } else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paing(String href) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(href, "jc35");
            Document document = Jsoup.parse(html);
            Elements tailPage = document.select("div.newspages > a.next");
            String pageCount = tailPage.attr("href").replace(".html","").split("p")[1];
            String attr = tailPage.attr("href");
            int i;
            for (i = 2; i <= Integer.parseInt(pageCount); i++) {
                String replace = attr.replace(pageCount, String.valueOf(i));
                String links = link.concat(replace);
                newsList(links);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jc35");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.mainLeftList > dl > dt > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        String href = link.concat(e.attr("href"));
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
