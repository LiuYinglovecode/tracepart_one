package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class Spsb114ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Spsb114ToRedis.class);
    private static String baseUrl = "http://www.spsb114.com";

    public static void main(String[] args) {
        Spsb114ToRedis spsb114ToRedis = new Spsb114ToRedis();
        spsb114ToRedis.homepage("http://www.spsb114.com/tech/");
    }
    //首页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "食品设备网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("td table tbody tr td a.blue");
                for (Element e : categoryList) {
                    String link = baseUrl + e.attr("href");
                    newsList(link);
//                    paging(link);

                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


/*    //分页
    private void paging(String url) {

        String replace = url.replace("http://www.spsb114.com/news/news_list_",
                "").replace(".html","");
        try {
            ArrayList<String> list = new ArrayList<>();
            String html = HttpUtil.httpGetwithJudgeWord(url, "食品设备网");
            Document document = Jsoup.parse(html);
            String Total = document.select("table.membertable_page tbody tr td[align]").last().text().replace("1 / ","");
            int total = Integer.valueOf(Total).intValue();//转行类型
            int number = 1;
            for (number = 1; number <= total ; number++) {
                String nextPage = "http://www.spsb114.com/news/news_list"+".php?gopage="+number+"&news_sort_id="+replace;
                list.add(nextPage);
            }
            for (String link : list) {
                newsList(link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }*/

    //新闻列表
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "食品设备网");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("a.zixun");
            if (newsListInfo!=null) {
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                        String s = baseUrl+ "/tech/" + href;
                        RedisUtil.insertUrlToSet("toCatchUrl", s);
                }
            }

            Elements elements = document.select("table.membertable_page > tbody > tr > td > a");
            for (Element element : elements) {
                if (element.text().contains("下一页")){
                    String attr = element.attr("href");
                    newsList(attr);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
