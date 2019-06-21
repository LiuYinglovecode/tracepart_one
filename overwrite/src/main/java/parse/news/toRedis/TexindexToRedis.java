package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.net.URL;
import java.util.ArrayList;

public class TexindexToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(TexindexToRedis.class);

    //    新闻网首页
    public void homePage(String url) {

        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document html = Jsoup.parse(new URL(url).openStream(), "GBK", get);
            Elements select = html.select("table[cellpadding=5] > tbody > tr > td.RightItemBody > a");
            for (Element element : select) {
                if (!element.attr("href").equals("/Articles/2018-5-21/430006.html") && !element.attr("href").equals("/Media/")) {
                    String href = "http://www.texindex.com.cn" + element.attr("href");
                    String text = element.text();
                    paging(href, text);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //    分页
    private void paging(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        int number = 1;
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            String Total = document.select("td[height=20]").text().split("共计 ")[1].split(" 个页面")[0];
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total + 1; number++) {
                String nextPage = url + "index" + number + ".html";
                list.add(nextPage);
            }
            for (String link : list) {
                System.out.println("下一页：" + link);
                newsList(link, plate);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    //    每页新闻链接
    private void newsList(String url, String plate) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "texindex");
            Document document = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            Elements titleList = document.select("td.RightItemBody table tbody tr td.InnerLink a");
            for (Element element : titleList) {
                list.add(element.attr("href"));
            }
            for (String link : list) {
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
