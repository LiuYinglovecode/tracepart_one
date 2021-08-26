package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MccetToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(MccetToRedis.class);
    private static String links = "http://www.mccet.com";

    public static void main(String[] args) {
        MccetToRedis mccetToRedis = new MccetToRedis();
        mccetToRedis.homepage("http://www.mccet.com/");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "mccet");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("div.w290.l > ul > li > a");
                for (Element e : categoryList) {
                    if (e.text().contains("行业焦点") || e.text().contains("企业资讯")){
                        String href =links + e.attr("href");
//                        System.out.println(href);
                        paging(href);
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

            String html = HttpUtil.httpGetwithJudgeWord(url, "mccet");
            Document parse = Jsoup.parse(html);
            Element tailPage = parse.select("div.page_css > a").last();//获取总结数
            String attr = tailPage.attr("href");
            String total = attr.split("&page=")[1];
            int number = 1;
            for (number = 1; number <= Integer.parseInt(total); number++) {
                String link = links.concat(attr.replace(total,String.valueOf(number))) ;//拼接链接地址
                newsList(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "mccet");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.hyl > ul > li > h1 > a");
            for (Element e : select) {
                String link =links.concat( e.attr("href") );
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
