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
                info.put("author",document.select("td.aut").first().text().replace("本文作者：",""));
                info.put("time",document.select("td.time").first().text());

                Element text = document.select(".lph-article-comView").first();
                text.select("p.sourceMsg").remove();
                String p = text.select("p").last().text();
                if (p.contains("雷锋网原创文章，未经授权禁止转载。详情见转载须知。")){
                    text.select("p").last().remove();
                }
                String replace = text.text().replace("雷锋网消息，", "").
                        replace("雷锋网(公众号：雷锋网)消息，", "").replace("(公众号：雷锋网)", "");
                info.put("text",replace);
                String newsId = NewsMd5.newsMd5(replace);
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
                mysqlUtil.insertNews(info, "crawler_news", newsId);
                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}