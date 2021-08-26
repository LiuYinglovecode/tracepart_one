package parse.news.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class ChuandongToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChuandongToRedis.class);

    public static void main(String[] args) {
        ChuandongToRedis chuandongToRedis = new ChuandongToRedis();
        chuandongToRedis.paging("https://www.chuandong.com/update/newnews.html");
    }

    //分页
    public void paging(String url) {
        String replace = url.replace(".html", "");
        try {
            int number = 1;
            int total = 20;
                for (number = 1; number <= total ; number++) {
                    String nextPage = replace + "_p" + number + ".html";
                    newsList(nextPage);
                }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chuandong");
//            Thread.sleep(SleepUtils.sleepMax());
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.tb-tr > a");
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
