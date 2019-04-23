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
import util.*;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class taojindiCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(taojindiCompany.class);
    private static Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static Map<String, String> header = null;
    private static String savePage = "";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
    }

    /*
     * 通过企业大全页面拿到行业企业名录大全,
     * 在通过行业企业名录大全企业名录url拿到相关公司的url
     */
    private void category(String url) {
        try {
            //通过企业大全页面拿到行业企业名录大全,
            String html = httpGetWithProxy(url, "淘金地");
            Document document = Jsoup.parse(html);
            Elements address = document.select("div.info-list.info-list5 > ul > li > a");
            for (Element element : address) {
                String href = "http://hy.taojindi.com" + (element.attr("href"));
                //在通过行业企业名录大全企业名录url拿到相关公司的url
                String html1 = httpGetWithProxy(href, "淘金地");
                Document parse = Jsoup.parse(html1);
                Elements select = parse.select("body > div > div:nth-child(6) > div.info-list.info-list6 > ul.clearfix > li > a");
                for (Element element1 : select) {
                    String herf = element1.attr("href");
                    Paging(herf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 对每一页数据进行循环,拿出每个企业的店铺url
     * 每一页的数据拿完后进去下一页继续
     * */
    private void Paging(String url) {
        try {
            //对每一页数据进行循环,拿出每个企业的店铺url
            String html = httpGetWithProxy(url, "淘金地");
            Document parse = Jsoup.parse(html);
            Elements address = parse.select("ul > li > div.description > div.til > a.title");
            for (Element page : address) {
                String href = page.attr("href");
                companyInformation(href);
            }
            //下一页
            Elements select = parse.select("div.s-main.clearfix > div.paging.mb30 > a");
            for (Element element : select) {
                if ("下一页".equals(element.text())) {
                    Paging(("http://www.taojindi.com" + element.attr("href")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 找到联系我们的url
     */
    private void companyInformation(String url) {
        JSONObject companyInfo = new JSONObject();
        //companyInfo.put("website_name","淘金地网站");
        try {
            String html = httpGetWithProxy(url, "淘金地");
            Document parse = Jsoup.parse(html);
            //公司名称
            companyInfo.put("name", parse.select("div.bd > div> div.name.highlight > a").text().trim());
            //对公司名称进行加密
            //companyInfo.put("MD5_id", MD5Util.getMD5String(parse.select("div.bd > div> div.name.highlight > a").text().trim()));
            companyInfo.put("id", MD5Util.getMD5String(parse.select("div.bd > div> div.name.highlight > a").text().trim()));
            //联系人
            companyInfo.put("contact", parse.select("div.bd > div > div.clearfix > a.mr5").text().trim());
            //主营产品
            companyInfo.put("industry", parse.select("div.bd > div.border_b.pl20.pr20.pt5.pb5 > div > div.main_pro_con").text().trim());
            Elements select = parse.select("body > div.main_nav > div > ul li a");
            for (Element element : select) {
                if ("联系我们".equals(element.text())) {
                    String href = element.attr("href");
                    String contactWe = httpGetWithProxy(href, "联系我们");
                    Document parse1 = Jsoup.parse(contactWe);
                    Elements select2 = parse1.select("div.contact_list.contact_list01 > ul > li > span");
                    for (Element element2 : select2) {
                        if (element2.text().contains("地址：")) {
                            companyInfo.put("address", element2.text().split("：", 2)[1].trim());
                        }
                    }
                    Elements select1 = parse1.select("div.contact_list.contact_list01 > ul > li");
                    for (Element element1 : select1) {
                        Thread.sleep(1000);
                        //Sleep.threadSleep(1-10);
                        if (element1.text().contains("邮编：")) {
                            companyInfo.put("postcode", element1.text().split("：", 2)[1].trim());
                        } else if (element1.text().contains("QQ：")) {
                            companyInfo.put("qq", element1.text().split("：", 2)[1].trim());
                        } else if (element1.text().contains("固话：")) {
                            companyInfo.put("landline", element1.text().split("：", 2)[1].trim());
                        } else if (element1.text().contains("传真：")) {
                            companyInfo.put("fax", element1.text().split("：", 2)[1].trim());
                        } else if (element1.text().contains("手机：")) {
                            companyInfo.put("phone", element1.text().split("：", 2)[1].trim());
                        } else if (element1.text().contains("公司网址：")) {
                            companyInfo.put("website", element1.text().split("：", 2)[1].trim());
                        }
                    }
                }
            }
            companyInfo.put("crawlerId", "11");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            insert(companyInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        taojindiCompany taojindiCompany = new taojindiCompany();
        taojindiCompany.category("http://hy.taojindi.com/");
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

