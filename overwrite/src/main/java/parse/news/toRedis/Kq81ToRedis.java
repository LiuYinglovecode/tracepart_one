package parse.news.toRedis;


import Utils.HttpUtil;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Kq81ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Kq81ToRedis.class);
    private static String link = "https://www.kq81.com";
    public static void main(String[] args) {
        Kq81ToRedis kq81ToRedis = new Kq81ToRedis();
        kq81ToRedis.home("https://www.kq81.com/");
    }


    public void home(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "kq81");
//            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#qh_con7 ul li a");
                for (Element e : categoryList) {
                    if (!e.text().contains("矿业专访") && !e.text().contains("专题报道")) {
                        String links = link + e.attr("href");
                        paging(links);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String links) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(links, "kq81");
            Document parse = Jsoup.parse(html);
            String last = parse.select("td.ShowPageNew a").last().attr("href");
            String s = last.split("&page=")[1];
            for (int i = 1; i <=Integer.parseInt(s) ; i++) {
                String url = link.concat("/AspCode/").concat(last.replace(s, String.valueOf(i)));
                newsList(url);

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "kq81");
            Document parse = Jsoup.parse(html);
            Elements links = parse.select("tr:nth-child(3) > td > table:nth-child(1) > tbody > tr > td > a");
            for (Element element : links) {
                String attr =link.concat("/AspCode/").concat( element.attr("href") );
                RedisUtil.insertUrlToSet("toCatchUrl",attr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
