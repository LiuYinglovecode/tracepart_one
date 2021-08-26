package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class ChemNetToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChemNetToRedis.class);
    private static final String baseUrl = new String("http://news.chemnet.com");


    public static void main(String[] args) {
        ChemNetToRedis chemNetToRedis = new ChemNetToRedis();
        chemNetToRedis.homepage("http://news.chemnet.com");
    }
    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(".classi a");
                for (Element element : categoryList) {
                    if (!element.text().contains("首页")&&!element.text().contains("预警预测")) {
                        paging(baseUrl.concat(element.attr("href")));
                    }

                }
            }
            LOGGER.info("网页不存在！");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            int number ;
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            Document document = Jsoup.parse(html);
            Element lastPage = document.select("div.pager > div.inc-page-jump > a").last().previousElementSibling();
            String href = lastPage.attr("href");
            String total = lastPage.text();
            for (number = 1; number <= Integer.parseInt(total) ; number++) {
                String link = baseUrl.concat("/").concat(href.replace(total,String.valueOf(number)));
                newsList(link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.content-list > ul > li > a");
                for (Element e : newsListInfo) {
                    RedisUtil.insertUrlToSet("toCatchUrl", baseUrl.concat(e.attr("href")));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
