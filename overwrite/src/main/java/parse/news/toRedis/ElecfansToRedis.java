package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class ElecfansToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElecfansToRedis.class);
    private static String baseUrl = "http://www.elecfans.com/news/hangye";

    public static void main(String[] args) {
        ElecfansToRedis elecfansToRedis = new ElecfansToRedis();
        elecfansToRedis.homepage("http://www.elecfans.com/news/hangye/");
    }
    public void homepage(String url) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "elecfans");
            if (null != html) {
                Document document = Jsoup.parse(html);
                String Total = document.select("#list18 > div.pagn > a.page-num").last().text();
                int total = Integer.valueOf(Total).intValue();//转行类型
                int number = 1;
                for (number = 1; number <= total; number++) {
                    String nextPage = baseUrl+"/Article_090_" + number + ".html";
                    category(nextPage);
                }

            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "elecfans");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select("#list18 > div > h3 > a");
                if (detailList.size()!=0) {
                    for (Element e : detailList) {
                        RedisUtil.insertUrlToSet("toCatchUrl", e.attr("href"));
                    }

                }else {
                    LOGGER.info("该页面为空");
                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
