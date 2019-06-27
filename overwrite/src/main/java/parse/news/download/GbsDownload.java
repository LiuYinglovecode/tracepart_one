package parse.news.download;

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

public class GbsDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(GbsDownload.class);
    private static String baseUrl = "http://www.gbs.cn";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "gbs");
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title",document.select("div.det_nr_tit > h1").text().trim());
                Elements select = document.select("div.det_nr_time > p");
                for (Element element : select) {
                    if (element.text().contains("发布：")){
                        info.put("time",element.text().replace("发布：",""));
                    }else if (element.text().contains("作者：")){
                        info.put("author",element.text().replace("作者：",""));
                    }else if (element.text().contains("来源：")){
                        info.put("source",element.text().replace("来源：",""));
                    }
                }
                Elements text = document.select("div.det_nr_p");
                info.put("text",text.text().trim());
                String newsId = MD5Util.getMD5String(text.text().trim());
                info.put("newsId",newsId);
                Elements imgList = document.select("div.det_nr_p > p > img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        if (e.attr("src").contains("//uimg")) {
                            imgs.add("http:" + e.attr("src"));
                        } else if (e.attr("src").contains("http:")) {
                            imgs.add(e.attr("src"));
                        } else if (e.attr("src").contains("/upload/news")){
                            imgs.add(baseUrl+e.attr("src"));
                        }
                    }
                    info.put("images", imgs.toString());//图片链接
                }

                info.put("url", url);
                info.put("crawlerId", "73");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", newsId);
                esUtil.writeToES(info, "crawler-news-", "doc");
                System.out.println(info);

            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
