package parse.news.toRedis;

import Utils.HtmlUnitUnits;
import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CeeiaToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CeeiaToRedis.class);
    private static String links = new String("http://www.ceeia.com");

    public static void main(String[] args) {
        CeeiaToRedis ceeiaToRedis = new CeeiaToRedis();
        ceeiaToRedis.homepage("http://www.ceeia.com/Index.aspx");

    }


    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ceeia");
//            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsList = document.select("div.index_mainnav > a");
                if (!newsList.isEmpty()) {
                    for (Element e : newsList) {
                        if (e.text().contains("综合新闻")) {
                            String href = links.concat(e.attr("href").replace("..",""));
//                            System.out.println(href);
                            paging(href);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String href) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(href);
            Element select = document.select("#Pagination > a.current").last();

            String tailPage = select.attr("href");
            String pages = select.attr("href").split("page=")[1];
            for (int i = 0; i <= Integer.parseInt(pages); i++) {
                String link = links.concat("/News_List.aspx").concat(tailPage.replace(pages, String.valueOf(i)));
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


    }

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ceeia");
            Thread.sleep(SleepUtils.sleepMin());
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("ul.news_ri_list > li > span.news_title > a");
                if (!newsListInfo.isEmpty()) {
                    for (Element e : newsListInfo) {
                        String href = links.concat(e.attr("href"));
                        RedisUtil.insertUrlToSet("toCatchUrl", href);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
