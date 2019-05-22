package news.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import es.ESClient;
import mysql.updateToMySQL;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import config.IConfigManager;

import java.util.HashMap;
import java.util.Map;


/**
 * @author liyujie
 * http://www.cinn.cn/
 */
public class cinn {
    private static final Logger LOGGER = LoggerFactory.getLogger(cinn.class);
    private static Map<String, String> header;
    private static Map Map;
    private static final String homepage = "http://www.cinn.cn/";
    private static String baseUrl = "http://www.cinn.cn";
    private static String tableName = "original_news";

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        cinn cinn = new cinn();
        cinn.homepage(homepage);
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(".banner_inner a");
                for (Element e : categoryList) {
                    if (!"#".equals(e.attr("href")) && !e.attr("href").contains("html")) {
                        category(baseUrl + e.attr("href"));
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void category(String url) {
        try {
            for (int i = 1; i < 66; i++) {
                String html = HttpUtil.httpGetwithJudgeWord(url + "/index_" + String.valueOf(i) + ".html", "中国工业报社");
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    Elements detailList = document.select(".block_left .smallblock.pd_news");
                    for (Element e : detailList) {
                        if (null != e.select(".news_title a").attr("href")) {
                            String detailUrl = url + e.select(".news_title a").attr("href").split(".", 2)[1];
                            detail(detailUrl);
                        }
                    }
                } else {
                    LOGGER.info("category null");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国工业报社");
            if (null != html) {
//                html = deleteLabel(html);
//                Map<Integer, String> map = splitBlock(html);
//                System.out.println(judgeBlocks(map));
                JSONObject info = new JSONObject();
                JSONArray imgs = new JSONArray();
                Document document = Jsoup.parse(html);
                String plate = document.select(".index a:last-child").text().trim();
                info.put("url", url);
                info.put("plate", plate);
                String title = document.select(".detail_title").text().trim();
                info.put("title", title);
                Elements source_time_list = document.select(".detail_abs.bdsharebuttonbox span");
                for (Element e : source_time_list) {
                    if (e.text().contains("文章来源")) {
                        String source = e.text().trim().split("文章来源 :", 2)[1];
                        info.put("source", source);
                    }
                    if (e.text().contains("发布时间")) {
                        String time = e.text().trim().split("发布时间 ：", 2)[1];
                        info.put("time", time);
                    }
                }
                Elements imgList = document.select(".TRS_Editor img");
                if (!"0".equals(String.valueOf(imgList.size()))) {
                    for (Element e : imgList) {
                        if (e.attr("src").contains("http")) {
                            imgs.add(e.attr("src"));
                        } else if (e.attr("src").contains("./")) {
                            imgs.add(url.substring(0, url.lastIndexOf("/")) + e.attr("src").split("/", 2)[1]);
                        }
                    }
                }
                info.put("images",imgs.toString());
                String text = document.select(".detail_content").text().trim();
                info.put("text", text);
                info.put("crawlerId", "27");
                insert(info, tableName, title, "title");

            } else {
                LOGGER.info("detail null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void toES(JSONObject info) {
        try {
            TransportClient transportClient = new ESClient.ESClientBuilder().createESClient().getClient();
            transportClient.prepareIndex("1", "3")
                    .setSource(info, XContentType.JSON)
                    .execute()
                    .actionGet();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info, String tablename, String title, String type) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.exist2(Map, tablename, title, "title")) {
                if (updateToMySQL.newsUpdate(Map, title, "title")) {
                    LOGGER.info("更新中 : " + Map.toString());
                }
            } else {
                if (updateToMySQL.newsInsert(Map)) {
                    LOGGER.info("插入中 : " + Map.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
