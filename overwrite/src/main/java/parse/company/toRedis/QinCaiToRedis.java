package parse.company.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class QinCaiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QinCaiToRedis.class);

    public void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "每日更新");
//            Thread.sleep(SleepUtils.sleepMin());
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("span.category-name > a");
            for (Element e : select) {
//                String href = e.attr("href");
                String text = e.text();
                nextpage(e.attr("href"),text);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextpage(String href,String text) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(href, "报告错误");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                String page = document.select("span.total").text().replace("共", "").replace("页", "");
                if (!page.isEmpty()) {
                    int total = Integer.parseInt(page);
                    for (int number = 1; number <= total; number++) {
                        String s = href + "index-p" + number + ".html";
                        companyList(s);
                    }
                }
            }else {
                LOGGER.error("解析异常1"+href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "用户协议");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
//                System.out.println(document);
                Elements select = document.select("div.itemlist > h2 > a");
                if (!select.isEmpty()) {
                    for (Element link : select) {
                        String href = link.attr("href");
//                            System.out.println(href);
                        RedisUtil.insertUrlToSet("toCatchUrl-Company", href);
                    }
                }
            }else {
                LOGGER.error("解析异常2"+url);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
