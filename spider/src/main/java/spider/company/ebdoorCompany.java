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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 *<p>Company: 一比多</p>
 *@author chenyan
 */
public class ebdoorCompany {

    private final static Logger LOGGER = LoggerFactory.getLogger(ebdoorCompany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void industryList(String url) {
        try {
            String html = httpGet(url, "一比多");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("dd > ul > li > b > a");
            for (Element e : select){
                String link = "http://shop.ebdoor.com/"+e.attr("href");
//                System.out.println(link);
                //Thread.sleep(3000);
                nextPage(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextPage(String url) {
        //        http://shop.ebdoor.com/ShopList/015.aspx
        String replace = url.replace(".aspx", "");
        int nextPage = 1;
        for (nextPage = 1;nextPage < 20835 ;nextPage++) {
            String link = replace + "," +nextPage+ ".aspx";
            System.out.println("下一页："+link);
            companyList(link);
        }
    }

    private void companyList(String url) {

        try {
            String html = httpGet(url, "一比多");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.Report_Main_Title > a:nth-child(1)");
            for (Element e : select) {
                String link = e.attr("href");
                companyinfo(link);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void companyinfo(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = httpGet(url, "一比多");
            Document parse = Jsoup.parse(html);
            companyInfo.put("id", MD5Util.getMD5String(parse.select(".Enc > tbody > tr:nth-child(1) > td > span").text().trim()));
            companyInfo.put("name",parse.select(".Enc > tbody > tr:nth-child(1) > td > span").text().trim());
            Elements company_info = parse.select("td.Link_W");
            if (company_info.size()!=0){
                companyInfo.put("company_info",company_info.text().trim());
            }else {
                companyInfo.put("company_info",parse.select(".Dec_Font_Style").text().trim());
            }
            Elements select = parse.select(".Enc tr");
            for (Element e : select) {
                if (e.select("td.Col777").text().contains("联 系 人：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("contact",e.select("td").text().trim());
                } else if (e.select("td.Col777").text().contains("电 话：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("landline",e.select("td").text().trim());
                } else if (e.select("td.Col777").text().contains("手 机：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("phone",e.select("td").text().trim());
                } else if (e.select("td.Col777").text().contains("传 真：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("fax",e.select("td").text().trim());
                } else if (e.select("td.Col777").text().contains("地 址：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("address",e.select("td").text().trim());
                } else if (e.select("td.Col777").text().contains("邮 编：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("postcode",e.select("td").text().trim());
                } else if (e.select("td.Col777").text().contains("邮箱地址：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("email",e.select("td").text().trim());
                } else if (e.select("td.Col777").text().contains("公司网址：")){
                    e.select("td.Col777").remove();
                    companyInfo.put("website",e.select("td").text().trim());
                }
            }
            companyInfo.put("crawlerId", "39");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            insert(companyInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        ebdoorCompany ebdoorCompany = new ebdoorCompany();
        ebdoorCompany.industryList("http://shop.ebdoor.com/");
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
