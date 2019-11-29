package parse.company.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D17ToRedis {
     private final static Logger LOGGER = LoggerFactory.getLogger(D17ToRedis.class);

    public static void main(String[] args) {
        D17ToRedis d17ToRedis = new D17ToRedis();
        d17ToRedis.guangdong("http://company.d17.cc/");
    }

    /**
     * @param url
     */
    public void guangdong(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "付款方式");
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(html);
            Elements address = document.select("#cmy_dq .cmy_content ul li a");
            for (Element e : address) {
                String addressList = e.attr("href");
                paging(addressList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String url){
        try {
            String html = HttpUtil.httpGetWithProxy(url, "付款方式");
            Thread.sleep(SleepUtils.sleepMin());
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.productlist_page");
            select.select("a").remove();
            String s = select.text().split("/")[1].replace("页", "");

            for (int i = 1; i <= Integer.parseInt(s); i++) {
                String concat = String.valueOf(i).concat(".html");
                String links = url.replace("1.html", concat);
                company(links);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void company(String url) {
        try {

            String html = HttpUtil.httpGetWithProxy(url, "付款方式");
            Thread.sleep(SleepUtils.sleepMin());
            Document document = Jsoup.parse(html);
            Elements companyList = document.select(".companylist_style ul .clr .name.clr a:first-child");
            for (Element e : companyList) {
                String companyUrl = e.attr("href") + "/introduce.html";
                RedisUtil.insertUrlToSet("toCatchUrl-Company",companyUrl);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

}
