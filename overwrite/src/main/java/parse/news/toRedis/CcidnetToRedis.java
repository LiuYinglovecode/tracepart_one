package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class CcidnetToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CcidnetToRedis.class);

    /**
     * 获取到分类列表，拿到需要的分类url。
     * @param url
     */
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("li.a2 > div.menu_div2 > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    paging(href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void paging(String href) {
        String html = HttpUtil.httpGetwithJudgeWord(href, "ccidnet");
        Document document = Jsoup.parse(html);
        String attr = document.select(" div.fy > a").last().previousElementSibling().attr("href");
        String s = attr.split("page=")[1];
        int number = Integer.parseInt(s);
        int pages = 1;
        for (pages = 1; pages <= number; pages++) {
            String value = String.valueOf(pages);
            String links = attr.replace(s, value);
            newsList(links);
        }
    }


    /**
     * 获取每页新闻列表，拿到每个新闻页面的url链接，
     * 将url链接放到redis中
     * @param url
     */

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ccidnet");
            if (null!=html) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("div.plist11_p.F_Left > h2 > a");
                if (0!=select.size()) {
                    for (Element e : select) {
                        String link = e.attr("href");
                        System.out.println(link);
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
