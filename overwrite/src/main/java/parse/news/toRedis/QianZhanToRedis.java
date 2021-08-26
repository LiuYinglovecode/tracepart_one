package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class QianZhanToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(QianZhanToRedis.class);

    public static void main(String[] args) {
        QianZhanToRedis qianZhanToRedis = new QianZhanToRedis();
        qianZhanToRedis.homepage("https://www.qianzhan.com/indynews/");
    }
    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "qianzhan");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.clearfix > li > a");
                for (Element e : categoryList) {
                    if (!e.attr("href").contains("people")
                            && !e.attr("href").contains("baike")
                            && !e.attr("href").contains("bg")
                            && !e.text().contains("首页")) {
                        String href = e.attr("href");
                        if (!href.contains("https:")){
                            String s = "https:" + href;
                            paing(s);
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
            String html = HttpUtil.httpGetwithJudgeWord(href, "关于我们");
            Document document = Jsoup.parse( html);
            String pageCount = (document.select("div.page > span")
                    .text()
                    .split(" 条")[0]
                    .replace("共 ", ""));
            int pages = (int) Math.ceil(Integer.parseInt(pageCount)/13);
            int i;
            for (i = pages; i >= 1; i--) {
                String links = href.replace(".html","").concat("-").concat(String.valueOf(i)).concat(".html");
                newsList(links);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "qianzhan");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("#ulNewsList > li > dl > dt > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        RedisUtil.insertUrlToSet("toCatchUrl", ("https:"+e.attr("href")));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
