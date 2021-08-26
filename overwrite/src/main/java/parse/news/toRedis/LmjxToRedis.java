package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class LmjxToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(LmjxToRedis.class);

    public static void main(String[] args) {
        LmjxToRedis lmjxToRedis = new LmjxToRedis();
        lmjxToRedis.homepage("https://news.lmjx.net/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.wp a");
                for (Element e : categoryList) {
                    if (e.text().contains("行业资讯")) {
                        String href = e.attr("href");
                        String link = "https:" + href;
                        more(link);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("news.lmjx.net  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * 分页：获取到总页数，对url进行拼接，得到完整的下一页新闻列表url。
     *
     * @param url
     */
    private void more(String url) {
         try {
            String replace = url.replace("industry.html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "lmjx");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String Total = parse.select("a.nextprev[href]").prev().text();
                int total = Integer.valueOf(Total).intValue();
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = replace + "0_0_0_" + number + ".html";
                    newsList(nextPage);
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新闻列表 ：解析网页一次获取全部想要的新闻信息url
     *
     * @param url
     */
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "lmjx");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements title = parse.select("div#i_clist_1.clist div.item h1 a");
                if (title != null) {
                    for (Element element : title) {
                        RedisUtil.insertUrlToSet("toCatchUrl", element.attr("href"));
                    }
                }
            } else {
                LOGGER.info("网页不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
