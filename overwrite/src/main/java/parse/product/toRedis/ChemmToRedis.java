package parse.product.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.net.URL;

public class ChemmToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChemmToRedis.class);
    private static String baseUrl = "http://www.chemm.cn";

    //首页
    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chemm");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            Elements select = parse.select("a.dLinkFont");
            for (Element e : select) {
                String href = baseUrl + e.attr("href");
                String trade_category = e.text().trim();
                productList(href, trade_category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //产品列表及分页
    private void productList(String url, String trade_category) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chemm");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);
            Elements select = parse.select("li.ProListMainTitle span a.bBoldLinkFont");
            for (Element e : select) {
                String href = baseUrl + e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", href);
            }
            Elements nextPage = parse.select(" a.pagelink");
            for (Element element : nextPage) {
                if (element.text().contains("下一页")) {
                    String href = baseUrl + element.attr("href");
                    System.out.println("下一页：" + href);
                    productList(href, trade_category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
