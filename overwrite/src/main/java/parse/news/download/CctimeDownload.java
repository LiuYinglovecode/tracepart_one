package parse.news.download;

import Utils.ForMat;
import Utils.HttpUtil;
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
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CctimeDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CctimeDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static void main(String[] args) {
        CctimeDownload cctimeDownload = new CctimeDownload();
        cctimeDownload.detailDownload("http://www.cctime.com/html/2019-12-24/1491787.htm");
    }

    public void detailDownload(String url) {
        try {
            JSONObject info = new JSONObject();
            JSONArray img = new JSONArray();
            String html = HttpUtil.httpGetwithJudgeWord(url, "飞象网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                info.put("title", document.select("tr:nth-child(1) > td > table > tbody > tr > td > h1").text().trim());

                Elements select = document.select("td.dateAndSource");
                if (null!=select) {
                    //正则获取时间
                    String re = "[^0-9]";
                    Pattern compile = Pattern.compile(re);
                    Matcher matcher = compile.matcher(select.text());
                    String time = matcher.replaceAll("").replaceAll(":", "").trim();
                    info.put("time", ForMat.getDatetimeFormat(time));
                    info.put("author", select.text().replace("作者：", "").trim());
                }
                Elements text = document.select("div.art_content");
                info.put("text", text.text().trim());
                info.put("html", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                Elements imgList = document.select("p img");
                if (!imgList.isEmpty()){
                    for (Element e : imgList) {
                        img.add(e.attr("src"));
                    }
                    info.put("images", img.toString());
                }

                info.put("url",url);
                info.put("crawlerId", "168");
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                            mysqlUtil.insertNews(info, "crawler_news", newsId);
////                            esUtil.writeToES(info, "crawler-news-", "doc", newsId);
//                            if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)){
//                                RedisUtil.insertUrlToSet("catchedUrl", url);
//                            }
//                if (mysqlUtil.insertNews(info, "crawler_news", newsId)) {
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
