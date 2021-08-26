package parse.news.toRedis;

import Utils.RedisUtil;
import news.parse.steelcnNews;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class SteelcnToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(SteelcnToRedis.class);
    private static final String homepage = "http://news.steelcn.cn/";

    public static void main(String[] args) {
        SteelcnToRedis steelcnToRedis = new SteelcnToRedis();
        steelcnToRedis.homepage("http://news.steelcn.cn/");
    }
    //首页
    public void homepage(String url) {
        //因为使用了Jsoup组合选择器查找元素会存在重复元素，这里使用HashSet去重
        HashSet<String> hs = new HashSet<>();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中钢网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.nav ul li,div.fdc_jj div h2 a");//Jsoup组合选择器查找元素
                for (Element e : categoryList) {
                    if (!e.select("a").text().equals("首页")) {
                        String attr = e.select("a").attr("href");
                        hs.add(homepage + attr.replace("/", ""));
                        Iterator<String> it = hs.iterator();

                    }
                }
                for (String h : hs) {
                    paging(h);
                }
            }
            LOGGER.info("news.steelcn.cn DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
    分页：获取下一页总数，进行url的链接拼接
     */
    private void paging(String url) {
        ArrayList<String> list = new ArrayList<>();
        int number = 1;
        String replace = url.replace(".html", "");
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中钢网");
            Document document = Jsoup.parse(html);
            String Total = document.select("div#Fenye strong").text().replace("1/", "").replace(" 页", "");
            int total = Integer.valueOf(Total).intValue();//转行类型
            for (number = 1; number <= total; number++) {
                String nextPage = replace + "_p" + number + ".html";
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
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻");
            Document document = Jsoup.parse(html);
            Elements newsListInfo = document.select("div.list ul li a");
            for (Element e : newsListInfo) {
                String href = e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
