package parse.news.download;

import Utils.ForMat;
import Utils.NewsMd5;
import Utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import Utils.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EapadDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(EapadDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {

        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        try {
            Thread.sleep(1000);
            String html = HttpUtil.httpGetwithJudgeWord(url, "eapad");
            if (html != null) {
                Document document = Jsoup.parse(html);
                info.put("title",document.select("div.caption p").text().trim());
                Elements element = document.select("div.time");
                element.select("a").remove();
                if (element.text().contains("来源：")) {
                    info.put("author", element.text().split("作者：")[1].replace("分享到：", "").replace("明星编辑：","").trim());
                    info.put("source", element.text().split("作者：")[0].split("来源：")[1].trim());
                    info.put("time", ForMat.getDatetimeFormat(element.text().split("来源：")[0].trim()));
                }else {
                    info.put("time", ForMat.getDatetimeFormat(element.text().replace("分享到：", "").trim()));

                }
                Elements textInfo = document.select("#div1");
                info.put("html", textInfo.html());
                info.put("text", textInfo.text().trim());
                String newsId = NewsMd5.newsMd5(textInfo.text().trim());
                info.put("newsId",newsId);
                Elements imgs = textInfo.select("p img");
                if (imgs.size() != 0) {
                    for (Element el : imgs) {
                        if (!el.attr("src").contains("http://") && !el.attr("src").contains("https://")) {
                            imgsList.add("http://www.eapad.cn"+el.attr("src"));
                        }else {
                            imgsList.add(el.attr("src"));
                        }
                    }
                    info.put("images", imgsList.toString());//图片
                }
                info.put("url", url);
                info.put("crawlerId", "84");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            } else {
                LOGGER.info("页面不存在！");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
