package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class AlliiiToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlliiiToRedis.class);
    private static final String baseUrl = new String("http://www.alliii.com");

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻");
            if (!html.isEmpty()) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.new_border > span > a,div.new_border390 > span > a,div.rw_zf > div > span > a");
                for (Element e : categoryList) {
                    if (!e.attr("href").contains("video")) {
                        String href = e.attr("href");
                        String link = baseUrl.concat(href);
//                        System.out.println(link);
                        more(link);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("news.lmjx.net  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * 分页：获取到总页数，对url进行拼接，得到完整的下一页新闻列表url。
     *
     * @param url
     */
    private void more(String url) {
        try {
            String replace = url.replace("1.html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                String sum = parse.select("span.gk_page_label").text().split("/")[1].replace("页","");
                int total = Integer.parseInt(sum);
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = replace.concat(String.valueOf(number)).concat(".html");
                    newsList(nextPage);
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新闻列表 ：解析网页一次获取全部想要的新闻信息url
     *
     * @param url
     */
    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements title = parse.select("table.news_list01 > tbody > tr > td > a");
                if (!title.isEmpty()) {
                    for (Element element : title) {
                        RedisUtil.insertUrlToSet("toCatchUrl", baseUrl.concat(element.attr("href")));
                    }
                }
            } else {
                LOGGER.info("网页不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
