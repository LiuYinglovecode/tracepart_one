package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdacnToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdacnToRedis.class);
    private static final String links = "http://www.idacn.org" ;

    public static void main(String[] args) {
        IdacnToRedis idacnToRedis = new IdacnToRedis();
        idacnToRedis.paging("http://www.idacn.org/news");
    }

    //分页
    public void paging(String url) {
        try {
            String s = HttpUtil.httpGetwithJudgeWord(url, "idacn");
            Document document = Jsoup.parse(s);
            Elements elements = document.select("a.end");
            String text = elements.text();
            String attr = elements.attr("href");
            for (int i = 1; i <=Integer.parseInt(text) ; i++) {
                String link = links.concat(attr.replace(text, String.valueOf(i)));
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "idacn");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("div.clearfix.mt-20.newsList > div > div > h4 > a");
                for (Element e : select) {
                    String link = links.concat(e.attr("href"));
                    RedisUtil.insertUrlToSet("toCatchUrl", link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
