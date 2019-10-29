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

/**
 * @author
 */
public class LeiphoneDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeiphoneDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void detail(String url) {

        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            Thread.sleep(8000);
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title",document.select(" div.article-title > div > h1.headTit").first().text().trim());
                info.put("author",document.select("td.aut").first().text().replace("本文作者：","").trim());
                info.put("time", ForMat.getDatetimeFormat(document.select("td.time").first().text().trim()));

                Element text = document.select(".lph-article-comView").first();
                text.select("a").remove();
                String s = text.html().replace("雷锋网特约稿件，未经授权禁止转载。详情见", "")
                        .replace("转载须知", "")
                        .replace("雷锋网", "")
                        .replace("(公众号：雷锋网)", "")
                        .replace("编辑", "");
                info.put("text",s);
                String text1 = text.html().replace("雷锋网特约稿件，未经授权禁止转载。详情见", "")
                        .replace("转载须知", "")
                        .replace("雷锋网", "")
                        .replace("(公众号：雷锋网)", "")
                        .replace("编辑", "").trim();
                info.put("text",text1);
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId",newsId);
                Elements imgList = text.select("p img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }
                info.put("url", url);
                info.put("crawlerId", "94");
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
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
