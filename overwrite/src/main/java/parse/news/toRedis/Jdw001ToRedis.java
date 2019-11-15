package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jdw001ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Jdw001ToRedis.class);
    private static String link = new String("http://www.jdw001.com/");

    public static void main(String[] args) {
        Jdw001ToRedis Jdw001ToRedis = new Jdw001ToRedis();
        Jdw001ToRedis.homepage("http://www.jdw001.com/portal.php");

    }

    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jdw001");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.head_ul > li > a");
                if (!categoryList.isEmpty()) {
                    for (Element e : categoryList) {
                        if (!e.text().contains("人物访谈")
                                &&!e.text().contains("名人风采")
                                &&!e.text().contains("专 题")
                                &&!e.text().contains("上游部件")
                                &&!e.text().contains("数据报告")
                                &&!e.text().contains("营销渠道")
                                &&!e.text().contains("产品库")
                                &&!e.text().contains("论 坛")) {
                            String href = e.attr("href");
//                            System.out.println(href);
                            paing(href);
                        }
                    }
                }
            } else {
                LOGGER.info("null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paing(String href) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(href, "jdw001");
            Document document = Jsoup.parse(html);
            Elements tailPage = document.select("#ct > div.mn > div.pgs.cl > div > a.last");
            String pageCount = tailPage.text().replace("... ","");
            String attr = tailPage.attr("href");
            int i;
            for (i = 1; i <= Integer.parseInt(pageCount); i++) {
                String replace = attr.replace(pageCount, String.valueOf(i));
                newsList(replace);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jdw001");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.bm_c.xld > dl > dt > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        String href = link.concat(e.attr("href"));
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
