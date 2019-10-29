package parse.product.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class Net114ProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(Net114ProductToRedis.class);

    //首页
    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网络114");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div:nth-child(4) > div > div:nth-child(1) > div.box-title1 > span > a,div:nth-child(4) > div:nth-child(1) > div.col-md-6.center-bg.m-r10 > div > div > div.col-md-4.box-bg > div > div.picfont1 > p > a,div:nth-child(8) > div:nth-child(1) > div.col-md-6.center-bg.m-r10 > div > div > div.col-md-4.box-bg1 > div > div.picfont1 > p > a");
            for (Element e : select){
                String href = e.attr("href");
                classify(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void classify(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "网络114");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.cation-title-1 > div > a");
            for (Element e : select) {
                String attr = e.attr("href");
                paging(attr);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String url) {
        try {
            String replace = url.replace(".html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "网络114");
            Document parse = Jsoup.parse(html);
            Element lastPage = parse.select("ul.pagination.pageul li a").last();
            String Total = lastPage.attr("href").split("-p-")[1].replace(".html","");
            int total = Integer.valueOf(Total).intValue();//类型转换
            int number = 1;
            for (number = 1; number <= total; number++) {
                String link = replace + "-p-" + number + ".html";//拼接链接地址
                productList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //产品列表
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "网络114");
            Document parse = Jsoup.parse(html);
            Elements elements = parse.select("a.offer-title");
            for (Element element : elements) {
                String href = element.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Product",href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
