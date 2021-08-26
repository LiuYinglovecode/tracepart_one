package parse.product.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class EbdoorProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(EbdoorProductToRedis.class);

    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("dd.LddList ul li");
            for (Element e : select){
                e.select("b").remove();
                String href = e.select("a").attr("href");
                paging(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //分页
    private void paging(String url) {

        try {
            String link = url.replace("1.aspx", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            String pagesNumber  = parse.select("#PageBreak_2").text();
            String totals = pagesNumber.replace("共", "").replace("页", "");
            int total = Integer.parseInt(totals);
            int number ;
            for (number = 1; number <= total; number++) {
                String nextPage = link + number + ".aspx";
                productLink(nextPage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //产品列表
    private void productLink(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("li.Rk_Cont1 dl dd a");
            for (Element e : select){
                String href = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Product", href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
