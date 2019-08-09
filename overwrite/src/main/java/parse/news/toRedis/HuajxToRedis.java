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

public class HuajxToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(HuajxToRedis.class);

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div#nav div.nav p a");
                for (Element element : categoryList) {
                    if (!element.text().contains("新闻首页")) {
                        String href = element.attr("href");
                        paging(href);
                    }
                }
            }
            LOGGER.info("HuajxToRedis  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        String replace = url.replace(".html", "");
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document document = Jsoup.parse(html);
            String Total = document.select("span.jump").text().split("页")[0].replace("共","");
            int total = Integer.parseInt(Total);
            for (number = 1; number <= total ; number++) {
                String nextPage = replace + "_p" + number + ".html";
                list.add(nextPage);
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
                Elements newsListInfo = document.select("div.leftBox h3 a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
//                System.out.println(link);
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
