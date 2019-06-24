package parse.news.download;

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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PlaDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static final String urllink = "https://www.51pla.com";

    /**
     * @param url 新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
     *            把链接放到集合中，在进行存储。
     */
    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                //链接地址
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                //标题
                String title = parse.select("div.title h2").text();
                info.put("title", title);
                Elements time = parse.select("div.meta > span");
                //发布时间及来源
                for (Element element : time) {
                    if (element.text().contains("发布时间：")) {
                        info.put("time", element.select("i").text().trim());
                    } else if (element.text().contains("来源：")) {
                        info.put("source", element.select("i").text().trim());
                    }
                }
                //图片
                Elements images = parse.select("div.content p img");
                if (images.size() != 0) {
                    for (Element image : images) {
                        String src = urllink + image.attr("src");
                        imgs.add(src);
                        info.put("images", imgs.toString());
                    }
                }
                //正文
                Elements text = parse.select("div.content");
                if (text.size() != 0) {
                    String trim = text.text().trim();
                    info.put("text", trim);
                    String newsId = NewsMd5.newsMd5(trim);
                    info.put("newsId", newsId);
                    info.put("crawlerId", "68");
                    info.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    info.put("@timestamp", timestamp2.format(new Date()));
                    info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(info, "crawler_news", newsId);
                    esUtil.writeToES(info, "crawler-news-", "doc");
                }

            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
