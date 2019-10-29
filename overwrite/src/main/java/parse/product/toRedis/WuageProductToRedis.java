package parse.product.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class WuageProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(WuageProductToRedis.class);
    private static String  baseUrl = "https://www.wuage.com";

    //首页
    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "钢材市场");
            Thread.sleep(SleepUtils.sleepMin());
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.item div.cont a");
            for (Element e : select) {
                String href = e.attr("href");
                paging(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String attr) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(attr, "钢材市场");
            Thread.sleep(SleepUtils.sleepMin());
            Document parse = Jsoup.parse(html);
            String href = parse.select("div.fe-page-linkBox a.fe-page-item.active").first().nextElementSibling().attr("href");
            Element lastPage = parse.select("li.mini-page span").first().nextElementSibling();
            String Total = lastPage.text().replace("/ ","");
            int total = Integer.parseInt(Total);//类型转换
            int number = 1;
            for (number = 1; number <= total; number++) {
                //https://www.wuage.com/list/lengyabanjuan-000m-0000-0000-0000-000-0000-page1.html?psa=W2.a531.a108.163
                String link =baseUrl + href.replace("page2","page"+number);//拼接链接地址
                productList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 解析成 document 获取每个产品页面的url地址
     */
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "钢材市场");
            if (null != html) {
                Thread.sleep(SleepUtils.sleepMin());
                Document parse = Jsoup.parse(html);
                Elements elements = parse.select("li.fe-col > p > a");
                for (Element element : elements) {
                    String href = element.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl-Product", href);
                }

            } else {
                LOGGER.info("page null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
