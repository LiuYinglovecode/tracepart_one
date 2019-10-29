package parse.company.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class YellowurlToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiToRedis.class);

    public void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国黄页网");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.cell ul.typical li a");
            for (Element e : select) {
                String href = e.attr("href");
                nextpage(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextpage(String href) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(href, "中国黄页网");
            assert html != null;
            Document document = Jsoup.parse(html);
            String Total = document.select("div.pages cite").text()
                    .split("/")[1].replace("页","");
            int total = Integer.valueOf(Total).intValue();
            for (number = 1; number < total + 1; number++) {
                String nextPage = href + number+"/index.html";
                list.add(nextPage);
            }
            for (String link : list) {
                System.out.println("下一页：" + link);
                companyList(link);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国黄页网");
            Document document = Jsoup.parse(html);
            Elements select = document.select("h2.title a.comtitle");
            for (Element link : select) {
                String href = link.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Company", href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


}
