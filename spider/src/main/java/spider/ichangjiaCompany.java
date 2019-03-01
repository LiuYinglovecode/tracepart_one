package spider;

import com.alibaba.fastjson.JSONObject;
import ipregion.ProxyDao;
import mysql.TxtUpdateToMySQL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.IConfigManager;
import util.IpProxyUtil;
import util.MD5Util;

import java.io.FileWriter;
import java.util.Map;
import java.util.Set;



public class ichangjiaCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(ichangjiaCompany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static Map<String, String> header = null;
    private static String savePage = "";

    private void category(String url){
        try {
            String html = httpGet(url, "厂家贸易网");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.m_l_1.f_l > div > div.company2-c > h1 > a");
            for (Element element : select) {
                //Thread.sleep(5000);
                String attr = element.attr("href");
                System.out.println(attr);
                //Thread.sleep(3000);
                contact(attr);
            }
            //下一页
            Elements s = parse.select("div.m_l_1.f_l > div.pages > a");
            for (Element element : s) {
                if ("下一页»".equals(element.text())) {
                    String href = element.attr("href");
                    System.out.println("下一页："+href);
                    category(href);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contact(String url){
        JSONObject companyInfo = new JSONObject();
        try {
            companyInfo.put("website",url);
            String html = httpGet(url, "公司介绍");
            Document htmlFile = Jsoup.parse(html);
            Elements select1 = htmlFile.select("#main > table > tbody > tr> td");
            for (Element e : select1) {
                if (e.text().contains("主营：")){
                    companyInfo.put("management_model", e.text().split("：", 2)[1]);
                }else if (e.text().contains("公司类型：")){
                    companyInfo.put("type", e.text().split("：", 2)[1]);
                }else if (e.text().contains("注册年份：")){
                    companyInfo.put("company_register_time", e.text().split("：", 2)[1]);
                }
            }
            Elements elements = htmlFile.select("#menu > ul > li > a");
            for (Element e : elements){
                //Thread.sleep(2000);
                if ("公司介绍".equals(e.text())){
                    String attr = e.attr("href");
                    String fileHtml = httpGet(attr, "公司介绍");
                    Document document = Jsoup.parse(fileHtml);
                    companyInfo.put("company_info",document.select("#main > div > div > table > tbody > tr > td > pre").text());
                    companyInfo.put("company_info",document.select("tbody > tr > td > p > span").text());
                    companyInfo.put("address",document.select("body > div.mlo > div > div > div > strong > a").text());
                    companyInfo.put("name", document.select("body > div.mlo > div > div > div > strong > a").text().trim());
                    companyInfo.put("id", MD5Util.getMD5String(document.select("body > div.mlo > div > div > div > strong > a").text().trim()));
                    Elements select = document.select("#side > div > ul > li");
                    for (Element element : select){
                        if (element.text().contains("联系人：")) {
                            companyInfo.put("contact", element.text().split("：", 2)[1].trim());
                        } else if (element.text().contains("电话：")) {
                            companyInfo.put("landline", element.text().split("：", 2)[1].trim());
                        }else if (element.text().contains("手机：")) {
                            companyInfo.put("phone", element.text().split("：", 2)[1].trim());
                        }else if (element.text().contains("邮件：")) {
                            companyInfo.put("email", element.text().split("：", 2)[1].trim());
                        }else if (element.text().contains("传真：")) {
                            companyInfo.put("fax", element.text().split("：", 2)[1].trim());
                        }
                    }
                }
            }

            insert(companyInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.138:2181");
        ichangjiaCompany ichangjiaCompany = new ichangjiaCompany();
        ichangjiaCompany.category("http://www.ichangjia.com/company/search.php?catid=0&kw=");
        LOGGER.info("---完成了---");
    }


    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (TxtUpdateToMySQL.taojindiUpdate(Map)) {
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
