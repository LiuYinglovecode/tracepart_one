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
import java.util.Map;
import java.util.Set;

/**
  * <p>Company: 阿土伯</p>
  * @author chenyan
 */

public class atoboCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(atoboCompany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static Map<String, String> header = null;
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
        atoboCompany atoboCompany = new atoboCompany();
        atoboCompany.productNavigationBar("http://www.atobo.com.cn/Companys/");
        LOGGER.info("---完成了---");
    }

    private void productNavigationBar(String url) {

        try {
//            String html = httpGetWithProxy(url, "阿土伯");
            String html = httpGet(url, "阿土伯");
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("div.filterMode div.filterlist ul li.alist div ul li a");
            for (Element element : elements) {
    //            System.out.println(element);
                    String href = element.attr("href");
                    String link = "http://www.atobo.com.cn" + href;
                    Thread.sleep(8000);
                    reClassification(link);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reClassification(String link1) {
        try {
//            String html2 = httpGetWithProxy(link1, "阿土伯");
            String html2 = httpGet(link1, "阿土伯");
            Document doc2 = Jsoup.parse(html2);
            Elements elements3 = doc2.select("#filterArea ul li a");
            for (Element element3 : elements3) {
                //最终按照地区分类进入公司主页面
                String href2 = element3.attr("href");
                //System.out.println(href2);
                String link2 = "http://www.atobo.com.cn" + href2;
                Thread.sleep(8000);
                productList(link2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void productList(String link2) {
        try {
//            String html = httpGetWithProxy(link2, "阿土伯");
            String html = httpGet(link2, "阿土伯");
            Document parse = Jsoup.parse(html);
            Elements list = parse.select("li.product_box div ul li.p_name div ul li.pp_2web a");
            for (Element element : list) {
                //获取企业主页面
                if (element.text().equals("分公司主页")){
                    String href = element.attr("href");
                    productDetails(href);
                }else if (element.text().equals("公司主页")){
                    String href = element.attr("href");
                    productDetails(href);
                }else if (element.text().equals("单位主页")){
                    String href = element.attr("href");
                    productDetails(href);
                }else if (element.text().equals("店铺主页")){
                    String href = element.attr("href");
                    Thread.sleep(8000);
                    productDetails(href);
                }
            }

            //下一页
            Elements elements = parse.select("span.page_next.page-n > a");
            for (Element element : elements) {
                if (element.text().equals("下一页")){
                    String href = element.attr("href");
                    String attr = "http://www.atobo.com.cn"+href;
                    System.out.println("下一页："+attr);
                    //停10秒
                    Thread.sleep(10000);
                    productList(attr);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void productDetails(String href) {

        JSONObject companyInfo = new JSONObject();
        try {
//            String html = httpGetWithProxy(href, "阿土伯");
            String html = httpGet(href, "阿土伯");
            Document parse = Jsoup.parse(html);
            companyInfo.put("id", MD5Util.getMD5String(parse.select("li.logotext p em").text()));
            companyInfo.put("name",parse.select("li.logotext p em").text().trim());
//            companyInfo.put("website_name","阿土伯网");

            //关于我们，公司介绍
            Elements navigationBar = parse.select("div.my-menu ul li.menuitem a");
            for (Element element : navigationBar) {
                if (element.text().equals("关于我们")) {
                    String href1 = href+element.attr("href");
                    Thread.sleep(8000);
//                    String html1 = httpGetWithProxy(href1, "阿土伯");
                    String html1 = httpGet(href1, "阿土伯");
                    Document document = Jsoup.parse(html1);
                    Elements elements = document.select("div.left-frame:nth-child(1) > div:nth-child(2) > div:nth-child(1)");
                    companyInfo.put("company_info", elements.text());
                }
                //联系我们
                else if (element.text().equals("联系我们")) {
                    String href2 = href + element.attr("href");
                    Thread.sleep(8000);
//                    String html2 = httpGetWithProxy(href2, "阿土伯");
                    String html2 = httpGet(href2, "阿土伯");
                    Document document = Jsoup.parse(html2);
//                    System.out.println(document.text());
                    Elements select = document.select("html body div.contextf div.g-o div.cont-ronqi div.cont-f div.left-frame div.homep-list div.messages ul");
                    for (Element element1 : select) {
                        if (element1.select("li.mess-left").text().contains("联系人：")) {
                            companyInfo.put("contact", element1.select("li.mess-right").text().trim());
                        } else if (element1.select("li.mess-left").text().contains("电话：")) {
                            companyInfo.put("landline", element1.select("li.mess-right").text().trim());
                        } else if (element1.select("li.mess-left").text().contains("手机：")) {
                            companyInfo.put("phone", element1.select("li.mess-right").text().trim());
                        } else if (element1.select("li.mess-left").text().contains("传真：")) {
                            companyInfo.put("fax", element1.select("li.mess-right").text().trim());
                        } else if (element1.select("li.mess-left").text().contains("地址：")) {
                            companyInfo.put("address", element1.select("li.mess-right").text().trim());
                        } else if (element1.select("li.mess-left").text().contains("邮编：")) {
                            companyInfo.put("postcode", element1.select("li.mess-right").text().trim());
                        } else if (element1.select("li.mess-left").text().contains("网址：")) {
                            companyInfo.put("website", element1.select("li.mess-right").text().trim());
                        }
                    }
                    Elements select1 = document.select("div.card-context ul");
                    for (Element element1 : select1) {
                        if (element1.select("li.card-left").text().contains("主营：")){
                            String text = element1.select("li.card-right").text();
                            companyInfo.put("industry",text);
                        }
                    }
                }
            }

            companyInfo.put("crawlerId", "12");
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
