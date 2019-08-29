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


public class QiYeKuToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiYeKuToRedis.class);

    public void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "企业库");
            Thread.sleep(SleepUtils.sleepMin());
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("#m_page > div > a");
            for (Element e : select) {
                String href ="http://www.qiyeku.com" + e.attr("href");
                nextpage(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextpage(String href) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(href, "企业库");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                String page = document.select("#m_page > table > tbody > tr > td > div > span").text()
                        .split("条记录 ")[1].split(" 页")[0];
                if (null != page) {
                    int total = Integer.parseInt(page);
                    for (int number = 1; number <= total ; number++) {
                        String s = href + "/all/all/" + number;
                        companyList(s);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "企业库");
            Thread.sleep(SleepUtils.sleepMin());
            if (null!=html) {
                Document document = Jsoup.parse(html);
//                System.out.println(document);
                Elements select = document.select("table.c_list > tbody > tr > td > a");
                if (0 != select.size()) {
                    for (Element link : select) {
                        if (link.text().contains("查看详细信息")) {
                            String href ="http://www.qiyeku.com" + link.attr("href");
//                            System.out.println(href);
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
