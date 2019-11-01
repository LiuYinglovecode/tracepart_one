package parse.news.toRedis;

import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

public class CinnToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CinnToRedis.class);
    private static String baseUrl = "http://www.cinn.cn";


    public static void main(String[] args) {
        CinnToRedis cinnToRedis = new CinnToRedis();
        cinnToRedis.homepage("http://www.cinn.cn");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(".banner_inner a");
                for (Element e : categoryList) {
                    if (!"#".equals(e.attr("href"))
                            && !e.attr("href").contains("html")
                            && !e.attr("href").contains("gywm")
                            && !e.attr("href").contains("zgjxzz")) {
                        category(baseUrl + e.attr("href"));
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.cinn.cn  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

/*    private void more(String href) {
        try {
            String url = null;
            String html = HttpUtil.httpGetwithJudgeWord(href, "关于我们");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                String categoryList = document.select("div.pd_pages > a").last().attr("href");
                String s = categoryList.split("_")[1].split(".html")[0];
                for (int i = 1; i <= Integer.parseInt(s); i++) {
                    if (url.contains("headline")) {
                        url = href + "/index_" + String.valueOf(i) + ".html";
                    } else {
                        url = href + "index_" + String.valueOf(i) + ".html";
                    }
                }
                category(url);
            } else {
                LOGGER.info("homepage null");
            }
            LOGGER.info("www.cinn.cn  DONE");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }*/

    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国工业报社");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select(".block_left .smallblock.pd_news");
                for (Element e : detailList) {
                    if (null != e.select(".news_title a").attr("href")) {
                        String detailUrl = url + "/" + e.select(".news_title a").attr("href").replace("./", "");
                        RedisUtil.insertUrlToSet("toCatchUrl", detailUrl);
                    }
                }

                String attr = document.select("div > a.fx_down").attr("href");
                if (!"".equals(attr)){
                    if (url.contains("headline")){
                        category(url+"/"+attr);
                    }else {
                        category(url+attr);
                    }

                }
            } else {
                LOGGER.info("category null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
