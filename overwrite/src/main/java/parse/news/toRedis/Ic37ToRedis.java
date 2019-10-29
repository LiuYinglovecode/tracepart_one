package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class Ic37ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(EapadToRedis.class);
    private static final String beasUrl = "https://www.ic37.com";

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.abctitle a,li#menu_guwen.tech div.sub_nav a");
                for (Element e : categoryList) {
                    if (!e.text().contains("技术文章首页") && !e.text().contains("文章投稿")) {
                        String href =beasUrl + e.attr("href");
//                        System.out.println(href);
                        paging(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String href) {
        try {
            String url = href.replace("1.htm", "");
            String html = HttpUtil.httpGetwithJudgeWord(href, "关于我们");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                int number = 1;
                Elements Total = parse.select("a.last");
                int total = Integer.valueOf(Total.text()).intValue();//类型转换
                for (number = 1; number <= total; number++) {
                    String link = url + number + ".htm";//拼接链接地址
//                    System.out.println("下一页：" + link);
                    newsList(link);
                }
            } else {
                System.out.println("页面不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (html!=null) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("li.listtitle h2 a");
                for (Element e : select) {
                    String link = beasUrl + e.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl", link);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
