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

public class ShopCompanyToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiToRedis.class);

    public void category(String url) {
        try {
            int number;
            int total = 500;
            String html = HttpUtil.httpGetwithJudgeWord(url, "99114");
            Thread.sleep(SleepUtils.sleepMax());
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("td.td_p > a");
            for (Element e : select) {
                String href = e.attr("href");
                for (number = 1; number < total + 1; number++) {
                    String s = "_" + String.valueOf(number);
                    String nextPage = href.replace("_1", s);
                    companyList(nextPage);
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "99114");
            Thread.sleep(SleepUtils.sleepMax());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements select = document.select("#footerTop > ul > li > a");
                if (null != select) {
                    for (Element link : select) {
                        if (link.text().contains("公司")||link.text().contains("厂")||link.text().contains("集团")
                                ||link.text().contains("有限")||link.text().contains("经营部")) {
                            String href = link.attr("href");
                            RedisUtil.insertUrlToSet("toCatchUrl-Company", href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
