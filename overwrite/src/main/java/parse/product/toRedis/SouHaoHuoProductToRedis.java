package parse.product.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SouHaoHuoProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(SouHaoHuoProductToRedis.class);

    public static void main(String[] args) {
        SouHaoHuoProductToRedis souHaoHuoProductToRedis = new SouHaoHuoProductToRedis();
        souHaoHuoProductToRedis.productPage("https://www.912688.com/chanpin/");
    }
    //首页
    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "搜好货");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.base-div.jc > dl > dd > a");
            for (Element e : select){
                String href = e.attr("href");
                classify(href);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void classify(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "搜好货");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.list_layout_filter > ul > li > a");
            for (Element e : select) {
                String attr = e.attr("href");
                productList(attr);
                paging(attr);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String attr) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(attr, "搜好货");
            Document parse = Jsoup.parse(html);
            Element lastPage = parse.select("div.s-mod-page.mb30 > a").last().previousElementSibling();
            int total = Integer.parseInt(lastPage.text().trim());//类型转换
            int number;
            for (number = 2; number <= total; number++) {
                String link = lastPage.attr("href").replace(String.valueOf(total),String.valueOf(number));//拼接链接地址
                productList(link);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //产品列表
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "搜好货");
            Thread.sleep(2000);
            if (null!=html) {
                Document parse = Jsoup.parse(html);
                Elements elements = parse.select("div.product-left-new.clearfix > ul > li > div.detailed > p.clear.h40 > a");
                for (Element element : elements) {
                    String href = element.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl-shhProduct", href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
