package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class PlaToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaToRedis.class);
    private static final String urllink = "https://www.51pla.com";


    public static void main(String[] args) {
        PlaToRedis plaToRedis = new PlaToRedis();
        plaToRedis.homepage("https://www.51pla.com/info/");
    }
    /**
     * 新闻首页：解析页面，拿到新闻分类的url
     *
     * @param url
     */
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.type-list li a,div.tech-right div a,div.market-content a,a.page-label");
                for (Element e : categoryList) {
                    String href = urllink + e.attr("href");
                    more(href);
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.51pla.com DONE");
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
            String replace = url.replace("1.htm", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "帮助中心");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String Total = parse.select("li.total").text().replace("共", "").replace("页", "");
                int total = Integer.valueOf(Total).intValue();
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = replace + number + ".htm";
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements title = parse.select("div.title a");
                if (title != null) {
                    for (Element element : title) {
                        RedisUtil.insertUrlToSet("toCatchUrl", urllink + element.attr("href"));
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
