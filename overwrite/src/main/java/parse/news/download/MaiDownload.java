package parse.news.download;

import Utils.MD5Util;
import Utils.NewsMd5;
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

public class MaiDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaiDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "86mai");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url",url);
                Document parse = Jsoup.parse(html);
                String title = parse.select("#title").text().trim();
                info.put("title",title);
                Elements select = parse.select("div.info");
                if (select.size()!=0) {
                    if (!select.text().contains("来源：")) {
                        info.put("time", select.text().trim().split("浏览次数：")[0].split("：")[1]);
                        info.put("amountOfReading", select.select("#hits").text().trim());
                    } else {
                        info.put("time",select.text().split("来源：")[0].split("：")[1]);
                        info.put("source",select.text().split("来源：")[1].split("作者：")[0]);
                        info.put("author",select.text().split("来源：")[1].split("作者：")[1].split(" 浏览次数")[0]);
                        info.put("amountOfReading", select.select("#hits").text().trim());
                    }
                }
                String text = parse.select("#article").text().trim();
                info.put("text",text);
                String newId = NewsMd5.newsMd5(text);
                info.put("newId",newId);
                Elements images = parse.select("#article > div > p > img");
                if (images.size()!=0) {
                    for (Element image : images) {
                        String src = image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                }

                info.put("crawlerId", "65");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", newId);
                esUtil.writeToES(info, "crawler-news-", "doc");
            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
