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

public class ChemmProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChemmProductToRedis.class);
    private static String baseUrl = "http://www.chemm.cn";

    //首页
    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chemm");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("a.dLinkFont");
            for (Element e : select) {
                String href = baseUrl + e.attr("href");
                productList(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //产品列表及分页
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chemm");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("li.ProListMainTitle span a.bBoldLinkFont");
            for (Element e : select) {
                String href = baseUrl + e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Product", href);
            }
            Elements nextPage = parse.select(" a.pagelink");
            for (Element element : nextPage) {
                if (element.text().contains("下一页")) {
                    String href = baseUrl + element.attr("href");
                    productList(href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
