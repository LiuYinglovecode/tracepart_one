package parse.product.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class MaiProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(MaiProductToRedis.class);


    public static void main(String[] args) {
        MaiProductToRedis maiProductToRedis = new MaiProductToRedis();
        maiProductToRedis.productPage("http://www.86mai.com/sell/");
    }
    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("a.px16");
            for (Element e : select){
                String href = e.attr("href");
                more(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void more(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements elements = parse.select("div.m2l > div:nth-child(1) > div.sort-v > ul > li > a");
            for (Element element : elements) {
                String href = element.attr("href");
                mores(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void mores(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements elements = parse.select("div.m2l > div:nth-child(1) > div.sort-v > ul > li > a");
            for (Element element : elements) {
                String href = element.attr("href");
                paging(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

        }
    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String url) {
        try {
            String replace = url.replace(".html", "");
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            String pagesNumber = parse.select("div.pages cite").text().split("/")[1].replace("页","");//获取总结数
            int total = Integer.valueOf(pagesNumber).intValue();//类型转换
            int number = 1;
            for (number = 1; number <= total; number++) {
                String link = replace + "_" + number + ".html";//拼接链接地址
                productList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

        }
    }

    //产品列表
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "网站首页");
            Document parse = Jsoup.parse(html);
            Elements elements = parse.select("div.list table tbody tr td div a");
            for (Element element : elements) {
                String href = element.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Product",href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

        }
    }
}
