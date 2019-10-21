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

public class ChinacraneDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinacraneDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url){

        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (html != null) {
                Document document = Jsoup.parse(html);
                newsInfo.put("title", document.select("#title").text().trim());//标题
                newsInfo.put("time", document.select("time").text().replace("发布时间:",""));//发布时间
                Elements select = document.select("div.from");
                if (!select.text().contains("作者：")) {
                    newsInfo.put("amountOfReading",select.text().split("人气：")[1]);
                    newsInfo.put("source",select.text().split("人气：")[0].replace("来源：",""));
                }else {
                    newsInfo.put("author",select.text().split("作者：")[1]);
                    newsInfo.put("amountOfReading",select.text().split("作者：")[0].split("人气：")[1]);
                    newsInfo.put("source",select.text().split("人气：")[0].replace("来源",""));
                }
                Elements textInfo = document.select("#article");
                if (textInfo.size() != 0) {
                    String text = textInfo.html();
                    newsInfo.put("text", text);
                    String newsId = NewsMd5.newsMd5(textInfo.text().trim());
                    newsInfo.put("newsId",newsId);
                    Elements img = textInfo.select("p > img");
                    if (img.size()!=0){
                        for (Element imgs : img) {
                            String src = imgs.attr("src");
                            imgsList.add(src);
                        }
                        newsInfo.put("images",imgsList.toString());
                    }
                    newsInfo.put("url",url);
                    newsInfo.put("crawlerId", "79");
                    newsInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    newsInfo.put("@timestamp", timestamp2.format(new Date()));
                    newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
//                    if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)) {
//                        RedisUtil.insertUrlToSet("catchedUrl", url);
//                    }
                    if (mysqlUtil.insertNews(newsInfo, "crawler_news", newsId)){
                        RedisUtil.insertUrlToSet("catchedUrl", url);
                    }
                }
            } else {
                LOGGER.info("页面不存在");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
