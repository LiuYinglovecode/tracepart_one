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
import java.util.HashSet;

public class HxnyToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(HxnyToRedis.class);
    private static final String links = "http://www.hxny.com";


    public static void main(String[] args) {
        HxnyToRedis hxnyToRedis = new HxnyToRedis();
        hxnyToRedis.homepage("http://www.hxny.com/guangfu/");
    }

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Thread.sleep(SleepUtils.sleepMin());

            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#newsTypeList > li > a");
                for (Element element : categoryList) {
                    String href = element.attr("href");
                    for (int i = 1; i <=3 ; i++) {
                        String link = links.concat(href).concat(String.valueOf(i));
                        newsList(link);
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //新闻列表
    private void newsList(String url) {
        HashSet<String> set = new HashSet<>();
        try {
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
//            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
//            if (null != html) {
//                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("#newsList > li > a");
                for (Element e : newsListInfo) {
                    if (e.attr("href").contains("nd-")) {
                        String href = links.concat(e.attr("href"));
                        set.add(href);
                    }
                }
//            }
            for (String link : set) {
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
