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
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("#Rtitle_D").text().trim());
                info.put("source", document.select("#grey6 > span").eq(0).text().replace("供稿：", "").trim());
                info.put("time", ForMat.getDatetimeFormat(document.select("#grey6 > span").eq(2).text().trim()));
                Elements amountOfReading = document.select("#spanhit");
                if (!amountOfReading.text().contains("--")) {
                    info.put("amountOfReading", amountOfReading.text().replace("人气：", "").trim());
                }


                /**
                 * 新闻内容：
                 * 判断新闻内容中是否包含不需要的信息，
                 * 如果包含就去掉。
                 */
                String newsId = null;
                Elements text = document.select("#article");
                info.put("text", text.text().trim());
                info.put("html", text.html());
                newsId = NewsMd5.newsMd5(text.text().trim());

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
                info.put("newsId", newsId);
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)) {
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
