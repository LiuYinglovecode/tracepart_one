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
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ZgwDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZgwDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "zgw");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title",document.select("div.fenxiang h3").text().trim());
                Elements select = document.select("div.fenxiang p");
                if (select.size() != 0) {
                    for (Element element : select) {
                        if (element.select("span").text().contains("-")){
                            info.put("time",select.select("span").text().trim().replace("关注",""));
                        }else {
                            select.select("span").remove();
                            info.put("source",select.text().replace("来源：",""));
                        }
                    }
                }
                info.put("author",document.select("div.neirong span.edit").text()
                        .replace("（责任编辑：","").replace("）",""));
                Elements text = document.select("div.left_content div.neirong");
                info.put("text",text.text().trim());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId",newsId);
                Elements imgList = document.select("div.neirong p span img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }

                info.put("url", url);
                info.put("crawlerId", "74");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", newsId);
                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }

            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
