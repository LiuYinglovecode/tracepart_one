package parse.product.toRedis;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class JdzjProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(JdzjProductToRedis.class);
    private static String links = new String("https://www.jdzj.com");

    public static void main(String[] args) {
        JdzjProductToRedis jdzjProductToRedis = new JdzjProductToRedis();
        jdzjProductToRedis.productPage("https://www.jdzj.com/chanpin.html");
    }

    public void productPage(String url) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            Elements select = document.select("div.listLeft > p > a");
            for (Element element : select) {
                String link = links.concat(element.attr("href"));
//                System.out.println(link);
                nextPage(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void nextPage(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "jdzj");
            Document document = Jsoup.parse(html);
            String total = document.select("div.n-right.fr > span > em").text();
            int i =(int) Math.ceil(Double.parseDouble(total) / 10);
            String attr = document.select("div.bottom > div > a").first().attr("href").split("p")[1];
            for (int page = 1; page <= i; page++) {
                String href = links.concat("/chanp").concat(attr).concat("p").concat(String.valueOf(page)).concat(".html");
//                System.out.println(href);
                productlist(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    private void productlist(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "jdzj");
            if (html!=null) {
                Document document = Jsoup.parse(html);
                Elements doc = document.select("#cpListUl > li > a:nth-child(3)");
                for (Element element : doc) {
                    String productInfoLink = links.concat(element.attr("href"));
                    RedisUtil.insertUrlToSet("toCatchUrl-Product", productInfoLink);

                }
            }
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
