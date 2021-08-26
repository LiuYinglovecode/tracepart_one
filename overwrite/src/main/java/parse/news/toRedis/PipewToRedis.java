package parse.news.toRedis;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class PipewToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipewToRedis.class);
    private static final String link = new String("http://www.pipew.com");

    public static void main(String[] args) {
        PipewToRedis pipewToRedis = new PipewToRedis();
        pipewToRedis.homepage("http://www.pipew.com/news/index.asp");
    }
    /**
     * 新闻首页：解析页面，拿到新闻分类的url
     *
     * @param url
     */
    public void homepage(String url) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
                Elements categoryList = document.select("div.newslist > table > tbody > tr > td > a");
                for (Element e : categoryList) {
                    String href =link.concat(e.attr("href").replace("..", ""));
                    more(href);
                }
            } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 分页：获取到总页数，对url进行拼接，得到完整的下一页新闻列表url。
     *
     * @param url
     */
    private void more(String url) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            String elements = document.select("div.newslist > div > a").last().attr("href");
            int tailPage = Integer.parseInt(elements.split("page=")[1]);
            int number;
            for (number = 1; number <= tailPage; number++) {
                String nextPage = elements.replace(String.valueOf(tailPage), String.valueOf(number));
//                    System.out.println(nextPage);
                newsList(nextPage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 新闻列表 ：解析网页一次获取全部想要的新闻信息url
     *
     * @param url
     */
    private void newsList(String url) {
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            Elements title = document.select("a.link-news");
            if (!title.isEmpty()) {
                for (Element element : title) {
                    RedisUtil.insertUrlToSet("toCatchUrl", link.concat("/news/").concat(element.attr("href")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
