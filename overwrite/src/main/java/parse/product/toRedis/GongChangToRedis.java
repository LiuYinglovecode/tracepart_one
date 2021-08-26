package parse.product.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class GongChangToRedis {
    private final static Logger LOGGER = LoggerFactory.getLogger(GongChangToRedis.class);

    //首页
    public void productPage(String url) {
        try {
            int industry = 8412;
            int number = 1;
            for (number = 1; number <= industry; number++) {
                String s = url + "/list/" + number + "/";
                String string = new String(s);
                String html = HttpUtil.httpGetwithJudgeWord(s, "关于我们");
                Document document = Jsoup.parse(html);
                Elements cite = document.select("cite");
                if (!cite.isEmpty()) {
                    int total = Integer.parseInt(cite.text().split("/")[1].replace("页", ""));
                    for (int i = 1; i <= total; i++) {
                        String concat = string.concat(String.valueOf(i)).concat("/");
                        productList(concat);
                    }
                } else {
                    productList(s);
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void productList(String concat) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(concat, "gongchang");
            if (!html.isEmpty()) {
                String string = new String("https://chanpin.gongchang.com/show/");
                Document document = Jsoup.parse(html);
                Elements elements = document.select("h3.title_list > a");
                if (!elements.isEmpty()) {
                    for (Element element : elements) {
                        String attr = element.attr("data-scode");
                        String links = string.concat(attr).concat("/");
                        RedisUtil.insertUrlToSet("toCatchUrl-Product", links);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
