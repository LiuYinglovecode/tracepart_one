package spider.product;

import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import news.parse.machine365News;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.MD5Util;

import java.util.HashMap;

/**
 * <p>Product: 全天候贸易网<p>
 * @author chenyan
 */
public class qth58Product {
    private final static Logger LOGGER = LoggerFactory.getLogger(qth58Product.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private static String baseUrl = "http://www.qth58.cn/";

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.136:2181");
        qth58Product qth58Product = new qth58Product();
        qth58Product.homePage("http://www.qth58.cn/product/");
        LOGGER.info("------完成了------");
    }

    private void insert(JSONObject productInfo) {
        Map = (java.util.Map) productInfo;
        if (updateToMySQL.productUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    private void homePage(String url) {
        try {
            String get = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            Document gbk = Jsoup.parse(get);
            Elements select = gbk.select("li.cate_sec_term a");
            for (Element element : select) {
                String link = element.attr("href");
                nextPage(link);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextPage(String url) {
        String replace = url.replace("http://www.qth58.cn/", "").replace("/","");
        int page = 1;
        for (page = 1; page < 2500; page++) {
            String link = baseUrl + replace + "-p" + page;
//            System.out.println(link);
            productlist(link);
        }

    }

    private void productlist(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "产品库");
            if (html!=null) {
                Document document = Jsoup.parse(html);
                Elements doc = document.select("a.comtitle");
                for (Element element : doc) {
                    String productInfoLink = element.attr("href");
                    productInfo(productInfoLink);
                }
            }else {
                LOGGER.info("网页不存在");
            }

        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }

    private void productInfo(String url) {
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
                productInfo.put("contactInformation", document.select("div.pcDcCatR dt").text().split("：", 2)[1]);//联系人
                productInfo.put("contacts", document.select("div.pcDcCatR dd").text().split("：", 2)[1]);//联系人电话
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
            insert(productInfo);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
