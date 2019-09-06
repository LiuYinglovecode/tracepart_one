package parse.product.toRedis;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EhsyProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(EhsyProductToRedis.class);


    //首页
    public void productPage(String url) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            Elements select = document.select("span.li-right-title > a");
            for (Element e : select) {
                String href = e.attr("href");
//                System.out.println(href);
                paging(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String attr) {
        try {
            HtmlUnitUnits htmlUnitUnits = HtmlUnitUnits.getInstance();
            htmlUnitUnits.setTimeout(30000);
            htmlUnitUnits.setWaitForBackgroundJavaScript(30000);
            Document document = htmlUnitUnits.getHtmlPageResponseAsDocument(attr);
            int i = Integer.parseInt(document.select("div.page-count > span.fullPage").text());
            for (int j = 1; j <= i; j++) {
                String s = attr + "?p=" + j;
                Document htmlPageResponseAsDocument = htmlUnitUnits.getHtmlPageResponseAsDocument(s);
                Elements elements = htmlPageResponseAsDocument.select("div.p-name > a");
                for (Element element : elements) {
                    RedisUtil.insertUrlToSet("toCatchUrl-Product",element.attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
