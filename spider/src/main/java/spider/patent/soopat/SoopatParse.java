package spider.patent.soopat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class SoopatParse {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoopatParse.class);
    private static SimpleDateFormat crawlerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String baseUrl = "http://www.soopat.com";
    private static Map<String, String> header = new HashMap<>();

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public void crawlerStart() {
        try {
            JSONArray mainClassify = SoopatIPC.getMainClassify();
            if (null != mainClassify) {
                for (Object f : mainClassify) {
                    if (null != f) {
                        classifyLv1(baseUrl + f);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void classifyLv1(String classifyLv1Url) {
        try {
            String html = HttpUtil.httpGet(classifyLv1Url, header);
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                Elements classifyLv2E = document.select(".IPCTable .IPCContentRow .IPCChild a");
                for (Element e : classifyLv2E) {
                    if (null != e.attr("href")) {
                        classifyLv2(baseUrl + e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void classifyLv2(String classifyLv2Url) {
        try {
            String html = HttpUtil.httpGet(classifyLv2Url, header);
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                Elements patentListE = document.select(".IPCTable .IPCContentRow .IPCControl a");
                for (Element e : patentListE) {
                    if (!e.attr("href").contains("http")) {
                        patentList(baseUrl + e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void patentList(String patentListUrl) {
        try {
            String html = HttpUtil.httpGet(patentListUrl, header);
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                Elements patentDetailE = document.select(".PatentBlock .PatentTypeBlock a");
                for (Element e : patentDetailE) {
                    if (!"".equals(e.attr("href"))) {
                        patentDetail(baseUrl + e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void patentDetail(String patentDetailUrl) {
        try {
            JSONObject info = new JSONObject();
            String html = HttpUtil.httpGet(patentDetailUrl, header);
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                String patentName = document.select(".detailtitle h1").not(".stateico.stateicoinvalid").text().trim();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
