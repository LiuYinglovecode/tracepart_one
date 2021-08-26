package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class QlmoneyToRedis {

    private static final Logger LOGGER = LoggerFactory.getLogger(QlmoneyToRedis.class);
    private static String baseUrl = "http://www.qlmoney.com";

    public static void main(String[] args) {
        QlmoneyToRedis qlmoneyToRedis = new QlmoneyToRedis();
        qlmoneyToRedis.homepage("http://www.qlmoney.com");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "qlmoney");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nav > ul > li > a");
                for (Element e : categoryList) {
                    if (e.text().contains("要闻")||e.text().contains("城市")||e.text().contains("科技")||e.text().contains("能源")||e.text().contains("普惠金融")) {
                        String href = e.attr("href");
                        paging(href);
                    } else if (e.text().contains("经济")||e.text().contains("理财")){
                        String href = e.attr("href");
                        more(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void more(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "qlmoney");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("div.more_read > a");
                for (Element element : detailList) {
                    String attr = element.attr("href");
                    paging(attr);
                }
            } else {
                LOGGER.info("该页面为空");
            }
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
    }

    private void paging(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "qlmoney");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Element detailList = document.select("#list_c > div.main_box.text_c.pages > a").last().previousElementSibling();
                int pageCount = Integer.parseInt(detailList.text());
                for (int i = 0; i <=pageCount ; i++) {
                    String concat = href.concat(String.valueOf(i)).concat(".html");
                    listNews(concat);
                }
            } else {
                    LOGGER.info("该页面为空");
                }

        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }

    }
    private void listNews(String link) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(link, "qlmoney");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("#list_c > div.main_box.news > ul > li > a");
                if (0 != detailList.size()) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl", (e.attr("href")));
                    }
                } else {
                    LOGGER.info("该页面为空");
                }
            }
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
    }
}