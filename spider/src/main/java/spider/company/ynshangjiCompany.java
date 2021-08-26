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
import util.IpProxyUtil;
import util.MD5Util;

import java.io.FileWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


/**
  * <p>Company: 云商网</p>
  * @author chenyan
 */
public class ynshangjiCompany {

    private final static Logger LOGGER = LoggerFactory.getLogger(ynshangjiCompany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //    产品导航栏及分类
    private void productNavigationAndClassification(String url) {
        try {
            String httpGet = httpGet(url, "ynshangji");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK",httpGet);
            Elements select = parse.select("div.con > dl > dd > a");
            for (Element element : select) {
                String href = "http://www.ynshangji.com"+element.attr("href");
                //System.out.println(href);
                companyList(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void companyList(String url) {
        try {
            String httpGet = httpGet(url, "ynshangji");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK",httpGet);
            Elements select = parse.select("div.description > div.til > a");
            for (Element element : select) {
                String href = "http://www.ynshangji.com"+element.attr("href");
                //System.out.println(href);
                companyDetails(href);
            }

            Elements nextPage = parse.select("div.paging.mb30 > a");
            for (Element page : nextPage){
                if (page.text().contains("下一页")){
                    String href ="http://www.ynshangji.com"+page.attr("href");
                    System.out.println("下一页："+href);
                    companyList(href);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void companyDetails(String url) {
        JSONObject companyInfo = new JSONObject();
//        companyInfo.put("website_name","云商网");
        try {
            String httpGet = httpGet(url, "ynshangji");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK",httpGet);
            Elements select = parse.select("#ciTxt");
            companyInfo.put("company_info",select.text());
            Elements select1 = parse.select("div.aiMain > ul > li");
            for (Element element : select1) {
                if (element.select("i").text().contains("公司名称")){
                    element.select("i").remove();
                    companyInfo.put("name",element.text());
                    companyInfo.put("id", MD5Util.getMD5String(element.text()));
                }else if (element.select("i").text().contains("联系人")){
                    element.select("i").remove();
                    companyInfo.put("contact",element.text());
                }else if (element.select("i").text().contains("公司地址")){
                    element.select("i").remove();
                    companyInfo.put("address",element.text());
                }else if (element.select("i").text().contains("联系电话")){
                    element.select("i").remove();
                    companyInfo.put("landline",element.text());
                }else if (element.select("i").text().contains("联系手机")){
                    element.select("i").remove();
                    companyInfo.put("phone",element.text());
                }else if (element.select("i").text().contains("联系QQ")){
                    element.select("i").remove();
                    companyInfo.put("qq",element.text());
                }else if (element.select("i").text().contains("企业商铺")){
                    element.select("i").remove();
                    companyInfo.put("website",element.text());
                }else if (element.select("i").text().contains("主营业务")){
                    element.select("i").remove();
                    companyInfo.put("industry",element.text());
                }
            }
            companyInfo.put("crawlerId", "10");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            insert(companyInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        ynshangjiCompany ynshangjiCompany = new ynshangjiCompany();
        ynshangjiCompany.productNavigationAndClassification("http://www.ynshangji.com/qiye/");
        LOGGER.info("---完成了---");
    }


    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (updateToMySQL.companyInsert(Map)) {
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
