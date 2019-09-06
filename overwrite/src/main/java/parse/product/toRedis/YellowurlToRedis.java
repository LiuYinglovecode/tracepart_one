package parse.product.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class YellowurlToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(YellowurlToRedis.class);

    //首页
    public void productPage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord (url, "黄页网");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.detail a");
            for (Element e : select){
                String href = e.attr("href");
                double random = Math.random() * 10000;
                if (5000<=random) {
                    Thread.sleep((int) (random));
                    paging(href);
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

//    private void classify(String href) {
//        try {
//            String html = HttpUtil.httpGetwithJudgeWord(href, "黄页网");
//            Document document = Jsoup.parse(html);
//            Elements select = document.select("ul#classifyList.classifyList li a");
//            for (Element e : select) {
//                String attr = e.attr("href");
//                double random = Math.random() * 10000;
//                if (5000<=random) {
//                    Thread.sleep((int) (random));
//                    paging(attr);
//                }
//
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//        }
//    }

    //分页。获取到总页数，拼接出下一页的链接地址。
    private void paging(String attr) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(attr, "黄页网");
            Document parse = Jsoup.parse(html);
            Elements lastPage = parse.select("cite");
            String Total = lastPage.text().split("/")[1].replace("页","");
            int total = Integer.valueOf(Total).intValue();//类型转换
            int number = 1;
            for (number = 1; number <= total; number++) {
                String link = attr + number + "/index.html";//拼接链接地址
                double random = Math.random() * 10000;
                if (5000<=random) {
                    Thread.sleep((int) (random));
                    productList(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //产品列表
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "黄页网");
            if (null!=html) {
            Document parse = Jsoup.parse(html);
                Elements elements = parse.select("h2.title a.comtitle");
                for (Element element : elements) {
                    String href = element.attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl-Product", href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
