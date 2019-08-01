package parse.company.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class QiyiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiyiToRedis.class);

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "71.net");
            Document parse = Jsoup.parse(html);
            Elements page = parse.select("div.ui-page-num a");
            String replace = "http://supplier.71.net" + page.last().attr("href")
                    .replace("2.html", "");
            String Total = page.last().previousElementSibling().text();
            int total = Integer.valueOf(Total).intValue();
            int number = 1;
            for (number = 1; number <= total; number++) {
                String link = replace + number + ".html";//拼接链接地址
                newsList(link);
            }
            LOGGER.info("supplier.71.net  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * 工具方法：解析到url，放入队列
     *
     * @param link:页面链接
     */
    private void newsList(String link) {
        try {
            String indexHtml = HttpUtil.httpGetwithJudgeWord(link, "71.net");
            if (indexHtml != null) {
                Document indexDoc = Jsoup.parse(indexHtml);
                Elements select = indexDoc.select("div.inside_box ul li a");
                for (Element se : select) {
                    if (!se.text().contains("药") || !se.text().contains("烟") || se.text().contains("赌博") || !se.text().contains("水")) {
                        RedisUtil.insertUrlToSet("toCatchUrl-Company", se.attr("href"));
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
