package parse.product.download;

import Utils.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EbdoorProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(EbdoorProductDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void productInfo(String url) {
        JSONObject productInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "ebdoor");
            Document parse = Jsoup.parse(html);
            productInfo.put("product_desc",parse.select("div#descDiv.BmConn2").text().trim());
            productInfo.put("company_name",parse.select("label#LinkmanInfo1_comp_Name a").text().trim());
            productInfo.put("company_id", MD5Util.getMD5String(parse.select("label#LinkmanInfo1_comp_Name a").text().trim()));
            productInfo.put("product_name",parse.select("h2.tipproname").text().trim());
            Elements select = parse.select("div.proInfoBox ul li dl");
            for (Element info : select){
                switch (info.text().trim()){
                    case "当前售价:":
                        productInfo.put("prices", info.nextElementSibling().text().trim());
                        break;
                    case "供应数量:":
                        productInfo.put("total_supply", info.nextElementSibling().text().trim());
                        break;
                    case "商品产地:":
                        productInfo.put("production_place", info.nextElementSibling().text().trim());
                        break;
                    default:
                }
            }
            Elements contact = parse.select("#descHeader > div.ws > span");
            for (Element brand : contact) {
                switch (brand.text().trim()){
                    case "品牌：":
                        productInfo.put("product_brand",brand.nextElementSibling().text().trim());
                        break;
                    default:
                }
            }
            Elements contactinfo = parse.select("div#cardcontent.contentdetail table tbody tr td");
            for (Element brand : contactinfo) {
                switch (brand.text().trim()) {
                    case "联 系 人：":
                        productInfo.put("contacts", brand.nextElementSibling().text().trim());
                        break;
                    case "手 机：":
                        productInfo.put("contactInformation", brand.nextElementSibling().text().trim());
                        break;
                    default:
                }
            }
            productInfo.put("detailUrl",url);
            productInfo.put("crawlerId", "47");
            productInfo.put("createTime", creatrTime.format(new Date()));
            productInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            productInfo.put("@timestamp", timestamp2.format(new Date()));
            productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//            mysqlUtil.insertProduct(productInfo);
//            if (esUtil.writeToES(productInfo, "crawler-product-", "doc", md5String)) {
//                RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//            }
            if (mysqlUtil.insertProduct(productInfo)){
                RedisUtil.insertUrlToSet("catchedUrl-Product",url);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
