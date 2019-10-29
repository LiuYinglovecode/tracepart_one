package parse.company.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class Net114ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Net114ToRedis.class);

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "net114");
            Document parse = Jsoup.parse(html);
            if (parse!=null) {
                Elements link = parse.select("ul.enterprise-text-li li p a");
                for (Element element : link) {
                    String attr = element.attr("href");
                    nextPage(attr);
                }
            }else {
                LOGGER.info("页面为空！");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextPage(String attr) {
        try {
            String replace = attr.replace(".html", "");
            String html = HttpUtil.httpGetwithJudgeWord(attr, "net114");
            Document document = Jsoup.parse(html);
            Elements elements = document.select("ul.pagination.pageul li a");
            if (elements.size()!=0) {
                String href = elements.last().attr("href");
                String Total = href.split("-p-")[1].replace(".html", "");
                int total = Integer.valueOf(Total).intValue();//类型转换
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String link = replace + "-p-" + number + ".html";//拼接链接地址
                    companyList(link);
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void companyList(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "net114");
            Document document = Jsoup.parse(html);
            Elements elements = document.select("a.corp-title");
            for (Element element : elements) {
                String link = element.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Company", link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
