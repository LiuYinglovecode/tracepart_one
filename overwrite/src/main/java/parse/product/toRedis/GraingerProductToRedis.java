package parse.product.toRedis;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraingerProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(GraingerProductToRedis.class);
    private static String baseUrl = "https://www.grainger.cn";


    //首页
    public void productPage(String url) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            Elements list = document.select("div.dd > dl > dd > a");
            for (Element href : list) {
                String link =baseUrl + href.attr("href");
                paging(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String link) {
        try {
            HtmlUnitUnits htmlUnitUnits = HtmlUnitUnits.getInstance();
            htmlUnitUnits.setTimeout(30000);
            htmlUnitUnits.setWaitForBackgroundJavaScript(30000);
            Document document = htmlUnitUnits.getHtmlPageResponseAsDocument(link);
            int i = Integer.parseInt(document.select("div.type_paix > div > label").last().text());
            for (int j = 1; j <= i; j++) {
                String s = link + "?page=" + j;
                Document htmlPageResponseAsDocument = htmlUnitUnits.getHtmlPageResponseAsDocument(s);
                Elements elements = htmlPageResponseAsDocument.select("div.proUL > ul > li > a");
                for (Element element : elements) {
                    String href = baseUrl + element.attr("href");
//                    System.out.println(href);
                    RedisUtil.insertUrlToSet("toCatchUrl-Product",href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
