package parse.company.download;

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


public class AtoboToDownload {
    private static final Logger LOGGER = LoggerFactory.getLogger(QinCaiDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public static void main(String[] args) {
        AtoboToDownload atoboToDownload = new AtoboToDownload();
        atoboToDownload.productDetails("http://24197xckc.atobo.com");
    }


    public void productDetails(String href) {

        JSONObject companyInfo = new JSONObject();
        try {
//            String html = httpGetWithProxy(href, "阿土伯");
            String html = util.HttpUtil.httpGetwithJudgeWord(href, "阿土伯");
            Document parse = Jsoup.parse(html);
            companyInfo.put("id", MD5Util.getMD5String(parse.select("li.logotext p em").text()));
            companyInfo.put("name", parse.select("li.logotext p em").text().trim());
//            companyInfo.put("website_name","阿土伯网");

            //关于我们，公司介绍
            Elements navigationBar = parse.select("div.my-menu ul li.menuitem a");
            for (Element element : navigationBar) {
                if (element.text().equals("关于我们")) {
                    String href1 = href + element.attr("href");

                    String html1 = util.HttpUtil.httpGetwithJudgeWord(href1, "阿土伯");
                    Document document = Jsoup.parse(html1);
                    Elements elements = document.select("div.left-frame:nth-child(1) > div:nth-child(2) > div:nth-child(1)");
                    companyInfo.put("company_info", elements.text());
                }

                //联系我们
                else if (element.text().equals("联系我们")) {
                    String href2 = href + element.attr("href");
                    String html2 = util.HttpUtil.httpGetwithJudgeWord(href2, "阿土伯");
                    Document document = Jsoup.parse(html2);
                    Elements select = document.select("li.mess-left");
                    for (Element element1 : select) {
                        if (element1.text().contains("联系人：")) {
                            element1.nextElementSibling().select("span").remove();
                            companyInfo.put("contact", element1.nextElementSibling().text().trim());
                        } else if (element1.text().contains("电话：")) {
                            element1.nextElementSibling().select("span").remove();
                            companyInfo.put("landline", element1.nextElementSibling().text().trim());
                        } else if (element1.text().contains("手机：")) {
                            element1.nextElementSibling().select("span").remove();
                            companyInfo.put("phone", element1.nextElementSibling().text().trim());
                            element1.nextElementSibling().select("span").remove();
                        } else if (element1.text().contains("传真：")) {
                            element1.nextElementSibling().select("span").remove();
                            companyInfo.put("fax", element1.nextElementSibling().text().trim());
                        } else if (element1.text().contains("地址：")) {
                            element1.nextElementSibling().select("span").remove();
                            companyInfo.put("address", element1.nextElementSibling().text().trim());
                        } else if (element1.text().contains("邮编：")) {
                            element1.nextElementSibling().select("span").remove();
                            companyInfo.put("postcode", element1.nextElementSibling().text().trim());
                        } else if (element1.text().contains("网址：")) {
                            element1.nextElementSibling().select("span").remove();
                            companyInfo.put("website", element1.nextElementSibling().text().trim());
                        }
                    }
                    Elements select1 = document.select("div.card-context ul");
                    for (Element element1 : select1) {
                        if (element1.select("li.card-left").text().contains("主营：")) {
                            String text = element1.select("li.card-right").text();
                            companyInfo.put("industry", text);
                        }
                    }
                    Element last = document.select("li.card-cert > a").last();
                    if (last.text().contains("工商信息")) {
                        String attr = last.attr("href");
                        String businessinfo = HttpUtil.httpGetwithJudgeWord(attr, "阿土伯");
                        Document business = Jsoup.parse(businessinfo);
                        Elements info = business.select("td.rowtitle");
                        for (Element e : info) {
                            if (e.text().contains("法人代表：")) {
                                e.nextElementSibling().select("span").remove();
                                companyInfo.put("incorporator", e.nextElementSibling().text().trim());
                            } else if (e.text().contains("注册资本：")) {
                                e.nextElementSibling().select("span").remove();
                                companyInfo.put("register_capital", e.nextElementSibling().text().trim());
                            } else if (e.text().contains("注 册 号：")) {
                                e.nextElementSibling().select("span").remove();
                                companyInfo.put("from_where_table_id", e.nextElementSibling().text().trim());
                            } else if (e.text().contains("企业类型：")) {
                                e.nextElementSibling().select("span").remove();
                                companyInfo.put("company_model", e.nextElementSibling().text().trim());
                            } else if (e.text().contains("注册地址：")) {
                                e.nextElementSibling().select("span").remove();
                                companyInfo.put("register_address", e.nextElementSibling().text().trim());
                            } else if (e.text().contains("成立日期：")) {
                                e.nextElementSibling().select("span").remove();
                                companyInfo.put("company_register_time", e.nextElementSibling().text().trim());
                            } else if (e.text().contains("经营范围：")) {
                                e.nextElementSibling().select("span").remove();
                                companyInfo.put("industry", e.nextElementSibling().text().trim());
                            }
                        }
                    }
                }
            }

            companyInfo.put("crawlerId", "12");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            if (mysqlUtil.insertCompany(companyInfo)) {
                RedisUtil.insertUrlToSet("catchedUrl-Company", href);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
