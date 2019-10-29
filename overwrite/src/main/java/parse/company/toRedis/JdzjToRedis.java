package parse.company.toRedis;

import Utils.HtmlUnitUnits;
import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import java.util.ArrayList;

public class JdzjToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdzjToRedis.class);
    private static String link = new String("https://www.jdzj.com");

    public static void main(String[] args) {
        JdzjToRedis jdzjToRedis = new JdzjToRedis();
        jdzjToRedis.category("https://www.jdzj.com/yp.html");
    }

    public void category(String url) {
        try {
//            String html = HttpUtil.httpGetwithJudgeWord(url, "机电之家");
//            Document parse = Jsoup.parse(html);
//            System.out.println(html);
            HtmlUnitUnits httpUtils = HtmlUnitUnits.getInstance();
            httpUtils.setTimeout(30000);
            httpUtils.setWaitForBackgroundJavaScript(30000);
            Document document = httpUtils.getHtmlPageResponseAsDocument(url);
            Elements select = document.select("li.mod_cate > div > dl> dt > a");
            for (Element e : select) {
                String href = link.concat(e.attr("href"));
                nextpage(href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextpage(String href) {
        try {
            ArrayList<String> list = new ArrayList<>();
            int number = 1;
            String html = HttpUtil.httpGetwithJudgeWord(href, "机电之家");
            assert html != null;
            Document document = Jsoup.parse(html);
            String s = document.select("div.crumbs.mt10 > span").text()
                    .split("到 ")[1].split(" 条")[0];
            int total =(int) Math.ceil(Double.parseDouble(s) / 10);
            String attr = document.select("#lblpage > a").last().attr("href");
            for (number = 1; number <= total ; number++) {
                String nextPage = link.concat(attr).replace(String.valueOf(total),String.valueOf(number));
                list.add(nextPage);
            }
            for (String link : list) {
                companyList(link);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "机电之家");
            Document document = Jsoup.parse(html);
            Elements select = document.select("li.inform > h2 > span > a");
            for (Element link : select) {
                String href = link.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Company", href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


}
