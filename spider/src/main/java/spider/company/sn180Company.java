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
import java.util.HashMap;
import java.util.Set;


/**
  * <p>Company: 商牛网</p>
  * @author chenyan
 */
public class sn180Company {

    private final static Logger LOGGER = LoggerFactory.getLogger(trustexporterCompany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }


    private void insertToMySQL(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (updateToMySQL.companyInsert(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        sn180Company sn180Company = new sn180Company();

        sn180Company.classify("http://www.sn180.com/Company/");
        LOGGER.info("---完成了---");
    }


    //分类
    private void classify(String url) {
        try {
            //String html = httpGetWithProxy(url, "商牛网");
            String html = httpGet(url, "商牛");
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("tr > td > p > a");
            for (Element element : elements) {
                if (element.text().trim().contains("电子元器件")) {
                    String attr = element.attr("href");
                    //System.out.println(attr);
                    Thread.sleep(5000);
                    paging(attr);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void paging(String attr) {
        try {

            String html = HttpUtil.httpGetwithJudgeWord(attr, "商牛");
            Document doc = Jsoup.parse(html);
            String elements = doc.select("#PageNumericNavigator1_lblTotalPage").text().trim();
            int i = Integer.parseInt(elements);
            String replace = attr.replace(".html", "");
            for (int j = 0; j <=i; j++) {
                String links = replace.concat("-p").concat(String.valueOf(j)).concat(".html");
                companyList(links);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //企业列表及分页
    private void companyList(String url) {
        try {
            //String html = httpGetWithProxy(url, "商牛网");
            String html = httpGet(url, "商牛");
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("div:nth-child(3) > a");
            for (Element element : elements) {
                if (element.text().contains("+ 联系方式")) {
                    String attr = element.attr("href");
                    companyDetails(attr);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    //企业信息
    private void companyDetails(String url) {
        JSONObject companyInfo = new JSONObject();
        companyInfo.put("website_name","商牛网");
        try {
            //String html = httpGetWithProxy(url, "商牛网");
            String contactInformation = httpGet(url, "商牛网");
            Document doc = Jsoup.parse(contactInformation);
            companyInfo.put("id", MD5Util.getMD5String(doc.select("#namecard_companyname").text()));
            companyInfo.put("name",doc.select("#namecard_companyname").text());
            companyInfo.put("contact",doc.select("#namecard_linkman").text());
            Elements element = doc.select("tr:nth-child(3) > td > table > tbody > tr");
            for (Element details : element) {
               if (details.select("td:nth-child(1) > b").text().contains("电　　话：")){
                   companyInfo.put("landline",details.select("td:nth-child(2)").text().trim());
               }else if (details.select("td:nth-child(1) > b").text().contains("移动电话：")){
                   companyInfo.put("phone",details.select("td:nth-child(2)").text().trim());
               }else if (details.select("td:nth-child(1) > b").text().contains("传　　真：")){
                   companyInfo.put("fax",details.select("td:nth-child(2)").text().trim());
               }else if (details.select("td:nth-child(1) > b").text().contains("邮　　箱：")){
                   companyInfo.put("email",details.select("td:nth-child(2)").text().trim());
               }else if (details.select("td:nth-child(1) > b").text().contains("地　　址：")){
                   companyInfo.put("address",details.select("td:nth-child(2)").text().trim());
               }else if (details.select("td:nth-child(1) > b").text().contains("邮　　编：")){
                   companyInfo.put("postcode",details.select("td:nth-child(2)").text().trim());
               }else if (details.select("td:nth-child(1) > b").text().contains("公司主页：")){
                   companyInfo.put("website",details.select("td:nth-child(2)").text().trim());
               }
            }

            Elements companyNavigationBar = doc.select("tr:nth-child(2) > td > table > tbody > tr > td > a");
            for (Element navigationLink : companyNavigationBar){
                if (navigationLink.text().contains("主 页")){
                    String homeLink = navigationLink.attr("href");
                    companyInfo.put("website",homeLink);
                    String homePage = httpGet(homeLink, "商牛网");
                    Document analysisHomePage = Jsoup.parse(homePage);
                    Elements homePageInfoList = analysisHomePage.select("#AutoNumber7 > tbody > tr > td > div:nth-child(2)");
                    for (Element homePageInfoList1 : homePageInfoList) {
                        if (homePageInfoList1.text().contains("主营产品或服务：")) {
                            Elements list = homePageInfoList1.select("#lblMainproduct");
                            companyInfo.put("main_product", list.text());
                        }
                    }
                    for (Element homePageInfoList1 : homePageInfoList){
                        Elements div = homePageInfoList1.select("div");
                        if (div.text().contains("主营行业：")) {
                            Elements list = div.select("#lblMainlevel");
                            companyInfo.put("industry", list.text());
                        }
                    }
                } else if (navigationLink.text().contains("公司介绍")) {
                    String companyIntroductionLink = navigationLink.attr("href");
                    String companyIntroductionPage = httpGet(companyIntroductionLink, "商牛网");
                    Document analysisCompanyIntroductionPage = Jsoup.parse(companyIntroductionPage);
                    companyInfo.put("company_info", analysisCompanyIntroductionPage.select("#lblIntroduction").text().trim());
                }
            }

            companyInfo.put("crawlerId", "31");
            companyInfo.put("createTime", creatrTime.format(new Date()));
//            insertToMySQL(companyInfo);
            System.out.println(companyInfo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
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
