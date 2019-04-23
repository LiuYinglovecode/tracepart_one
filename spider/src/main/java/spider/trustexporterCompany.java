package spider;

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
import java.util.Map;
import java.util.Set;

/**
 * <p>Company: 万国网</p>
 * @author chenyan
 */
public class trustexporterCompany {

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
        Map = (Map) companyInfo;
        if (updateToMySQL.dataUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        trustexporterCompany trustexporterCompany = new trustexporterCompany();
        trustexporterCompany.industryNavigationBar("http://qiye.trustexporter.com/");
        LOGGER.info("---完成了---");
    }

    /**
     * 行业导航栏：industryNavigationBar
     * @param url
     */
    private void industryNavigationBar(String url) {

        try {
            //String html = httpGetWithProxy(url, "trustexporter.com");
            String html = httpGet(url, "trustexporter.com");
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("div > dl > dd > a");
            for (Element element : elements) {
                String attr = element.attr("href");
                //Thread.sleep(5000);
                companyList(attr);
                Paging(attr);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 公司列表：companyList
     * 下一页：nextPage
     * @param url
     */
    private void companyList(String url) {
        try {
            //String html = httpGetWithProxy(url, "trustexporter.com");
            String html = httpGet(url, "trustexporter.com");
            Document doc = Jsoup.parse(html);
            if (doc!=null) {
                Elements elements = doc.select("tbody > tr > td > ul > li > a");
                for (Element element : elements) {
                    String attr = element.attr("href");
                    Thread.sleep(5000);
                    companyDetails(attr);
                }
            }else {
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //下一页
    private void Paging(String url){
        int beginPag = 1;
        for (beginPag = 2; beginPag < 200; beginPag++) {
            String beginpag = url + "pn"+ beginPag + ".htm";
            System.out.println("下一页："+beginpag);
            companyList(beginpag);
        }
    }

    /**
     * 公司信息：companyDetails
     * @param url
     */
    private void companyDetails(String url) {
        JSONObject companyInfo = new JSONObject();
//        companyInfo.put("website_name","万国网");
        try {
            //String html = httpGetWithProxy(url, "trustexporter.com");
            String html = httpGet(url, "trustexporter.com");
            Document doc = Jsoup.parse(html);
            companyInfo.put("id", MD5Util.getMD5String(doc.select("#logoi").text()));
            companyInfo.put("name",doc.select("#logoi").text());
            Elements element = doc.select("#menu ul li a");

            for (Element el : element) {
//                联系方式
                if (el.text().contains("联系我们")){
                    String href = el.attr("href");
                    //String html2 = httpGetWithProxy(href, "trustexporter.com");
                    String contactUs = httpGet(href, "trustexporter.com");
                    Document document = Jsoup.parse(contactUs);
                    Elements tr = document.select("div.px13.lh18 table tbody tr");
                    for (Element e : tr) {
                        Elements td1 = e.select("td:nth-child(1)");
                        Elements td2 = e.select("td:nth-child(2)");
                        for (int i = 0; i < td2.size(); i++) {
                            if ("公司名称：".equals(td1.eq(i).text())) {
                                companyInfo.put("name", td2.eq(i).text());
                                companyInfo.put("id", MD5Util.getMD5String(td2.eq(i).text()));
                            } else if ("公司地址：".equals(td1.eq(i).text())) {
                                companyInfo.put("address", td2.eq(i).text());
                            } else if ("手机号码：".equals(td1.eq(i).text())) {
                                companyInfo.put("phone", td2.eq(i).text());
                            } else if ("邮政编码：".equals(td1.eq(i).text())) {
                                companyInfo.put("postcode", td2.eq(i).text());
                            } else if ("公司电话：".equals(td1.eq(i).text())) {
                                companyInfo.put("landline", td2.eq(i).text());
                            } else if ("公司传真：".equals(td1.eq(i).text())) {
                                companyInfo.put("fax", td2.eq(i).text());
                            } else if ("电子邮件：".equals(td1.eq(i).text())) {
                                companyInfo.put("email", td2.eq(i).text());
                            } else if ("联 系 人：".equals(td1.eq(i).text())) {
                                companyInfo.put("contact", td2.eq(i).text());
                            } else if ("公司网址：".equals(td1.eq(i).text())) {
                                companyInfo.put("website", td2.eq(i).text());
                            }
                        }
                    }
                }else {
//                    公司简介
                    if (el.text().contains("企业介绍")) {
                        String href = el.attr("href");
                        //String conpanyiIntroduce = httpGetWithProxy(href, "trustexporter.com");
                        String conpanyiIntroduceHtml = httpGet(href, "trustexporter.com");
                        Document conpanyiIntroduce = Jsoup.parse(conpanyiIntroduceHtml);
                        Elements select = conpanyiIntroduce.select("p.MsoNormal");
                        if (select.size()==0){
                            companyInfo.put("company_info",conpanyiIntroduce.select("div:nth-child(3) > div > table > tbody > tr > td").text());
                        }else {
                            companyInfo.put("company_info", conpanyiIntroduce.select("p.MsoNormal").text());

                        }
                    }
                }
            }
            companyInfo.put("crawlerId", "14");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            insertToMySQL(companyInfo);

        } catch (Exception e) {
            e.printStackTrace();
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
