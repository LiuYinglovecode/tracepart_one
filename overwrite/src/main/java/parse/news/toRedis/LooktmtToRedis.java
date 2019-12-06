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

public class LooktmtToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(LooktmtToRedis.class);
    private static final String homepage = "http://www.looktmt.com/";

    public static void main(String[] args) {
        LooktmtToRedis looktmtToRedis = new LooktmtToRedis();
        looktmtToRedis.homepage("http://www.looktmt.com/");
    }

    //主页
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "looktmt");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("li.dropdown.nav-dropdown a");
                for (Element e : categoryList) {
                    paging(e.attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            int i;
           if (url.contains("guandian")){
               for (i = 1; i <=505 ; i++) {
                   String s = url.concat("/").concat(String.valueOf(i));
                   newsList(s);
               }
           }else if (url.contains("jinrong")){
               for (i = 1; i <=558 ; i++) {
                   String s = url.concat("/").concat(String.valueOf(i));
                   newsList(s);
               }
           }else if (url.contains("blockchain")){
               for (i = 1; i <=477 ; i++) {
                   String s = url.concat("/").concat(String.valueOf(i));
                   newsList(s);
               }
           }else if (url.contains("internet")){
               for (i = 1; i <=1750 ; i++) {
                   String s = url.concat("/").concat(String.valueOf(i));
                   newsList(s);
               }
           }else if (url.contains("tongxin")){
               for (i = 1; i <=335 ; i++) {
                   String s = url.concat("/").concat(String.valueOf(i));
                   newsList(s);
               }
           }else if (url.contains("zhineng")){
               for (i = 1; i <=196 ; i++) {
                   String s = url.concat("/").concat(String.valueOf(i));
                   newsList(s);
               }
           }else if (url.contains("kuaibao")){
               for (i = 1; i <=7300 ; i++) {//如果该网站加入日更，可以爬取改分类前10页，其余分类可不爬
                   String s = url.concat("/").concat(String.valueOf(i));
                   newsList(s);
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
            String html = HttpUtil.httpGetwithJudgeWord(url, "looktmt");
            Thread.sleep(SleepUtils.sleepMin());
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements newsListInfo = document.select("div.hlgd-content > div> dl > dd > h3 > a");
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
