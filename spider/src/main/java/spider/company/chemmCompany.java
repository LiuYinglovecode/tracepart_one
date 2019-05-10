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
 *<p>Company: 中国化工机械网</p>
 *@author chenyan
 */
public class chemmCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(net114Company.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void category(String url) {
        //            网页信息字段
        JSONObject datasourceInfo = new JSONObject();
        datasourceInfo.put("id","29");
        datasourceInfo.put("website","中国化工机械网");
        datasourceInfo.put("url","http://www.chemm.cn/");
        datasourceInfo.put("type","company");
        datasourceInfo.put("createTime",creatrTime.format(new Date()));
        datasource(datasourceInfo);
        try {
            String html = httpGet(url, "chemm");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("#CompanyArea > ul > li > a");
            for (Element e : select){
                String href = e.attr("href");
                String link = "http://www.chemm.cn" + href;
                //Thread.sleep(3000);
                nextPage(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextPage(String url) {
        try {
//            http://www.chemm.cn/Company/CompanyLib-1-1.html
            String replace = url.replace(".html", "");
            int beginPag = 1;
            for (beginPag = 1; beginPag < 824; beginPag++) {
                String beginpag = replace+ "-" + beginPag+".html";
                System.out.println("下一页："+beginpag);
                company(beginpag);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


    }


    private void company(String url){

        try {
            String html = httpGet(url, "chemm");
            Document parse = Jsoup.parse(new URL(url).openStream(), "GBK",html);
            Elements elements = parse.select("div.ProListMain > ul > li > span > a");
            for (Element element :elements){
                String href = element.attr("href");
                //Thread.sleep(3000);
                info(href);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void info(String url){
        JSONObject companyInfo = new JSONObject();

        companyInfo.put("website",url);
        try {
            String get = httpGet(url, "chemm");
            Document gbk = Jsoup.parse(new URL(url).openStream(), "GBK", get);//解决解析后乱码问题
            Elements select = gbk.select("#ContactMethod > ul > li");
            for (Element s : select){
                if (s.text().contains("联系人")){
                    companyInfo.put("contact",s.text().split("：",2)[1]);
                }else if (s.text().contains("电 话")){
                    companyInfo.put("landline",s.text().split("：",2)[1]);
                }else if (s.text().contains("手 机")){
                    companyInfo.put("phone",s.text().split("：",2)[1]);
                }else if (s.text().contains("传 真")){
                    companyInfo.put("fax",s.text().split("：",2)[1]);
                }else if (s.text().contains("地 址")){
                    companyInfo.put("address",s.text().split("：",2)[1]);
                }
            }
            //Thread.sleep(10000);
            Elements elements = gbk.select("#MemberMenu > ul > li > a");
            for (Element e : elements){
                if ("公司简介".equals(e.text())){
                    String href = e.attr("href");
                    url = url +"/"+ href;
                    String html = httpGet(url, "chemm");
                    Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);//解决解析后乱码问题
                    companyInfo.put("company_info",parse.select("#CompanyInfo > div:nth-child(2) > ul > li").text());
                    companyInfo.put("management_model",parse.select("#MainProduct").text());
                }
                //Thread.sleep(5000);
                if ("联系方式".equals(e.text())){
                    String href = e.attr("href");
                    url = url +"/"+ href;
                    String html = httpGet(url, "chemm");
                    Document parse = Jsoup.parse(new URL(url).openStream(), "GBK", html);//解决解析后乱码问题
                    //companyInfo.put("postcode",parse.select("#ContactInfoDiv > table > tbody > tr:nth-child(6) > td:nth-child(2)").text());
                    //companyInfo.put("email",parse.select("#ContactInfoDiv > table > tbody > tr:nth-child(8) > td:nth-child(2)").text());
                    companyInfo.put("id", MD5Util.getMD5String(parse.select("#MemberTitle").text()));
//                    companyInfo.put("website_name","中国化工机械网");
                    companyInfo.put("name", parse.select("#MemberTitle").text());
                    companyInfo.put("main_product", parse.select("#MemberMainProduct").text().split("：", 2)[1].trim());
                }
            }
            companyInfo.put("crawlerId", "36");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            insert(companyInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        chemmCompany chemmCompany = new chemmCompany();
        chemmCompany.category("http://www.chemm.cn/Company/");
        //chemmCompany.category("http://www.chemm.cn/Sample");

        LOGGER.info("---完成了---");
    }


    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (updateToMySQL.dataUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    private void datasource(JSONObject datasourceInfo) {
        Map = (java.util.Map) datasourceInfo;
        if (updateToMySQL.datasourceUpdate(Map)) {
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
