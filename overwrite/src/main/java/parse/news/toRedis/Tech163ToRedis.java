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

public class Tech163ToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(Tech163ToRedis.class);

    public void homePage(String url) {
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "163");
            Thread.sleep(1000);
            Document html = Jsoup.parse(get);
            Elements select = html.select("div.nav-channel.clearfix > div > div > a");
            for (Element element : select) {
                if (element.text().contains("互联")||element.text().contains("通信")||element.text().contains("IT")||element.text().contains("原创")) {
                    String href = element.attr("href");
                    paging(href);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //    分页
    private void paging(String url) {
//        newsList(url);
        ArrayList<String> list = new ArrayList<>();
        int number = 1;
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "163");
            Thread.sleep(1000);
            Document document = Jsoup.parse(html);
            String link = document.select("div.pages > a,div.endPageNum > a").first().nextElementSibling().nextElementSibling().attr("href");
            String text = document.select("div.pages > a,div.endPageNum > a").last().previousElementSibling().text();
            int total = Integer.valueOf(text).intValue();
            for (number = 2; number <= total; number++) {
                if (number<10){
                    String nextPage = link.replace("02","0"+number).replace("03","0"+number).replace("04","0"+number);
                    list.add(nextPage);
                }else {
                    String s = String.valueOf(number);
                    String nextPage = link.replace("02",s).replace("03",s).replace("04",s);
                    list.add(nextPage);
                }
            }
            for (String links : list) {
//                System.out.println("下一页：" + links);
                newsList(links);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    //    每页新闻链接
    private void newsList(String url) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "163");
            Thread.sleep(1000);
            Document document = Jsoup.parse(html);
            Elements titleList = document.select("div.titleBar.clearfix > h3 > a,ul.list_f14d > li> a");
            for (Element element : titleList) {
                list.add(element.attr("href"));
            }
            for (String link : list) {
                RedisUtil.insertUrlToSet("toCatchUrl",link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
