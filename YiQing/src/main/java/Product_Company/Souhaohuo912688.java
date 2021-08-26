package Product_Company;

import Dao.MainDBDao;
import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author liyujie
 * 根据关键字搜索
 * 获取第一页相关度最高的产品
 */
public class Souhaohuo912688 {
    private final static Logger LOGGER = LoggerFactory.getLogger(Souhaohuo912688.class);
    private static String kouzhao = "https://s.912688.com/prod/dy/search?kw=%E5%8F%A3%E7%BD%A9&view=img";
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static MainDBDao dbDao = new MainDBDao();

    public static void main(String[] args) {
        Souhaohuo912688 souhaohuo912688 = new Souhaohuo912688();
        souhaohuo912688.productList(kouzhao);
    }

    /**
     * 产品列表
     *
     * @param url
     */
    private void productList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "搜好货");
            Thread.sleep(2000);
            if (null != html) {
                Document parse = Jsoup.parse(html);
                Elements elements = parse.select("div.product-left-new.clearfix > ul > li > div.detailed");
                for (Element element : elements) {
                    String productURL = element.select("p.clear.h40 > a").attr("href");
                    String companyUrl = element.select("p.mt5 > a").attr("href").split(".com", 2)[0].concat(".com/company.html");
                    product(productURL);
                    company(companyUrl);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 产品数据
     *
     * @param url
     */
    public void product(String url) {
        try {
            JSONArray imgsList = new JSONArray();
            JSONObject productInfo = new JSONObject();
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "912688");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document parse = Jsoup.parse(html);
                String title = parse.select("div.main-data-param.R > h1").text().trim();
                if (!"".equals(title)) {
                    productInfo.put("product_name", title);//标题
                    //价格、起订量、可售量
                    Elements elements = parse.select("div.main-data-param.R > table > tbody > tr");
                    if (elements.size() != 0) {
                        for (Element element : elements) {
                            if (element.text().contains("格")) {
                                productInfo.put("prices", element.text().split("格")[1].replace("¥", "").trim());//价格
                            } else if (element.text().contains("起订量")) {
                                productInfo.put("mini_order", element.text()
                                        .replace("起订量", "")
                                        .replace("≥", "")
                                        .replace("件", "")
                                        .replace("个", "")
                                        .replace("台", "")
                                        .replace("张", "")
                                        .replace("套", "")
                                        .replace("pcs", "")
                                        .replace("把", "")
                                        .replace("千克", "")
                                        .replace("部", "")
                                        .replace("米", "")
                                        .replace("K", "")
                                        .trim());//品牌

                            } else if (element.text().contains("可售量")) {
                                productInfo.put("total_supply", element.text()
                                        .replace("可售量", "")
                                        .replace("台", "")
                                        .replace("件", "")
                                        .replace("个", "")
                                        .replace("张", "")
                                        .replace("套", "")
                                        .replace("pcs", "")
                                        .replace("把", "")
                                        .replace("千克", "")
                                        .replace("部", "")
                                        .replace("米", "")
                                        .replace("K", "")
                                        .trim());//可售量
                            }
                        }
                    }

                    //品牌
                    Elements select = parse.select("div.three-con > table > tbody > tr > td");
                    if (!select.isEmpty()) {
                        for (Element element : select) {
                            if (element.text().contains("品牌")) {
                                productInfo.put("product_brand", element.nextElementSibling().text().trim());
                            }
                        }
                    }

                    /**
                     * 产品图片
                     */
                    Elements img = parse.select("li > img.prod-pic-list,#prodDetailDiv > p > img,#prodDetailDiv > p > span > img");
                    if (0 != img.size()) {
                        for (Element element : img) {
                            if (!element.attr("src").contains("nopic60")) {
                                imgsList.add(element.attr("src"));
                            }
                        }
                        productInfo.put("product_images", imgsList.toString());
                    }

                    /**
                     * 产品信息
                     */
                    Elements productDesc = parse.select("#prodDetailDiv");
                    if (0 != productDesc.size()) {
                        productInfo.put("product_desc", productDesc.text().trim());
                    }

                    /**
                     * 公司名
                     */
                    String nameId = null;
                    Elements name = parse.select("div > ul > li > a.com-name.b2b-statics");
                    if (0 != name.size()) {
                        String trim = name.text().trim();
                        productInfo.put("company_name", trim);
                        nameId = MD5Util.getMD5String(trim);
                    } else {
                        String s = "企业未知";
                        productInfo.put("company_name", s);
                        nameId = MD5Util.getMD5String(s);
                    }
                    Elements select1 = parse.select("#bot-nav > div.member.act > div > ul > li > span.name");
                    for (Element element : select1) {
                        if (element.text().contains("联系姓名")) {
                            productInfo.put("contacts", element.nextElementSibling().text().trim());
                        } else if (element.text().contains("电话号码：")) {
                            productInfo.put("contactInformation", element.nextElementSibling().text().trim());
                        } else if (element.text().contains("所在地区：")) {
                            productInfo.put("production_place", element.nextElementSibling().text().trim());
                        }
                    }
                    productInfo.put("company_id", nameId);


                    productInfo.put("crawlerId", "146");
                    productInfo.put("timestamp", timestamp.format(new Date()));
                    timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
                    productInfo.put("@timestamp", timestamp2.format(new Date()));
                    productInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 公司数据
     *
     * @param url
     */
    private void company(String url) {
        try {
            JSONObject companyInfo = new JSONObject();
            String html = HttpUtil.httpGetwithJudgeWord(url, "912688");
            Thread.sleep(SleepUtils.sleepMin());
            if (null != html) {
                Document document = Jsoup.parse(html);
                //source_id	数据来源，值为“kg_covid_datasource”表的 id
                companyInfo.put("source_id", "146");
                //name	公司名称
                companyInfo.put("name", document.select(".com-name").text().trim());
                //company_id	企业唯一标识
                companyInfo.put("company_id", MD5Util.getMD5String(document.select(".com-name").text().trim()));
                Elements memberInfo = document.select(".member-info ul li");
                for (Element e : memberInfo) {
                    if (e.text().trim().contains("联系姓名")) {
                        //contacts	联系人
                        companyInfo.put("contacts", e.select("a").text().trim());
                    } else if (e.text().trim().contains("电话号码")) {
                        //tel	联系电话
                        companyInfo.put("tel", e.select("span").text().trim());
                    } else if (e.text().trim().contains("所在地区")) {
                        //province	省
                        //city	市
                        companyInfo.put("province", e.text().trim().split("：", 2)[1].split("省", 2)[0]);
                        companyInfo.put("city", e.text().trim().split("省", 2)[1].split("市", 2)[0].split("-", 2)[1]);
                    }
                }
                Elements tbody = document.select(".shop-table-con tbody tr td");
                for (Element e : tbody) {
                    if (e.text().trim().contains("员工人数")) {
                        //employees	员工人数
                        companyInfo.put("employees", e.nextElementSibling().text().trim());
                    } else if (e.text().trim().contains("经营地址")) {
                        //adress	公司地址
                        companyInfo.put("adress", e.nextElementSibling().text().trim());
                    } else if (e.text().trim().contains("公司类型")) {
                        //type	公司类型
                        companyInfo.put("type", e.nextElementSibling().text().trim());
                    } else if (e.text().trim().contains("经营模式")) {
                        //model	经营类型
                        companyInfo.put("model", e.nextElementSibling().text().trim());
                    } else if (e.text().trim().contains("主营行业")) {
                        //industry	主营行业
                        companyInfo.put("industry", e.nextElementSibling().text().trim());
                    } else if (e.text().trim().contains("主营产品")) {
                        //product	主营产品
                        companyInfo.put("product", e.nextElementSibling().text().trim());
                    } else if (e.text().trim().contains("品牌名称")) {
                        //brand	企业品牌
                        companyInfo.put("brand", e.nextElementSibling().text().trim());
                    }
                }
                //info	公司简介
                companyInfo.put("info", document.select(".shop-comp-ins.textarea-content").text().trim());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
