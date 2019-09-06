package parse.news.download;

import Utils.NewsMd5;
import Utils.RedisUtil;
import Utils.SleepUtils;
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

public class ChinazDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChinazDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "chinaz");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("url", url);
                info.put("title", document.select("div.article-detail-hd > h2").text().trim());
                info.put("time", document.select("div.meta > span").eq(0).text().trim());
                info.put("source", document.select("div.meta > span.source").text().replace("稿源：","").trim());
                Elements select = document.select("#ctrlfscont");
                select.select("blockquote").remove();
                if (select.select("p").last().text().contains("本文由站长之家用户投稿")) {
                    select.select("p").last().remove();
                }
                if (select.select("p").last().previousElementSibling().text().contains("免责声明")) {
                    select.select("p").last().previousElementSibling().remove();
                }
//                String text = select.text()
//                        .replace("图片版权所属：站长之家 站长之家(ChinaZ.com) ", "")
//                        .replace("本文由站长之家用户投稿，未经站长之家同意，严禁转载。如广大用户朋友，发现稿件存在不实报道，欢迎读者反馈、纠正、举报问题（反馈入口）。 免责声明：本文为用户投稿的文章，站长之家发布此文仅为传递信息，不代表站长之家赞同其观点，不对对内容真实性负责，仅供用户参考之用，不构成任何投资、使用建议。请读者自行核实真实性，以及可能存在的风险，任何后果均由读者自行承担。", "");
                info.put("text",select.text().trim());
                Elements imgList = select.select("p img,p a img");
                if (0!=imgList.size()) {
                    for (Element img : imgList) {
                        String attr = img.attr("src");
                        imgs.add(attr);
                    }
                    info.put("images", imgs.toString());
                }

                String newsId = NewsMd5.newsMd5(select.text().trim());
                info.put("newsId", newsId);
                info.put("crawlerId", "108");
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
