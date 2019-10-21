package parse.news.download;

import Utils.NewsMd5;
import Utils.RedisUtil;
import Utils.SleepUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LedinsideDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(LedinsideDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static final String baseUrl = new String("https://www.ledinside.cn");
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ledinside");
            if (!html.isEmpty()) {
                String newsId = null;
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("#squeeze > div > h2").text().trim());
                Elements select = document.select("#squeeze > div > div.submitted");
                if (!select.isEmpty() && select.text().contains("[编辑：")) {
                    info.put("time", select.text().split("\\[编辑：")[0].trim());
                    info.put("author", select.text().split("\\[编辑：")[1].replace("]", "").trim());
                    info.put("source", "LEDinside");
                }


                /**
                 * 文本信息
                 */
                Elements text = document.select("#squeeze > div > div.node_body.clear-block");
                if (!text.isEmpty()) {
                    String trim = text.html().replace("如需转载，需本网站E-Mail授权。并注明\"来源于LEDinside\"，未经授权转载、断章转载等行为，本网站将追究法律责任！E-Mail:service@ledinside.com", "")
                            .replace("如需获取更多资讯，请关注LEDinside官网（www.ledinside.cn）或搜索微信公众账号（LEDinside）。", "")
                            .trim();
                    info.put("text", trim);
                    newsId = NewsMd5.newsMd5(text.html().replace(" ", "").trim());
                    info.put("newsId", newsId);

                    /**
                     * 图片
                     */
                    Elements imgList = text.select("p > img");
                    if (!imgList.isEmpty()) {
                        for (Element e : imgList) {
                            if (!e.attr("src").contains("https")) {
                                imgs.add(baseUrl.concat(e.attr("src")));
                            }else {
                                imgs.add(e.attr("src"));
                            }
                        }
                        info.put("images", imgs.toString());//图片
                    }


                    info.put("url", url);
                    info.put("crawlerId", "118");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                    if (mysqlUtil.insertNews(info, "crawler_news", newsId)) {
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                } else {
                    LOGGER.info("detail null");
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
