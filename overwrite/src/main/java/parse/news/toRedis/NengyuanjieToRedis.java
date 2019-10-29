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

public class NengyuanjieToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(NengyuanjieToRedis.class);

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("a.sub");
                for (Element e : categoryList) {
                    if (!e.text().equals("大讲堂") && !e.text().equals("访谈") && !e.text().equals("会议之声")) {
                        String link = e.attr("href");
                        String plate = e.text();
//                        Thread.sleep(7000);
                        paging(link, plate);
                    }
                }
            }
            LOGGER.info("www.nengyuanjie.net  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //分页
    private void paging(String url, String plate) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            Document document = Jsoup.parse(html);
            String Total = document.select("a.a1").prev().text();
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total + 1; number++) {
                String nextPage = url + "?&page=" + number;
                list.add(nextPage);
            }
            for (String link : list) {
                Thread.sleep(7000);
                newsList(link, plate);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "能源界");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("div.info h3 a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                Thread.sleep(7000);
                RedisUtil.insertUrlToSet("toCatchUrl", href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
