package spider.patent;

import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.IpProxyUtil;

import java.util.HashMap;
import java.util.Map;

public class zhuanlichaxun {
    private final static Logger LOGGER = LoggerFactory.getLogger(zhuanlichaxun.class);
    private static java.util.Map<String, String> Map;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static Map<String, String> header;
    private static String patentListUrl;
    private static String baseUrl;

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
        patentListUrl = "http://www.zhuanlichaxun.net/c-0-1-4092350-0-0-0-0-9-0-2.html";
        baseUrl = "http://www.zhuanlichaxun.net/";
    }

    public static void main(String[] args) {
        zhuanlichaxun zhuanlichaxun = new zhuanlichaxun();
        zhuanlichaxun.getPatentList(patentListUrl);
    }

    private void getPatentList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "专利");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements patentList = document.select(".maTableT .normal .wordwrap a");
                for (Element e : patentList) {
                    String patentName = e.text().trim().split("\\.", 2)[0];
                    String patentUrl = baseUrl + e.attr("href");
                    detail(patentUrl, patentName);
                }
                Elements pageList = document.select(".paginator");
                for (Element e : pageList) {
                    if ("下一页".equals(e.text().trim())) {
                        getPatentList(baseUrl + e.attr("href"));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url, String patentName) {
        JSONObject info = new JSONObject();
        String html = HttpUtil.httpGetwithJudgeWord(url, "专利");
        if (null != html) {
            Document document = Jsoup.parse(html);
            Elements infoList = document.select(".zljbxx tr td");
            for (Element e : infoList) {
                String key = e.text().trim();
                switch (key) {
                    case "申请专利号：":
                        info.put("applicationNumber", e.nextElementSibling().text().trim());
                        break;
                    case "申请日：":
                        info.put("applicationDate", e.nextElementSibling().text().trim());
                        break;
                    case "公开号：":
                        info.put("publicNumber", e.nextElementSibling().text().trim());
                        break;
                    case "公开日：":
                        info.put("publicDate", e.nextElementSibling().text().trim());
                        break;
                    case "主分类号：":
                        info.put("mainClassificationNumber", e.nextElementSibling().text().trim());
                        break;
                    case "申请人：":
                        info.put("applicant", e.nextElementSibling().text().trim());
                        break;
                    case "发明人：":
                        info.put("inventor", e.nextElementSibling().text().trim());
                        break;
                    case "地址：":
                        info.put("address", e.nextElementSibling().text().trim());
                        break;
                    default:
                }
            }
            info.put("patentName", patentName);
            info.put("crawlerId", "40");
            info.put("abstract", document.select(".gdyy").text().trim());
            insert(info);
        }
    }

    private void insert(JSONObject info) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.patentInsert(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


}
