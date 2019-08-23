package parse.news.download;

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
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GongkongDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(GongkongDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "本站介绍");
            if (null != html) {
                Thread.sleep(4000);
                ArrayList<String> obj = new ArrayList<>();
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title",document.select("#Rtitle_D").text().trim());
                info.put("source",document.select("#grey6 > span").eq(0).text().trim().replace("供稿：",""));
                info.put("time",document.select("#grey6 > span").eq(2).text());
                Elements amountOfReading = document.select("#spanhit");
                if (!amountOfReading.text().contains("--")){
                    info.put("amountOfReading",amountOfReading.text().replace("人气：",""));
                }


                /**
                 * 新闻内容：
                 * 判断新闻内容中是否包含不需要的信息，
                 * 如果包含就去掉。
                 */
                Elements text = document.select(".content");
                if (text.text().contains("上一篇：")){
                    String s = text.text().split("上一篇：")[0];
                    info.put("text",s);
                    String newsId = NewsMd5.newsMd5(s);
                    obj.add(newsId);

                }else {
                    info.put("text", text.text().trim());
                    String newsId = NewsMd5.newsMd5(text.text().trim());
                    obj.add(newsId);
                }
                Elements imgList = text.select("p img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }
                Elements select = document.select(".text2");
                for (Element se : select) {
                    if (se.text().contains("审核编辑")) {
                        info.put("author ", se.text().replace("审核编辑（", "").replace("）", ""));
                    }
                }
                info.put("url", url);
                info.put("crawlerId", "96");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                for (String s : obj) {
                    info.put("newsId",s);
                    mysqlUtil.insertNews(info, "crawler_news", s);
                    if (esUtil.writeToES(info, "crawler-news-", "doc", s)){
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}