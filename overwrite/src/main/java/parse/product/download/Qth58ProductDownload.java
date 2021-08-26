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
import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Qth58ProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(Qth58ProductDownload.class);
    private static ESUtil esUtil = new ESUtil();
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static void main(String[] args) {
        Qth58ProductDownload qth58ProductDownload = new Qth58ProductDownload();
        qth58ProductDownload.productInfo("http://www.qth58.cn/wujiyan/34602706.html");
    }

    public void productInfo(String url) {
        JSONObject productInfo = new JSONObject();
        productInfo.put("detailUrl", url);
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系方式");
            if (html!=null) {
                Document document = Jsoup.parse(html);
                productInfo.put("product_name", document.select("div.pcNewDcPname h1").text().trim());//产品名称，title
                productInfo.put("prices", document.select("div.pcDcPmsgDetail p strong").text().trim());//产品价格
                productInfo.put("company_name", document.select("a.pcDcCatCnamea").text().trim());//公司名字
                productInfo.put("company_id", MD5Util.getMD5String(document.select("a.pcDcCatCnamea").text().trim()));//公司名加密
                productInfo.put("contactInformation",  document.select("div.pcDcCatR dd").text().split("：", 2)[1]);//联系人电话
                productInfo.put("contacts",document.select("div.pcDcCatR dt").text().split("：", 2)[1]);//联系人
                Elements elements = document.select("div.pcDcPmsgDetail ul li");
                for (Element element : elements) {
                    if (element.text().contains("起订量")) {
                        productInfo.put("mini_order", element.text().split("：", 2)[1]);
                    } else if (element.text().contains("可售数量")) {
                        productInfo.put("total_supply", element.text().split("：", 2)[1]);
                    }
                }
                productInfo.put("product_desc", document.select("div.canshu-content").text().trim());//产品详情
                Elements product_desc = document.select("div.canshu-content");
                if (product_desc.size()!=0){
                    productInfo.put("product_desc", product_desc.text().trim());//产品详情
                }else {
                    productInfo.put("product_desc", document.select("div.pcProDetailInfoList,div.pcProDetailIntro").text().trim());
                }
            }else {
                LOGGER.info("网页不存在");
            }
            productInfo.put("crawlerId","37");
            productInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            productInfo.put("@timestamp", timestamp2.format(new Date()));
            productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
//                    mysqlUtil.insertProduct(productInfo);
//                    if (esUtil.writeToES(productInfo, "crawler-product-", "doc", nameId)) {
//                        RedisUtil.insertUrlToSet("catchedUrl-Product", url);
//                    }
            if (mysqlUtil.insertProduct(productInfo)) {
                RedisUtil.insertUrlToSet("catchedUrl-Product", url);
            }

        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
