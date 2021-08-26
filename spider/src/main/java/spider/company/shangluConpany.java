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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * <a>http://big5.b2b6.com</a>
 * <a>Company：商录网</a>
 * @author:chenyan
 */
public class shangluConpany {
    private final static Logger LOGGER = LoggerFactory.getLogger(shangluConpany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String baseUrl = "http://big5.b2b6.com";
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
        shangluConpany ceshi = new shangluConpany();
        ceshi.productNavigationBar("http://big5.b2b6.com/yp/");
        LOGGER.info("---完成了---");
    }

    //首页
    private void productNavigationBar(String url) {
        try {
            String httpGet = httpGet(url, "商录");
            Document parse = Jsoup.parse(httpGet);
            Elements select = parse.select("#dCatalogueBox > ul > li > a");
            for (Element element : select) {
                String href = baseUrl+element.attr("href");
                nextPage(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //分页
    private void nextPage(String url) {
        String replace = url.replace("1.aspx", "");
        int page = 1;
        for (page = 1; page < 201; page++) {
            String pageLink = replace + page + ".aspx";
            System.out.println("下一页："+pageLink);
            companyList(pageLink);
        }
    }

    //列表
    private void companyList(String url) {
        try {
            String httpGet = httpGet(url, "b2b6");
            Document parse = Jsoup.parse(httpGet);
            Elements select = parse.select("#dMainBox > a");
            for (Element element : select) {
                String href = baseUrl+element.attr("href");
                companyInfo(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //信息
    private void companyInfo(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String httpGet = httpGet(url, "b2b6");
            Document parse = Jsoup.parse(httpGet);
            String html = parse.select("#dMainBox").html();
            String[] brs = html.split("br");
            for (String br : brs) {
                if (br.contains("公司名稱：")) {
                    String name = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","");
                    companyInfo.put("name",name);
                    companyInfo.put("id", MD5Util.getMD5String(name));
                }else if (br.contains("聯系電話：")){
                    String landline = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","").replace("（溫馨提示：請核實資質，謹防詐騙）","");
                    companyInfo.put("landline",landline);
                }else if (br.contains("註冊日期：")){
                    String company_register_time = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","");
                    companyInfo.put("company_register_time",company_register_time);
                }else if (br.contains("註冊資金：")){
                    String register_capital = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","");
                    companyInfo.put("register_capital",register_capital);
                }else if (br.contains("職工人數：")){
                    String employees = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","");
                    companyInfo.put("employees",employees);
                }else if (br.contains("郵政編碼：")){
                    String postcode = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","");
                    companyInfo.put("postcode",postcode);
                }else if (br.contains("公司地址：")){
                    String address = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","");
                    companyInfo.put("address",address);
                }else if (br.contains("經營範圍：")){
                    String industry = br.split("：", 2)[1].replace("</b> ", "").replace("<", "").replace("\n","");
                    companyInfo.put("industry",industry);
                }
            }
            companyInfo.put("website",parse.select("#dMainBox > a[target=_blank]").attr("href").replace("\n",""));
            Elements select = parse.select(".cssDesc");
            if (select!=null){
                select.select(".none").remove();
                String company_info = select.text();
                companyInfo.put("company_info",company_info.replace("\n",""));
            }

            companyInfo.put("crawlerId", "45");
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
}
