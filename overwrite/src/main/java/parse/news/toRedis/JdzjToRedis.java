package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class JdzjToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdzjToRedis.class);

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.eif-header-nav ul li");
                for (Element e : categoryList) {
                    if (!e.text().contains("首　页")) {
                        String href = "http://www.jdzj.com/news/zx" + e.attr("zx") + ".html";
                        String plate = e.text();
                        paging(href, plate);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.jdzj.com  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url, String plate) {
        try {
            String replace = url.replace(".html", "").replace("zx", "");
            int number = 1;
            int total = 3000;
            for (number = 1; number < total; number++) {
                String link = replace + "_0__" + number + ".html";//拼接链接地址
                newsList(link, plate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url, String plate) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "站内导航");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String text = parse.select("div.pageNav a").last().text().trim();
                if (text.equals("下一页")) {
                    Elements select = parse.select("div.singleNews h3 a");
                    for (Element e : select) {
                        String link = "http://www.jdzj.com" + e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                    }
                } else {
                    LOGGER.info("最后一页！");
                }
            } else {
                LOGGER.info("网页不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
