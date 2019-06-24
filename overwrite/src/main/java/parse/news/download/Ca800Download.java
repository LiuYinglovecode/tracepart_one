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

public class Ca800Download {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ca800Download.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    /**
     * @param url 新闻内容：有些新闻有很多图片，要拿到所有的图片链接，
     *            把链接放到集合中，在进行存储。
     */
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        newsInfo.put("url", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国自动化网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                //标题
                String title = document.select("div.newsdetail.border.fl h1").text().trim();
                newsInfo.put("title", title);
                Elements select = document.select("div.title_bar.f12.h25");
                if (select.size() != 0) {
                    if (select.text().contains("新闻类型")) {
                        newsInfo.put("time", select.text().split("新闻类型")[0].split("：")[1]);
                        newsInfo.put("plate", document.select("div.title_bar.f12.h25 a").text().trim());
                    }
                } else {
                    Elements select1 = document.select("div.title_bar");
                    if (select1.text().contains("来源：")) {
                        newsInfo.put("time", select1.text().split("来源：")[0].split("：")[1]);
                        newsInfo.put("plate", document.select("div.title_bar a").text().trim());
                    }
                }

                Elements text = document.select("div.newsdetail_con");//新闻内容
                if (text.size() != 0) {
                    newsInfo.put("text", text.text());
                    String newsId = NewsMd5.newsMd5(text.text());
                    newsInfo.put("newsId", newsId);
                    Elements img = text.select("div > img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                            newsInfo.put("images", imgsList.toString());//图片
                        }
                    }
                    newsInfo.put("crawlerId", "59");
                    newsInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    newsInfo.put("@timestamp", timestamp2.format(new Date()));
                    newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    System.out.println(newsInfo);
                    mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
                    esUtil.writeToES(newsInfo, "crawler-news-", "doc");
                } else {
                    Elements text1 = document.select("div.newsdetail.border.fl div.detail");//新闻内容
                    newsInfo.put("text", text1.text());
                    String newsId = NewsMd5.newsMd5(text1.text());
                    newsInfo.put("newsId", newsId);
                    Elements img = text1.select("p.MsoNormal img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            imgsList.add(element.attr("src"));
                            newsInfo.put("images", imgsList.toString());//图片
                        }
                    }
                    newsInfo.put("crawlerId", "59");
                    newsInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    newsInfo.put("@timestamp", timestamp2.format(new Date()));
                    newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                    mysqlUtil.insertNews(newsInfo, "crawler_news", newsId);
                    esUtil.writeToES(newsInfo, "crawler-news-", "doc");
                }
            } else {
                LOGGER.info("页面不存在！");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
