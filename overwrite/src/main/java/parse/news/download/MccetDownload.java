package parse.news.download;

import Utils.ForMat;
import Utils.HttpUtil;
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
import util.MD5Util;
import util.mysqlUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MccetDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaijxDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        MccetDownload mccetDownload = new MccetDownload();
        mccetDownload.newsInfo("http://www.mccet.com/industry/info.aspx?m=9&n=116567");
    }

    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "mccet");
            Thread.sleep(SleepUtils.sleepMax());
            Document document = Jsoup.parse(html);
            if (html != null) {
                info.put("title",document.select("div.h1 > h1").text().trim());
                Elements elements = document.select("div.v_con_sx > em");
                for (Element element : elements) {
                    if (element.text().contains("年")&&element.text().contains("月")&&element.text().contains("日")){
                        info.put("time", ForMat.getDatetimeFormat(element.text().trim()));
                    }else if (element.text().contains("来源：")){
                        String source = element.text().replace("来源：","").trim();
                        info.put("source", source);
                    }
                }



                Elements select2 = document.select("div.c_con");
                info.put("text", select2.text().trim());
                info.put("html", select2.html());
                String newsId = MD5Util.getMD5String(select2.text().trim());
                info.put("newsId",newsId);

                Elements img = select2.select("p img,span img,span p img");
                if (img.size() != 0) {
                    for (Element element : img) {
                        imgsList.add(element.attr("src"));
                    }
                    info.put("images", imgsList.toString());//图片
                }

                info.put("url", url);
                info.put("crawlerId", "162");
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
