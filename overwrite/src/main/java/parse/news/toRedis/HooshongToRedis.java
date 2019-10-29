package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.net.URL;
import java.util.ArrayList;

public class HooshongToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(HooshongToRedis.class);

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "hooshong");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.box_body > table > tbody > tr > td > a");
                for (Element element : categoryList) {
                    if (element.text().contains("业界动态")||element.text().contains("分析预测")||element.text().contains("技术动态")||
                            element.text().contains("价格行情")||element.text().contains("企业新闻")||element.text().contains("贸易信息")||
                            element.text().contains("国际动态")||element.text().contains("政策信息")) {
                        String href = element.attr("href");
                        paging(href);
                    }
                }
            }
            LOGGER.info("HooshongToRedis  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        String replace = url.replace(".html", "");
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "hooshong");
            Document document = Jsoup.parse(html);
            String Total = document.select("div.m_l.f_l > div > div > div > a").last()
                    .attr("href").replace(replace,"")
                    .replace("-","").replace(".html","");
            int total = Integer.parseInt(Total);
            for (number = 1; number <= total ; number++) {
                String nextPage = replace + "-" + number + ".html";
                list.add(nextPage);
            }
            for (String link : list) {
                newsList(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {
        ArrayList<String> list = new ArrayList<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "hooshong");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("li.catlist_li > a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
//                System.out.println(link);
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
