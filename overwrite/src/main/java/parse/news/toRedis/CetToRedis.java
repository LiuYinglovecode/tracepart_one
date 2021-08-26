package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CetToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CetToRedis.class);
    private static String links = new String("http://www.cet.com.cn");

    public static void main(String[] args) {
        CetToRedis cetToRedis = new CetToRedis();
        cetToRedis.homepage("http://www.cet.com.cn/");
    }


    public void homepage(String url) {
        try {
                String html = HttpUtil.httpGetwithJudgeWord(url, "cet");
                Document document = Jsoup.parse(html);
                Elements page = document.select("td[valign=bottom] > a");
                for (Element element : page) {
                    if (element.text().contains("财 经") || element.text().contains("科技")) {
                        String href = links.concat(element.attr("href"));
                        more(href);
                    }
                }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

        }

    }

    private void more(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "cet");
            Document document = Jsoup.parse(html);
            Elements page = document.select("td[valign=top] > div > a,#demo > a");
            for (Element element : page) {
                if (element.text().contains("商界纵横")
                        || element.text().contains("公司管理")
                        || element.text().contains("经济政策")
                        || element.text().contains("民 企")
                        || element.text().contains("国 企")
                        || element.text().contains("宏 观")
                        || element.text().contains("更多")) {
                    String url = links.concat(element.attr("href"));
                    newsList(url);
                    paging(url);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String url) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cet");
            Document document = Jsoup.parse(html);
            Elements nextPage = document.select("span.fc_ch1");
            if (!nextPage.isEmpty()) {
                String text = nextPage.eq(1).text();
                for (int i = 2; i <=Integer.parseInt(text) ; i++) {
                    String href = url.replace(".shtml", "").concat("_").concat(String.valueOf(i).concat(".shtml"));
                    newsList(href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());

        }
    }


    private void newsList(String url) {
        try {
            String cet = HttpUtil.httpGetwithJudgeWord(url, "cet");
            Document document = Jsoup.parse(cet);
            Elements elements = document.select("td.font_05 > a");
            for (Element element : elements){
                String attr = links.concat(element.attr("href"));
                RedisUtil.insertUrlToSet("toCatchUrl",attr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
