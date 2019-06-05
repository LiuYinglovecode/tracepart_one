package spider.patent.zhiqueip;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.mysqlUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class zhiqueipParse {
    private final static Logger LOGGER = LoggerFactory.getLogger(zhiqueipParse.class);
    private static Map<String, String> header = new HashMap<>();
    private static String baseUrl = "https://www.zhiqueip.com";

    static {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public void patentList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "知雀");
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                Elements patentUrlList = document.select(".deal-ul.content .deal-item .deal-item-right");
                for (Element e : patentUrlList) {
                    detail(baseUrl + e.select("a").attr("href"), e.select("a").text().trim(), e.select(".deal-item-lpr").text().trim());
                }
                Element nextpage = document.select(".main-page a").last();
                if ("下一页".equals(nextpage.text().trim())) {
                    patentList(baseUrl + nextpage.attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String detailUrl, String patentName, String state) {
        try {
            JSONObject info = new JSONObject();
            String html = HttpUtil.httpGetwithJudgeWord(detailUrl, "知雀");
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                Elements infoList = document.select(".item-detail-a-inner-wrapper .item-main-line .item-main-key");
                for (Element e : infoList) {
                    String key = e.text().trim();
                    switch (key) {
                        case "申请号：":
                            info.put("applicationNumber", e.nextElementSibling().text().trim());
                            break;
                        case "申请日：":
                            info.put("applicationDate", e.nextElementSibling().text().trim());
                            break;
                        case "公开（公告）号：":
                            info.put("publicNumber", e.nextElementSibling().text().trim());
                            break;
                        case "公开日：":
                            info.put("publicDate", e.nextElementSibling().text().trim());
                            break;
                        case "申请（专利权）人：":
                            info.put("applicant", e.nextElementSibling().text().trim());
                            break;
                        case "发明人：":
                            info.put("inventor", e.nextElementSibling().text().trim());
                            break;
                        case "主分类号：":
                            info.put("mainClassificationNumber", e.nextElementSibling().text().trim());
                            break;
                        case "分类号：":
                            info.put("classificationNumber", e.nextElementSibling().text().trim());
                            break;
                        case "地址：":
                            info.put("address", e.nextElementSibling().text().trim());
                            break;
                        case "国省代码：":
                            info.put("nationalCode", e.nextElementSibling().text().trim());
                            break;
                        default:
                    }
                }
                info.put("patentName", patentName);
                info.put("state", state);
                info.put("abstract", document.select(".item-detail-text").text().trim());
                info.put("crawlerId", "55");
                mysqlUtil.insertPatent(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
