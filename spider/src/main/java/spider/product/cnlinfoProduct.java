package spider.product;

import com.alibaba.fastjson.JSONObject;
import ipregion.ProxyDao;
import mysql.updateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.IpProxyUtil;
import util.MD5Util;

import java.util.HashMap;
import java.util.Set;
/**
 * <p>Product: 行业信息网<p>
 * @author chenyan
 */
public class cnlinfoProduct {
    private final static Logger LOGGER = LoggerFactory.getLogger(cnlinfoProduct.class);
    private static java.util.Map<String, String> header;
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }


    private void productNavigationBar(String url) {

//        ArrayList list = new ArrayList();

        try {

            String html = httpGetWithProxy(url,"cnlinfo");
            Document document = Jsoup.parse(html);
            Elements productBar = document.select("div.total-nav > ul > li > a");
            for (Element element : productBar) {
                String href = element.attr("href");
                productClassification(href);
            }


//            Elements elements = document.select("div.canshu-content > ul > li");
//            for (Element element : elements) {
//                String trim = element.text().trim();
//                list.add(trim);
//            }
//            System.out.println(list);
//            Object json = JSON.toJSONString(list);
//            System.out.println(json);
//            companyInfo.put("product_desc",json);

//            insert(companyInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void productClassification(String url) {
        try {

            String html = httpGetWithProxy(url,"cnlinfo");
            Document document = Jsoup.parse(html);
            Elements productBar = document.select("div.main_con > ul > li > p > a");
            for (Element element : productBar) {
                String href = element.attr("href");
                String trade_category = element.text();
                productList(href,trade_category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void productList(String url,String trade_category) {
        try {

            String html = httpGetWithProxy(url,"cnlinfo");
            Document document = Jsoup.parse(html);
            Elements productBar = document.select("div.product_list_info > h3 > a");
            for (Element element : productBar) {
                String href = element.attr("href");
                productInfo(href,trade_category);
            }

            Elements select = document.select("a.page");
            for (Element element : select) {
                if (element.text().contains("下一页")){
                    String href = "http://www.cnlinfo.net"+element.attr("href");
                    System.out.println("下一页："+href);
                    productList(href,trade_category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void productInfo(String url,String trade_category) {
        JSONObject productInfo = new JSONObject();
        productInfo.put("detailUrl",url);
        productInfo.put("trade_category", trade_category);
        productInfo.put("crawlerId", "38");
        try {
            String html = httpGetWithProxy(url,"cnlinfo");
            if (html!=null) {
                Document document = Jsoup.parse(html);
//            System.out.println(document);
//          产品名
                Elements product_name = document.select("div.chanpin-info > h2");
                if (product_name.size() != 0) {
                    productInfo.put("product_name", product_name.text().trim());
                } else {
                    productInfo.put("product_name", document.select("div.top_info div.det h1").text().trim());
                }
//          公司名及MD5加密
                Elements company_name = document.select("div.logo > a");
                if (company_name.size() != 0) {
                    productInfo.put("company_id", MD5Util.getMD5String(company_name.text().trim()));
                    productInfo.put("company_name", company_name.text().trim());
                } else {
                    productInfo.put("company_id", MD5Util.getMD5String(document.select("div.det h1").text().trim()));
                    productInfo.put("company_name", document.select("div.det h1").text().trim());
                }

                //价格
                Elements prices = document.select("div.chanpin-info");
                if (prices.size() != 0) {
                    if (prices.select("div.info-jiage div.jiage-yuanjia span.info-key").text().trim().contains("价 格：")) {
                        prices.select("span.info-key").remove();
                        productInfo.put("prices", prices.select("div.info-jiage div.jiage-yuanjia").text().trim());
                    }
                    if (prices.select("div.info-pinpai span.info-key").text().trim().contains("品牌：")) {//品牌
                        productInfo.put("product_brand", prices.select("div.info-pinpai span.info-value").text().trim());
                    }
                } else {
                    Elements prices1 = document.select("div.pro_price");
                    if (prices1.select("div.pro_pri label").text().trim().contains("价格")) {
                        productInfo.put("prices", prices1.select("div.pro_pri span").text().trim());
                    }
                }
                //发货地
                Elements delivery_place = document.select("div.info-wuliu span.info-value");
                if (delivery_place.size() != 0) {
                    productInfo.put("delivery_place", delivery_place.text().trim());
                } else {
                    productInfo.put("delivery_place", document.select("div.address span").text().trim());
                }
                //最小起订量
                Elements mini_order = document.select("div.info-min-goumai span.info-value");
                if (mini_order.size() != 0) {
                    productInfo.put("mini_order", mini_order.text().trim());//最小起订量
                } else {
                    productInfo.put("mini_order", document.select("div.pro_batch span").text().trim());
                }
                //供货总量
                Elements total_supply = document.select("div.info-kucun span.info-value");
                if (total_supply.size() != 0) {
                    productInfo.put("total_supply", total_supply.text().trim());
                } else {
                    productInfo.put("total_supply", document.select(".order_lists > li:nth-child(4) > span").text().trim());//供货总量
                }
//            productInfo.put("mini_order",document.select("div.info-min-goumai span.info-value").text().trim());//最小起订量
//            productInfo.put("total_supply", document.select("div.info-kucun span.info-value").text().trim());//供货总量
//            productInfo.put("delivery_place",document.select("div.info-wuliu span.info-value").text().trim());//发货地
                productInfo.put("delivery_period", document.select("div.info-fahuo span.info-value").text().trim());//发货期限
                Elements select = document.select("div.personal_top");
                if (select.size() != 0) {
                    productInfo.put("contactInformation", select.select("span a").text().trim());//联系人
                    productInfo.put("contacts", select.select("div.personal_bottom").text().trim());//电话
                } else {
                    Elements detail_sj_btnBox = document.select(".detail_sj_btnBox");
                    if (detail_sj_btnBox.select("p:nth-child(1)").text().trim().contains("联系人")) {
                        productInfo.put("contactInformation", detail_sj_btnBox.select("p:nth-child(1)").text().split(":", 2)[1]);
                        if (detail_sj_btnBox.select("p:nth-child(2)").text().trim().contains("手机"))
                            productInfo.put("contacts", detail_sj_btnBox.select("p:nth-child(2)").text().split(":", 2)[1]);
                    }
                }
                //产品详情
                Elements product_desc = document.select(".canshu-content ,.chanpin-content");
                if (product_desc.size() != 0) {
                    productInfo.put("product_desc", product_desc.text().trim());
                } else {
                    productInfo.put("product_desc", document.select(".panes").text().trim());
                }
            }else {
                LOGGER.info("网页不存在");
            }
            insert(productInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        cnlinfoProduct cnlinfoProduct = new cnlinfoProduct();
        cnlinfoProduct.productNavigationBar("http://www.cnlinfo.net/");
        LOGGER.info("---完成了---");
    }


    private void insert(JSONObject productInfo) {
        Map = (java.util.Map) productInfo;
        if (updateToMySQL.productUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    private String httpGetWithProxy(String url, String judgeWord) {
        String ipProxy = null;
        try {
            if (ipProxyList.isEmpty()) {
                LOGGER.info("ipProxyList is empty");
                Set<String> getProxy = getProxy();
                ipProxyList.addProxyIp(getProxy);
            }
            ipProxy = ipProxyList.getProxyIp();
            String html = null;
            for (int i = 0; i < 5; i++) {
                if (url != null && ipProxy != null) {
                    html = HttpUtil.httpGetWithProxy(url, header, ipProxy);
                }
                if (html != null && html.contains(judgeWord)) {
                    return html;
                }
                ipProxyList.removeProxyIpByOne(ipProxy);
                ProxyDao.delectProxyByOne(ipProxy);
                ipProxy = ipProxyList.getProxyIp();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private static Set<String> getProxy() {
        return ProxyDao.getProxyFromRedis();
    }
}
