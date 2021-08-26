package parse.news.download;

import Utils.*;
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

public class JsjxmhwDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsjxmhwDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();
    private static String links = "http://www.jsjxmhw.com";


    public static void main(String[] args) {
        JsjxmhwDownload jsjxmhwDownload = new JsjxmhwDownload();
        jsjxmhwDownload.newsInfo("http://www.jsjxmhw.com/Html/NewsView.asp?ID=8306&SortID=10");
    }

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "jsjxmhw");
            Thread.sleep(SleepUtils.sleepMin());
            if (html != null) {
                Document parse = Jsoup.parse(html);
                Elements elements = parse.select("td[align=center] > font[style]");
                if (!elements.isEmpty()) {
                    newsInfo.put("title", elements.text().trim());
                }
                Elements select = parse.select("td:nth-child(3) > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(3) > td");
                if (!select.isEmpty()) {
//                    内容来源：中国工业新闻网      浏览次数：2      更新时间：2019-12-10
                    if (select.text().contains("内容来源：") && select.text().contains("浏览次数：") && select.text().contains("更新时间：")) {
                        newsInfo.put("source", select.text().split("浏览次数：")[0].replace("内容来源：", "").trim());
                        newsInfo.put("amountOfReading", select.text().split("更新时间：")[0].split("浏览次数：")[1].trim());
                        newsInfo.put("time", ForMat.getDatetimeFormat(select.text().split("更新时间：")[1].trim()));

                    }
                }

                Elements select1 = parse.select("td:nth-child(3) > table > tbody > tr > td > table > tbody > tr > td > table > tbody > tr:nth-child(4) > td");
                if (!select1.isEmpty()) {
                    String trim = select1.text().trim();
                    String s = NewsMd5.newsMd5(trim);
                    newsInfo.put("text", trim);
                    newsInfo.put("newsId", s);
                    newsInfo.put("html", select1.html());

                    Elements pImg = select1.select("p img");
                    if (!pImg.isEmpty()) {
                        String attr = pImg.attr("src");
                        if (!attr.contains("http://")) {
                            String s1 = links.concat(attr.replace("..", ""));
                            imgsList.add(s1);
                        }
                    }
                }

                newsInfo.put("images",imgsList.toString());
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "161");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
////                esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId);
//                if (esUtil.writeToES(newsInfo, "crawler-news-", "doc", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(newsInfo)) {
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }
            } else {
                LOGGER.info("页面不存在");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
