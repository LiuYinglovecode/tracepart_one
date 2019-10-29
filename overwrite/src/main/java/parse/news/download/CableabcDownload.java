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

public class CableabcDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(CableabcDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    //新闻信息
    public void newsInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject newsInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "电缆网");
            if (html != null) {
                Document document = Jsoup.parse(html);
                String title = document.select("div.contentspage h1").text().trim();
                newsInfo.put("title", title.trim());//标题
                newsInfo.put("time", ForMat.getDatetimeFormat(document.select("div.addtime").text().trim()));//发布时间
                Elements select = document.select("div.time.clearfix.mmbb span");
                for (Element element : select) {
                    if (element.text().contains("/")) {
                        newsInfo.put("time", ForMat.getDatetimeFormat(element.text().trim()));
                    } else if (element.text().contains("来源：")) {
                        newsInfo.put("source", element.text().split("：")[1].trim());
                    }
                }
                String newsId = null;
                Elements text = document.select("#divcontent");
                if (text.size() != 0) {
                    Elements select1 = text.select("#main_ContentPlaceHolder1_pnlContent.info_ltext.lhh.lll_content");
                    if (select1.size() != 0) {
                        if (select1.select("p").last().previousElementSibling().text().contains("转载请注明出处")) {
                            select1.select("p").last().previousElementSibling().remove();
                            newsInfo.put("text", select1.text().trim());
                            newsInfo.put("html", select1.html());
                            newsId = NewsMd5.newsMd5(select1.text().trim());
                        } else {
                            select1.select("p").last().remove();
                            newsInfo.put("text", select1.text().trim());
                            newsInfo.put("html", select1.html());
                            newsId = NewsMd5.newsMd5(select1.text().trim());
                        }
                    }
                    if (text.select("p").last().text().contains("转载请注明出处")) {
                        text.select("p").last().remove();
                        newsInfo.put("text", text.text().trim());
                        newsInfo.put("html", text.html());
                        newsId = NewsMd5.newsMd5(text.text().trim());
                    } else {
                        newsInfo.put("text", text.text().trim());
                        newsInfo.put("html", text.html());
                        newsId = NewsMd5.newsMd5(text.text().trim());
                    }
                    Elements img = text.select("#main_ContentPlaceHolder1_pnlContent.info_ltext.lhh.lll_content p img");
                    if (img.size() != 0) {
                        for (Element element : img) {
                            if (element.attr("src").contains("http://news.cableabc.com")) {
                                imgsList.add(element.attr("src"));
                            } else {
                                imgsList.add("http://news.cableabc.com" + element.attr("src"));
                            }
                            newsInfo.put("images", imgsList.toString());//图片链接
                        }
                    }
                }
                newsInfo.put("newsId",newsId);
                newsInfo.put("url", url);//链接地址
                newsInfo.put("crawlerId", "53");
                newsInfo.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                newsInfo.put("@timestamp", timestamp2.format(new Date()));
                newsInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                if (mysqlUtil.insertNews(newsInfo, "crawler_news", newsId)){
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(newsInfo)){
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
