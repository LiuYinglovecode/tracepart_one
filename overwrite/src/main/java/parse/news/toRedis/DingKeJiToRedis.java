package parse.news.toRedis;

import Utils.HtmlUnitUnits;
import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DingKeJiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(DingKeJiToRedis.class);
    private static String links = new String("http://www.dingkeji.com");

    public static void main(String[] args) {
        DingKeJiToRedis dingKeJiToRedis = new DingKeJiToRedis();
        dingKeJiToRedis.homepage("http://www.dingkeji.com/");

    }


    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "dingkeji");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsList = document.select("div.nav > a");
                if (!newsList.isEmpty()) {
                    for (Element e : newsList) {
                        if (e.attr("href").contains("category")) {
                            String href = e.attr("href");
//                            System.out.println(href);
                            paging(href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String href) {
        try {
            String dingkeji = HttpUtil.httpGetwithJudgeWord(href, "dingkeji");
            Document document = Jsoup.parse(dingkeji);
            Elements elements = document.select("div.page > span");
            String trim = elements.text().split("条")[0].replace("共", "").trim();
            double v = Double.parseDouble(trim) / 10;
            int parseInt = new Double(Math.ceil(v)).intValue();
            String first = document.select("div.page > a").first().attr("href");
            String pages = first.split("=")[0];
            for (int i = 1; i <= parseInt; i++) {
                String link = links.concat(pages).concat("=").concat(String.valueOf(i));
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "dingkeji");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.list9_r > div.title3 > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        String href = links.concat(e.attr("href"));
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
