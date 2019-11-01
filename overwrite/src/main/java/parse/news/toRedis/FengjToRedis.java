package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class FengjToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(FengjToRedis.class);


    public static void main(String[] args) {
        FengjToRedis fengjToRedis = new FengjToRedis();
        fengjToRedis.homepage("http://news.fengj.com/");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "fengj");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.column_title > strong > a,div.bottom_con > div > div > a");
                for (Element e : categoryList) {
                    String href = e.attr("href");
                    paging(href);
                }

            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            String replace = url.replace("1.html", "");
            String html = HttpUtil.httpGetwithJudgeWord(url, "fengj");
            Document parse = Jsoup.parse(html);
            String pagesNumber = parse.select("span.xxts").text();//获取总条数
            if (Integer.parseInt(pagesNumber)>10) {
                int ceil = (int) Math.ceil(Double.parseDouble(pagesNumber) / 10);//获取总页数
                int number = 1;
                for (number = 1; number < ceil; number++) {
                    String link = replace +"page"+ number+"/";//拼接链接地址
                    newsList(link);
                }
            }else {
                newsList(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "fengj");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.ml_topic > a");
            for (Element e : select) {
                String link = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
