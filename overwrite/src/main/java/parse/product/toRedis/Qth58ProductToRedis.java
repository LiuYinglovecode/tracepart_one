package parse.product.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;


public class Qth58ProductToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(Qth58ProductToRedis.class);
    private static String baseUrl = "http://www.qth58.cn/";



    public static void main(String[] args) {
        Qth58ProductToRedis qth58ProductToRedis = new Qth58ProductToRedis();
        qth58ProductToRedis.homePage("http://www.qth58.cn/product/");
    }

    public void homePage(String url) {
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("li.cate_sec_term a");
            for (Element element : select) {
                String link = element.attr("href");
                nextPage(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextPage(String url) {
        String replace = url.replace("http://www.qth58.cn/", "").replace("/","");
        int page = 1;
        for (page = 1; page < 2500; page++) {
            String link = baseUrl + replace + "-p" + page;
            productlist(link);
        }

    }

    private void productlist(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "产品库");
            if (html!=null) {
                Document document = Jsoup.parse(html);
                Elements doc = document.select("a.comtitle");
                for (Element element : doc) {
                    String productInfoLink = element.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl-Product",productInfoLink);
                }
            }else {
                LOGGER.info("网页不存在");
            }
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
