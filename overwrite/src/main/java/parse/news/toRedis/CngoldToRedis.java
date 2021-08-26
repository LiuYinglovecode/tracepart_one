package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CngoldToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CngoldToRedis.class);
    private static final String homepage = "http://www.cngold.com.cn";

    public static void main(String[] args) {
        CngoldToRedis cngoldToRedis = new CngoldToRedis();
        cngoldToRedis.homepage("http://www.cngold.com.cn/");
    }

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中金网");
            Thread.sleep(SleepUtils.sleepMin());

            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.new_nav > a");
                for (Element e : categoryList) {
                    if (e.text().contains("外汇")
                            || e.text().contains("商品")
                            || e.text().contains("要闻")
                            || e.text().contains("国内")
                            || e.text().contains("观点")
                            || e.text().contains("行业")
                            || e.text().contains("区块链")) {
                        if (!e.attr("href").contains("fxeye")) {
//                            System.out.println(e);
                        paging(e.attr("href"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            Thread.sleep(SleepUtils.sleepMin());
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(url, "中金网");
            Thread.sleep(SleepUtils.sleepMin());

            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements Total = document.select("a.page_next").prev();
                String text = Total.text();
                String attr = Total.attr("href");
                int total = Integer.parseInt(text);
                for (number = 1; number <= total; number++) {
                    if (url.contains("yaowen")) {
                        String s = url.replace("/yaowen/", "");
                        String nextPage = url.concat(s.replace(text, String.valueOf(number)));
                        list.add(nextPage);
                    }if (url.contains("view")){
                        String s = url.replace("/view/", "");
                        String nextPage = url.concat(s.replace(text, String.valueOf(number)));
                        list.add(nextPage);
                    }if (url.contains("hangye")){
                        String s = attr.replace("/hangye/", "");
                        String nextPage = url.concat(s.replace(text, String.valueOf(number)));
                        list.add(nextPage);
                    }if (url.contains("blockchain")){
                        String s = url.replace("/blockchain/", "");
                        String nextPage = url.concat(s.replace(text, String.valueOf(number)));
                        list.add(nextPage);
                    } else {
                        String s = attr.replace("/", "");
                        String nextPage = url.concat(s.replace(text, String.valueOf(number)));
                        list.add(nextPage);
                    }
                }
                for (String link : list) {
                    newsList(link);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //新闻列表
    private void newsList(String url) {

        ArrayList<String> list = new ArrayList<>();
        try {
            Thread.sleep(SleepUtils.sleepMin());
            String html = HttpUtil.httpGetwithJudgeWord(url, "中金网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.data-list > dl > dd > h4 > a");
                for (Element e : newsListInfo) {
                    String href = e.attr("href");
                    list.add(href);
                }
            }
            for (String link : list) {
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
