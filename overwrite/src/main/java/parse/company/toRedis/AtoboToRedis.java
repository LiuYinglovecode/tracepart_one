package parse.company.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class AtoboToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QinCaiToRedis.class);
    private static String webPage = new String("http://www.atobo.com.cn");

    public static void main(String[] args) {

        AtoboToRedis AtoboToRedis = new AtoboToRedis();
        AtoboToRedis.productNavigationBar("http://www.atobo.com.cn/Companys/");

    }

    public void productNavigationBar(String url) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "阿土伯");
            if (html!=null) {
                Document doc = Jsoup.parse(html);
                Elements elements = doc.select("div.filterMode div.filterlist ul li.alist div ul li a");
                for (Element element : elements) {
                    String href = element.attr("href");
                    String link = webPage.concat(href);
                    Thread.sleep(SleepUtils.sleepMin());
                    reClassification(link);
                }
            } else {
                LOGGER.info("网页连接失败");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void reClassification(String link1) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link1, "阿土伯");
            if (html!=null) {
                Document doc2 = Jsoup.parse(html);
                Elements elements3 = doc2.select("#filterArea ul li a");
                for (Element element3 : elements3) {
                    //最终按照地区分类进入公司主页面
                    String href2 = element3.attr("href");
                    //System.out.println(href2);
                    String link2 = webPage.concat(href2);
                    Thread.sleep(SleepUtils.sleepMin());
                    more(link2);
                }
            } else {
                LOGGER.info("网页连接失败");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void more(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "阿土伯");
            if (html!=null) {
                Document doc = Jsoup.parse(html);
                Elements elements3 = doc.select("#filterArea ul li a");
                for (Element element3 : elements3) {
                    String href = element3.attr("href");
                    String links = webPage.concat(href);
                    Thread.sleep(SleepUtils.sleepMin());
                    mores(links);
                }
            } else {
                LOGGER.info("网页连接失败");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void mores(String links) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(links, "阿土伯");
            if (html!=null) {
                Document doc = Jsoup.parse(html);
                Elements elements3 = doc.select("#filterArea ul li a");
                for (Element element3 : elements3) {
                    String href = element3.attr("href");
                    String link = webPage.concat(href);
                    Thread.sleep(SleepUtils.sleepMin());
                    paging(link);
                }
            } else {
                LOGGER.info("网页连接失败");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "阿土伯");
            if (html!=null) {
                Document parse = Jsoup.parse(html);
                String pageNumber = parse.select("span.total")
                        .first()
                        .text()
                        .replace("共", "")
                        .replace("页", "")
                        .trim();
                if (pageNumber.equals("1")) {
                    productList(webPage.concat(link));
                } else {
                    String nextPage = parse.select("span.page_next.page-n > a")
                            .attr("href")
                            .replace("y2", "");
                    for (int i = 1; i <= Integer.parseInt(pageNumber); i++) {
                        String links = webPage.concat(nextPage.concat("y").concat(String.valueOf(i)));
                        Thread.sleep(SleepUtils.sleepMin());
                        productList(links);
                    }
                }
            } else {
                LOGGER.info("网页连接失败");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void productList(String link2) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link2, "阿土伯");
            if (html!=null) {
                Document parse = Jsoup.parse(html);
                Elements list = parse.select("li.product_box div ul li.p_name div ul li.pp_2web");
                for (Element element : list) {
                    //获取企业主页面
                    String href = element.select("a").first().attr("href");
                    RedisUtil.insertUrlToSet("toCatchUrl-Company", href);
                }
            } else {
                LOGGER.info("网页连接失败");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
