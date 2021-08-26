package maima;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class SeedUrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(Maima.class);
    private static String Entrance = "http://www.casicloud.com/";
    private static String base = "http://www.casicloud.com";
    private static String maimaToCatche = "maimaToCatche";
    private static Map<String, String> header = null;

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36");
    }

    public static void main(String[] args) {
        try {
            SeedUrl seedUrl = new SeedUrl();
            seedUrl.casicloud();
            LOGGER.info("DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void casicloud() {
        try {
            String html = HttpUtil.httpGet(Entrance, header);
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                Elements hrefList = document.select("a");
                for (Element e : hrefList) {
                    String url = e.attr("href");
                    if (url.startsWith("/")) {
                        RedisUtil.insertUrlToSet(maimaToCatche, base + url);
                    } else if (url.contains(base)) {
                        RedisUtil.insertUrlToSet(maimaToCatche, url);
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
