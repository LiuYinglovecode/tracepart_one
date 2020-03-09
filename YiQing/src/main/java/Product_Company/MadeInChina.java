package Product_Company;

import Utils.HttpUtil;
import Utils.MD5Util;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * @author liyujie
 * 中国直在网
 * https://cn.made-in-china.com/
 */
public class MadeInChina {
    private final static Logger LOGGER = LoggerFactory.getLogger(MadeInChina.class);
    /**
     * 口罩
     */
    private static String KOUZHAO = "https://cn.made-in-china.com/market/kouzhao-1.html";

    public static void main(String[] args) {
        MadeInChina madeInChina = new MadeInChina();
        madeInChina.category(KOUZHAO);
    }

    private void category(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国制造网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements pcList = document.select(".prod-item.js-prod-item");
                for (Element e : pcList) {
                    String companyName = e.select(".co-info.clear > a").text().trim();
                    String productUrl = e.select(".tit.js-tit > a").attr("href");
                    String companyUrl = "https://cn.made-in-china.com/showroom/" +
                            e.select(".co-info.clear > a").attr("href").split("//", 2)[1].split(".cn", 2)[0] +
                            "-companyinfo.html";
//                    product(productUrl, companyName);
                    company(companyUrl, companyName);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 产品数据
     *
     * @param url 地址
     */
    private void product(String url, String companyName) {
        JSONObject productInfo = new JSONObject();
        JSONObject priseInfo = new JSONObject();
        JSONArray images = new JSONArray();
        try {
            String html = HttpUtil.httpGet(url, null);
            if (!"".equals(html)) {
                Document document = Jsoup.parse(html);
                //company_id	企业ID
                productInfo.put("company_id", MD5Util.getMD5String(companyName));
                //product_name	产品名称
                productInfo.put("product_name", document.select(".rightCon > h1").text().trim());
                Elements contactList = document.select(".contactInfo > li");
                for (Element e : contactList) {
                    if (e.text().contains("先生") || e.text().contains("女士")) {
                        //contacts	联系人
                        productInfo.put("contacts", e.select("strong").first().text().trim());
                    } else if (e.text().contains("电话") || e.text().contains("手机")) {
                        //tel	联系方式
                        productInfo.put("tel", e.select("strong").first().text().trim());
                    }
                }
                Elements priceList = document.select(".prices.js-price-type > tbody > tr");
                //charge_unit	单位
                productInfo.put("charge_unit", priceList.first().text().trim());
                //price	单位价格
                for (Element e : priceList) {
                    if (!e.text().contains("订货量")) {
                        priseInfo.put(e.select("td").first().text().trim(), e.select("td").last().text().trim());
                    }
                }
                productInfo.put("price", priseInfo);
                //inventory	库存
                Elements prodetails = document.select("#prodetails_data > tbody > tr");
                for (Element e : prodetails) {
                    if (e.select("th").text().contains("供货总量")) {
                        productInfo.put("inventory", e.select("td").text().trim());
                    }
                }
                Elements deTableDd = document.select(".de-table-bd.clear tbody > tr > td");
                for (Element e : deTableDd) {
                    if (e.text().contains("品牌")) {
                        //brand	品牌
                        productInfo.put("brand", e.nextElementSibling().text().trim());
                    } else if (e.text().contains("规格")) {
                        //specs	规格
                        productInfo.put("specs", e.nextElementSibling().text().trim());
                    } else if (e.text().contains("材质")) {
                        //material	材料
                        productInfo.put("material", e.nextElementSibling().text().trim());
                    } else if (e.text().contains("类型")) {
                        //classify_name	类别
                        productInfo.put("classify_name", e.nextElementSibling().text().trim());
                    }
                }
                //description	描述
                productInfo.put("description", document.select(".de-detail").text().trim());
                //image	图片
                Elements imageList = document.select(".de-detail img");
                for (Element e : imageList) {
                    images.add(e.attr("src"));
                }
                productInfo.put("image", images);
                LOGGER.info(String.valueOf(productInfo));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 企业数据
     *
     * @param url 地址
     */
    private void company(String url, String companyName) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "中国制造网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                //company_id	企业唯一标识
                companyInfo.put("company_id", MD5Util.getMD5String(companyName));
                //name	公司名称
                companyInfo.put("name", companyName);
                Elements contactList = document.select(".contactInfo > li");
                for (Element e : contactList) {
                    if (e.text().contains("先生") || e.text().contains("女士")) {
                        //contacts	联系人
                        companyInfo.put("contacts", e.select("strong").first().text().trim());
                    } else if (e.text().contains("电话") || e.text().contains("手机")) {
                        //tel	联系方式
                        companyInfo.put("tel", e.select("strong").first().text().trim());
                    } else if (e.text().contains("地址")) {
                        //province	省
                        companyInfo.put("province", e.select("span").last().text().trim().split("省", 2)[0]);
                        //city	市
                        companyInfo.put("city", e.select("span").last().text().trim().split("省", 2)[1].split("市", 2)[0]);
                        //adress	公司地址
                        companyInfo.put("adress", e.select("span").last().text().trim());
                    }
                }
                //info	公司简介
                companyInfo.put("info", document.select(".companyInf.js-companyInf").text().trim());
                Elements memb = document.select(".memb-lst > tbody > tr");
                for (Element e : memb) {
                    if (e.select("th").text().contains("主营产品")) {
                        //product	主营产品
                        companyInfo.put("product", e.select("td").text().trim());
                    } else if (e.select("th").text().contains("员工人数")) {
                        //employees	员工人数
                        companyInfo.put("employees", e.select("td").text().trim());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
