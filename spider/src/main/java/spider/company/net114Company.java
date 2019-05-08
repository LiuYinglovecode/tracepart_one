package spider.company;

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
import config.IConfigManager;
import util.IpProxyUtil;
import util.MD5Util;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class net114Company {
    private final static Logger LOGGER = LoggerFactory.getLogger(net114Company.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    void category(String url) {
        try {
            String html = httpGet(url, "网络114");
            Document document = Jsoup.parse(html);
            Elements select = document.select("ul.enterprise_nav_list li p a.plftcat");
            for (Element link : select) {
                String href = link.attr("href");
                String page = httpGet(href, "网络114");
                Document document1 = Jsoup.parse(page);
                Elements elements = document1.select("ul.product_w369_list li p a");
                for (Element links : elements) {
                    String href1 = links.attr("href");
                    Thread.sleep(4000);
                    company(href1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void company(String url) {
        try {
            String html = httpGet(url, "网络114");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.enterprise_list_text p a");
            for (Element link : select) {
                String href = link.attr("href");
                Thread.sleep(4000);
                info(href);
            }
            //下一页
            Elements paging = document.select("span.page_ico.page_ico_r a");
            for (Element element : paging) {
                if ("下一页".equals(element.text())) {
                    String links = "http://corp.net114.com" + element.attr("href");
                    System.out.println("下一页：" + links);
                    Thread.sleep(4000);
                    company(links);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void info(String url) {
        JSONObject companyInfo = new JSONObject();
        //companyInfo.put("website_name", "网络114网站");
        try {
            String html = httpGet(url, "网络114");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.bg_white.p_7 div.div_text p");
            if (select.size() == 0) {
                companyInfo.put("name", document.select("#detail_left_content > div.p_10_0 > div.right.w_453.enterprise_details > h1").text());
                companyInfo.put("id", MD5Util.getMD5String(document.select("#detail_left_content > div.p_10_0 > div.right.w_453.enterprise_details > h1").text()));
                companyInfo.put("company_info", document.select("#corp_deion").text());
                Elements select1 = document.select("#detail_left_content > div.p_10_0 > div.right.w_453.enterprise_details > p");
                for (Element link : select1) {
                    if (link.text().contains("经营模式：")) {
                        link.select("b").remove();
                        companyInfo.put("management_model", link.text());
                    } else if (link.text().contains("详细地址：")) {
                        link.select("b").remove();
                        companyInfo.put("address", link.text());
                    } else if (link.text().contains("年 产 值：")) {
                        link.select("b").remove();
                        companyInfo.put("company_turnover", link.text());
                    } else if (link.text().contains("员工人数：")) {
                        link.select("b").remove();
                        companyInfo.put("employees", link.text());
                    } else if (link.text().contains("成立时间：")) {
                        link.select("b").remove();
                        companyInfo.put("company_register_time", link.text());
                    } else if (link.text().contains("联系人：")) {
                        link.select("b").remove();
                        companyInfo.put("contact", link.text());
                    } else if (link.text().contains("联系电话：")) {
                        link.select("b").remove();
                        companyInfo.put("phone", link.text());
                    } else if (link.text().contains("厂房面积：")) {
                        link.select("b").remove();
                        companyInfo.put("company_area", link.text());
                    }
                }
            }
            else {
                for (Element element : select) {
                    if (element.text().contains("联系人")) {
                        companyInfo.put("contact", element.text().split("：", 2)[1].trim());
                    } else if (element.text().contains("电 话")) {
                        companyInfo.put("landline", element.text().split("：", 2)[1].trim());
                    } else if (element.text().contains("传 真")) {
                        companyInfo.put("fax", element.text().split("：", 2)[1].trim());
                    } else if (element.text().contains("网 址")) {
                        companyInfo.put("website", element.text().split("：", 2)[1].trim());
                    } else if (element.text().contains("地 址")) {
                        companyInfo.put("address", element.text().split("：", 2)[1].trim());
                    }
                }

                Thread.sleep(4000);
                Elements select2 = document.select("ul.nav_list li a");
                for (Element e : select2) {
                    if (e.text().equals("企业介绍")) {
                        String href = e.attr("href");
                        String page = httpGet(href, "企业介绍");
                        Document file = Jsoup.parse(page);
                        companyInfo.put("company_info", file.select("div.p_10.about_div p").text());
                    }
                }

                Thread.sleep(4000);
                Elements s = document.select("ul.nav_list li a");
                companyInfo.put("name", document.select("div.top_text_title").text());
                companyInfo.put("id", MD5Util.getMD5String(document.select("div.top_text_title").text()));
                for (Element e : s) {
                    if (e.text().equals("联系我们")) {
                        String href = e.attr("href");
                        String page = httpGet(href, "联系我们");
                        Document file = Jsoup.parse(page);
                        Elements elements = file.select("#form > table > tbody > tr > td > p");
                        for (Element element : elements) {
                            if (element.text().contains("手机")) {
                                companyInfo.put("phone", element.text().split("：", 2)[1].trim());
                            }
                        }
                    }
                }
            }
            companyInfo.put("crawlerId", "9");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            insert(companyInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        net114Company net114Company = new net114Company();
        net114Company.category("http://corp.net114.com/");
        //net114Company.info("http://detail.net114.com/gongsi/766483985.html");
        LOGGER.info("---完成了---");
    }


    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (updateToMySQL.dataUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    //代理
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

    private String httpGet(String url, String judgeWord) {
        try {
            String html = null;
            for (int i = 0; i < 5; i++) {
                if (null != url) {
                    html = HttpUtil.httpGet(url, header);
                }
                if (html != null && html.contains(judgeWord)) {
                    return html;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private static Set<String> getProxy() {
        return ProxyDao.getProxyFromRedis();
    }

    private void write(String file) throws Exception {
        try {
            FileWriter out = new FileWriter(savePage, true);
            out.write(String.valueOf(file));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
