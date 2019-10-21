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
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.Locale;
        import java.util.TimeZone;

public class ItDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItDownload.class);
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "更多阅读");
            if (null != html) {
                Thread.sleep(6000);
                ArrayList<String> obj = new ArrayList<>();
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                info.put("title", document.select("header.entry-header > h1").text().trim());
                info.put("time", document.select("li.post-time > time").text());

                /**
                 * 新闻内容：
                 * 判断新闻内容中是否包含不需要的信息，
                 * 如果包含就去掉。
                 */
                Elements text = document.select("div.entry-content.articlebody");
                text.select("div#wp_rp_first").remove();
                text.select("script").remove();
                text.select("p script").remove();
                info.put("text", text.html());
                String newsId = NewsMd5.newsMd5(text.text().trim());
                Elements imgList = text.select("p img");
                if (imgList.size() != 0) {
                    for (Element e : imgList) {
                        imgs.add(e.attr("src"));
                    }
                    info.put("images", imgs.toString());//图片链接
                }
                info.put("timestamp", timestamp.format(new Date()));
                timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                info.put("@timestamp", timestamp2.format(new Date()));
                info.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                info.put("url", url);
                info.put("crawlerId", "98");
                info.put("newsId", newsId);
//                mysqlUtil.insertNews(info, "crawler_news", newsId);
//                if (esUtil.writeToES(info, "crawler-news-", "doc", newsId)) {
//                    RedisUtil.insertUrlToSet("catchedUrl", url);
//                }
                if (mysqlUtil.insertNews(info, "crawler_news", newsId)){
                    RedisUtil.insertUrlToSet("catchedUrl", url);
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
