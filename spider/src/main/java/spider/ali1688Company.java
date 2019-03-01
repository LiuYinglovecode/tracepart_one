//package spider;
//
//import com.alibaba.fastjson.JSONObject;
//import ipregion.ProxyDao;
//import mysql.TxtUpdateToMySQL;
//import org.apache.commons.logging.LogFactory;
//import org.htmlparser.visitors.HtmlPage;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import util.HttpUtil;
//import util.IConfigManager;
//import util.IpProxyUtil;
//import util.MD5Util;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.logging.Level;
//
//import static ipregion.IpRegionTaobao.getProxy;
//
//
//public class ali1688Company {
//    private final static Logger LOGGER = (Logger) LoggerFactory.getLogger(ali1688Company.class);
//    private static java.util.Map<String, String> Map = null;
//    private IpProxyUtil ipProxyList = new IpProxyUtil();
//    private static java.util.Map<String, String> header = null;
//    private static String savePage = "";
//
//    static {
//        header = new HashMap();
//        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
//        header.put("Cookie", "cna=wUKiFOPyah4CATutdVr1QSfq; ali_ab=59.173.117.90.1545813841343.4; UM_distinctid=167f4124cf2349-0c65983c6dfe27-6313363-e1000-167f4124cf3d23; ali_beacon_id=183.92.248.201.1546390937479.114454.7; hng=CN%7Czh-CN%7CCNY%7C156; h_keys=\"%u667a%u80fd%u624b%u673a#%u8033%u673a#%u5916%u8d38%u6cf3%u88c5#%u590d%u5370%u673a#%u540c%u5b66%u5f55#%u6587%u80f8%u65b0%u54c1#%u5185%u88e4%u65b0%u54c1#%u4ea7%u54c1%u56db%u4ef6%u5957#%u6c7d%u8f66%u6302%u4ef6#2018%u79cb%u51ac%u7537%u88c5\"; ali_apache_track=c_mid=b2b-6490458563b5fc|c_lid=tb_721021|c_ms=1; __last_loginid__=tb_721021; lid=tb_721021; l=aB8L9hgpysZGtxsXyMaJQLpDi707HAZPZ1AY1Mwg8TEhNzrsoNSmRCr6-OzwMq8tHrcMcxd2CcSw.; cookie2=12b59af03d7ce9d813d327ed006a6bbd; t=cfe4279e10f7ab0b79f5a4b5b8041295; _tb_token_=54e61be3eb5e5; __cn_logon__=false; ad_prefer=\"2019/01/21 10:23:23\"; alicnweb=touch_tb_at%3D1548037880647%7ChomeIdttS%3D90400678830868150334602480301220697084%7ChomeIdttSAction%3Dtrue%7Clastlogonid%3Dtb_721021; isg=BE1NiNSQLWlJ64kjwKRLhuqmXGkHgoORb7zmu4_SieRThm04V3qRzJsQ9VpFbJm0");
//        header.put("accept-language", "zh-CN,zh;q=0.9");
//        header.put("accept-encoding", "gzip, deflate, br");
//        header.put("accept", "*/*");
//        header.put("scheme", "http");
//    }
//
//    /**
//     * @param url
//     */
//    //1688首页导航栏
//    void category(String url) {
//        try {
//            String html = httpGet(url, "1688");
//            Document document = Jsoup.parse(html);
//            Elements address = document.select("#sub-nav > li.fd-clr > a");
//            for (Element element : address) {
//                if (element != null) {
//                    String companyList = element.attr("href");
//                    write("category : " + companyList);
//                    Thread.sleep(10000);
//                    companyList(companyList);
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//        }
//    }
//
//    //1688子页导航栏
//    private void companyList(String url) {
//        try {
//            String html = httpGet(url, "1688");
//            if (html == null) {
//                String s = "https:" + url;
//                String a = httpGet(s, "1688");
//                Document document = Jsoup.parse(a);
//                Elements address = document.select("div.ch-menu-body > div > div > ul > li > a");
//                for (Element element : address) {
//                    if (element != null) {
//                        String href = element.attr("href");
//                        write("category : " + href);
//                        Thread.sleep(10000);
//                        Paging(href);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//        }
//    }
//
//    //下一页
//    private void Paging(String url){
//        int beginPag = 1;
//        for (beginPag = 1; beginPag < 100; beginPag++) {
//            String beginpag = url + "&beginPag=" + beginPag;
//            sellerList(beginpag);
//        }
//    }
//
//    private void sellerList(String url) {
//        try {
//            String html = httpGet(url, "1688");
//            Document document = Jsoup.parse(html);
//            Elements address = document.select("#sm-offer-list > li > div > div > a");
//            for (Element element : address) {
//                if (element != null) {
//                    String companyList = element.attr("href");
//                    write("category : " + companyList);
//                    //System.out.println("===================================="+companyList);
//                   // Details(companyList);
//                    Record(companyList);
//                    Thread.sleep(10000);
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//        }
//    }
//
//   //公司档案
//    private  void  Record(String url){
//        try {
//        String html = httpGet(url ,"1688");
//        Document parse = Jsoup.parse(html);
//            Element first = parse.select("#topnav > div > ul > li.selected.creditdetail-page > a").first();
//            if (first != null) {
//                String href = first.attr("href");
//                Details(href);
//                Thread.sleep(10000);
//            }else {
//                Element first1 = parse.select("#topnav > div > ul > li.creditdetail-page > a").first();
//                String href1 = first1.attr("href");
//                //System.out.println(href1);
//                Details(href1);
//                Thread.sleep(10000);
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//        }
//    }
//
//
//  //联系方式
//    private void Details(String url) {
//        JSONObject companyInfo = new JSONObject();
//        try {
//            String html = httpGet(url, "联系方式");
//            Document document = Jsoup.parse(html);
//            Element element = document.select("div.top-nav-bar-box ul li.contactinfo-page a").first();
//            String href =  element.attr("href");
// //           System.out.println("href="+href);
//            Thread.sleep(10000);
//            String linkmanHtml =  httpGet(href,"链接");
////            System.out.println("linkmanHtml="+linkmanHtml);
//
//            Document linkman = Jsoup.parse(linkmanHtml);
//           // Element first = linkman.select("div.m-content > div.props-part > div.fd-clr").first();
////            System.out.println("----------------"+first);
//
//            //String company = linkman.select("div.fd-clr > div.contact-info > h4").text();
//            //String contacts = linkman.select("div.fd-clr > div.contact-info > dl > dd > a.membername").text();
//            companyInfo.put("name",linkman.select("div.fd-clr > div.contact-info > h4").text());
//            companyInfo.put("contacts",linkman.select("div.fd-clr > div.contact-info > dl > dd > a.membername").text());
//            companyInfo.put("website",linkman.select("div.fd-clr > div.fd-line > div.contcat-desc > dl > dd > div > a").attr("href")+"/");
//            String key = linkman.select("div.fd-clr > div.fd-line > div.contcat-desc > dl > dt").text();
//            String value = linkman.select("div.fd-clr > div.fd-line > div.contcat-desc > dl > dd").text();
//            System.out.println("---------------------" +key);
//            System.out.println("---------------------" +value);
//
//             Elements dls = linkman.select("div.fd-clr > div.fd-line > div.contcat-desc > dl");
//           //Map<String,String> map = new HashMap<String,String>();
//            for (Element dl:dls) {
//               //String key = dl.select("dt").first().text();
//               //String value  = dl.select("dd").first().text();
//                Elements contactDt = dl.select("dt");
//                Elements contactDd = dl.select("dd");
//                for (int i = 0; i < contactDd.size(); i++) {
//                    if ("电 话：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("landline", contactDd.eq(i).text());
//                    } else if ("移动电话：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("telephone", contactDd.eq(i).text());
//                    } else if ("传 真：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("fax", contactDd.eq(i).text());
//                    } else if ("地 址：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("address", contactDd.eq(i).text());
//                    } else if ("电子邮件：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("emil", contactDd.eq(i).text());
//                    } else if ("邮 编：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("postcode", contactDd.eq(i).text());
//                    } else if ("公司主页：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("website", contactDd.eq(i).text());
//                    } else if ("旺铺主页：".equals(contactDt.eq(i).text())) {
//                        companyInfo.put("website", contactDd.eq(i).text());
//                    }
//                }
//            }
//            insert(companyInfo);
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//        }
//
//    }
//
//
//    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.138:2181");
//        ali1688Company ali1688Company = new ali1688Company();
//        ali1688Company.category("https://www.1688.com/");
//        LOGGER.info("---完成了---");
//    }
//
//
//    private void insert(JSONObject companyInfo) {
//        Map = (java.util.Map) companyInfo;
//        if (TxtUpdateToMySQL.ali1688Update(Map)) {
//            LOGGER.info("插入中 : " + Map.toString());
//        }
//    }
//
//    private String httpGetWithProxy(String url, String judgeWord) {
//        String ipProxy = null;
//        try {
//            if (ipProxyList.isEmpty()) {
//                LOGGER.info("ipProxyList is empty");
//                Set<String> getProxy = getProxy();
//                ipProxyList.addProxyIp(getProxy);
//            }
//            ipProxy = ipProxyList.getProxyIp();
//            String html = null;
//            for (int i = 0; i < 5; i++) {
//                if (url != null && ipProxy != null) {
//                    html = HttpUtil.httpGetWithProxy(url, header, ipProxy);
//                }
//                if (html != null && html.contains(judgeWord)) {
//                    return html;
//                }
//                ipProxyList.removeProxyIpByOne(ipProxy);
//                ProxyDao.delectProxyByOne(ipProxy);
//                ipProxy = ipProxyList.getProxyIp();
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return null;
//    }
//
//    private String httpGet(String url, String judgeWord) {
//        try {
//            String html = null;
//            for (int i = 0; i < 5; i++) {
//                if (null != url) {
//                    html = HttpUtil.httpGet(url, header);
//                }
//                if (html != null && html.contains(judgeWord)) {
//                    return html;
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        return null;
//    }
//
//    private void write(String file) throws Exception {
//        try {
//            FileWriter out = new FileWriter(savePage, true);
//            out.write(String.valueOf(file));
//            out.write("\r\n");
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
//        }
//    }
//}
