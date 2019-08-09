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

public class CndianjiDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CndianjiDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject info = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "新闻资讯");
            if (html != null) {
                Document document = Jsoup.parse(html);
                Elements title = document.select("div.nileft_tbox.mb15 > h1");
                if (title.size()!=0) {
                    info.put("title", title.text().trim());
                }else {
                    info.put("title", document.select("div.nileft_tbox.mb15 > h1").text().trim());
                }
                Elements timeSource = document.select(".n_source");
                info.put("amountOfReading",timeSource.select("#click").text().replace("浏览次数：",""));
                timeSource.select("#click").remove();
                info.put("time",timeSource.text().replace("文章来源：",""));
                info.put("source",timeSource.text().replace("上传时间：",""));

                Elements textInfo = document.select("div.news_info02");
                String text = textInfo.text();
                info.put("text", text);
                String newsId = NewsMd5.newsMd5(text);
                info.put("newsId",newsId);
                Elements imgs = textInfo.select("p img");
                if (imgs.size() != 0) {
                    for (Element element : imgs) {
                        imgsList.add(element.attr("src"));
                    }
                    info.put("images", imgsList.toString());//图片
                }
                info.put("url", url);
                info.put("crawlerId", "83");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                mysqlUtil.insertNews(info, "crawler_news", newsId);
                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
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
