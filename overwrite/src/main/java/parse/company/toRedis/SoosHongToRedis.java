package parse.company.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;


public class SoosHongToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoosHongToRedis.class);

    public static void main(String[] args) {
        SoosHongToRedis soosHongToRedis = new SoosHongToRedis();
        soosHongToRedis.industryList("http://www.sooshong.com/company/");
    }

    public void industryList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.classbox dl dt a");
            for (Element e : select) {
                String link = "http://www.sooshong.com" + e.attr("href");
                nextPage(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextPage(String url) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document document = Jsoup.parse(html);
            String select = document.select("div.list_classh").text().split("\\(")[1].replace(")","").trim();
            double v = Double.parseDouble(select) / 20;
            int i = new Double(Math.ceil(v)).intValue();
            int nextPage ;
            for (nextPage = 1; nextPage <=i; nextPage++) {
                String link = url + "p" + nextPage;
                companyList(link);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.lianxi a");
            for (Element e : select) {
                String link = "http://www.sooshong.com" + e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl-Company",link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
