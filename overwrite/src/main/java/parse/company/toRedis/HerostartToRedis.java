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

import java.util.ArrayList;

public class HerostartToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiToRedis.class);

    public static void main(String[] args) {
        HerostartToRedis herostartToRedis = new HerostartToRedis();

        herostartToRedis.category("http://www.herostart.com/");
    }
    public void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "herostart");
            Thread.sleep(SleepUtils.sleepMin());
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.bbody.li_dot.fontgray > ul > li > span > a");
            for (Element e : select) {
                String href = e.attr("href");

                classification(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void classification(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "herostart");
            Thread.sleep(SleepUtils.sleepMin());
            if (null!=html) {
                Document document = Jsoup.parse(html);
                Elements select = document.select("div.chcat > div > a");
                for (Element e : select) {
                    String link = e.attr("href");
                    nextpage(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextpage(String href) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(href, "herostart");
            Thread.sleep(SleepUtils.sleepMin());
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements pageCount = document.select("div.pages > cite");
                if (0 != pageCount.size()) {
                    int total = Integer.parseInt(pageCount.text().split("/")[1].replace("页", ""));
                    for (number = 1; number <= total; number++) {
                        String nextPage = href.replace(".html", "") + "-pn" + number + ".html";
                        list.add(nextPage);
                    }
                } else {
                    companyList(href);
                }
                for (String link : list) {
                    companyList(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "herostart");
//            Thread.sleep(SleepUtils.sleepMin());
            if (null!=html) {
                Document document = Jsoup.parse(html);
                Elements select = document.select("td[align=left] > ul > li > a");
                if (0!=select.size()) {
                    for (Element link : select) {
                        String href = link.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl-Company", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
