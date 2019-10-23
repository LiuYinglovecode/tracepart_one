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
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CinicDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CinicDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String baseUrl = new String("http://cinic.org.cn");


    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (!html.isEmpty()) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                info.put("title", parse.select("center > b").text().trim());
                String select = parse.select("div.col-l > div > center").text();
                if (!select.isEmpty()&&select.contains("来源：")&&select.contains("时间：")){
                    info.put("time", ForMat.getDatetimeFormat(select.split("时间：")[1].trim()));
                    info.put("source",select.split("时间：")[0].replace("来源：","").trim());
                }else if (!select.isEmpty()&&select.contains("作者：")&&select.contains("时间：")){
                    info.put("time",select.split("时间：")[1].trim());
                    info.put("author",select.split("时间：")[0].replace("作者：","").trim());
                }else {
                    info.put("time",select.replace("时间：","").trim());
                }



                String newsId = null;
                Elements text = parse.select(".dc-ccm1");
                text.select("div").remove();
                if (!text.isEmpty()) {
                    String trim = text.html();
                    if (trim.contains("转自：")){
                        String t = trim.split("转自：")[0].replace("转自：", "");
                        info.put("text",t);
                        newsId = NewsMd5.newsMd5(text.text().trim());
                    }else {
                        String t = trim.split("版权及免责声明：")[0].replace("版权及免责声明：", "");
                        info.put("text",t);
                        newsId = NewsMd5.newsMd5(text.text().trim());
                    }

                    Elements images = text.select("p > img,div > img");
                    if (!images.isEmpty()) {
                        for (Element image : images) {

                            if (image.attr("src").contains("http")) {
                                imgs.add(image.attr("src"));
                            }else {
                                imgs.add(baseUrl.concat(image.attr("src")));
                            }
                        }
                        info.put("images", imgs.toString());
                    }
                }





                info.put("newsId", newsId);
                info.put("crawlerId", "123");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
////                esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
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
