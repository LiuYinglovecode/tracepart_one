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
import spider.product.chemmProduct;
import util.ESUtil;
import util.HttpUtil;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChemNetDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChemNetDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();


    public static void main(String[] args) {
        ChemNetDownload ChemNetDownload = new ChemNetDownload();
        ChemNetDownload.newsInfo("http://news.chemnet.com/detail-2527182.html");
        LOGGER.info("---完成了---");
    }



    public void newsInfo(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (!html.isEmpty()) {
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                info.put("url", url);
                Document parse = Jsoup.parse(html);
                info.put("title", parse.select("div.pt30.center > div").text().trim());

                Elements elements = parse.select("div.pt30.center > p");
                if (!elements.isEmpty()){
                    String s = elements.text().split("　")[1];
//                    /**
//                     * 用正则获取日期，时间
//                     */
//                    String reg = "^(?=^.{3,255}$)(http(s)?:\\/\\/)?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(\\/\\w+\\.\\w+)*$";
//                    Pattern pat = Pattern.compile(reg);
//                    Matcher mat=pat.matcher(elements.text());
//                    String time = mat.replaceAll("").trim();
//                    System.out.println(time);
////                    info.put("time",time);
                    /**
                     * 用正则获取来源
                     */
                    String re = "[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}";
                    Pattern compile = Pattern.compile(re);
                    Matcher matcher = compile.matcher(s);
                    String source = matcher.replaceAll("").trim();
                    info.put("source",source);
                    info.put("time",s.replace(source,"").trim());
                }

                Elements text = parse.select("div.detail-text.line25.font14px > div");
                info.put("text", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                info.put("newsId", newsId);
                Elements images = text.select("img,p > img");
                if (!images.isEmpty()) {
                    for (Element image : images) {
                        imgs.add(image.attr("src"));
                    }
                    info.put("images", imgs.toString());
                }



                info.put("crawlerId", "124");
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
