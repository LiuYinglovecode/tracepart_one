package parse.news.download;

import Utils.ForMat;
import Utils.NewsMd5;
import Utils.RedisUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.IConfigManager;
import news.parse.jiancaiNews;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Ic37Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ic37Download.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        try {
            Thread.sleep(1000);
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (html != null) {
                Document document = Jsoup.parse(html);
                info.put("title",document.select(".newstitle > h1").text().trim());
                Elements element = document.select(".newstitle > span");
                if (element.text().contains("资讯类别")) {
                    //时间：2019-8-2， 来源：互联网， 资讯类别：行业统计
                    info.put("plate", element.text().split("资讯类别：")[1].trim());
                    info.put("source", element.text().split("资讯类别：")[0].split("来源：")[1].replace("，","").trim());
                    info.put("time", ForMat.getDatetimeFormat(element.text().split("来源：")[0].replace("时间：","").replace("，","").trim()));
                }else if (element.text().contains("文章类别：")) {
                    //时间：2008-12-11， 来源：互联网， 文章类别：单片机/DSP
                    info.put("plate", element.text().split("文章类别：")[1].trim());
                    info.put("source", element.text().split("文章类别：")[0].split("来源：")[1].replace("，","").trim());
                    info.put("time", ForMat.getDatetimeFormat(element.text().split("来源：")[0].replace("时间：","").replace("，","").trim()));
                }
                Elements textInfo = document.select(".contentlist2,.contentlist");
                info.put("text", textInfo.text().trim());
                info.put("html", textInfo.html());
                String newsId = NewsMd5.newsMd5(textInfo.text().trim());
                info.put("newsId",newsId);
                Elements imgs = textInfo.select("p img");
                if (imgs.size() != 0) {
                    for (Element el : imgs) {
                        if (el.attr("src").contains("http")&&el.attr("src").contains("https")) {
                            imgsList.add(el.attr("src"));
                        }else {
                            imgsList.add("https://www.ic37.com"+el.attr("src"));
                        }
                    }
                    info.put("images", imgsList.toString());//图片
                }
                info.put("url", url);
                info.put("crawlerId", "86");
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
