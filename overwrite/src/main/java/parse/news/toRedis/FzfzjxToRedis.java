package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class FzfzjxToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(FzfzjxToRedis.class);
    private static final String beasUrl = "http://www.fzfzjx.com";

    public static void main(String[] args) {
        FzfzjxToRedis fzfzjxToRedis = new FzfzjxToRedis();
        fzfzjxToRedis.homepage("http://www.fzfzjx.com/news/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#nheadnav > p > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("新闻首页") && !e.text().contains("本站速递")) {
                        String href =beasUrl + e.attr("href");
//                        System.out.println(href);
                        paging(href);
//                        newsList(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String href) {
        try {
            String replace = href.split("news/t")[1].replace("/","");
            String html = HttpUtil.httpGetwithJudgeWord(href, "关于我们");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                int number = 1;
                String attr = parse.select("#pagenum > a").last().attr("href");
                String p = attr.split("p")[1].replace(".html", "").replace("/","");
                int total = Integer.valueOf(p).intValue();//类型转换
                for (number = 1; number <= total; number++) {
                    String url = beasUrl.concat("/news/t").concat(replace)+ "_p" + number+"/";
                    newsList(url);
              }
            } else {
                System.out.println("页面不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "fzfzjx");
            if (html!=null) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("dl.newslist > dt > p > a");
                for (Element e : select) {
                        String link =beasUrl + e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
