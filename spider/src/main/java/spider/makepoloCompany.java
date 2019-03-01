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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author liyujie
 * http://corp.makepolo.com/
 */
public class makepoloCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(makepoloCompany.class);
    private static Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static Map<String, String> header = null;
    private static String savePage = "F:\\DataCatch\\makepolo\\page.txt";

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    private void category(String url) {
        try {
            String html = httpGet(url, "马可波罗-精准采购搜索引擎");
            Document document = Jsoup.parse(html);
            Elements categoryList = document.select(".com_productn .com_productn_type");
            Elements categoryList2 = document.select(".com_productn .side_popup a");
//            for (Element e : categoryList) {
//                String companyList = e.attr("href");
//                if (!"#".equals(companyList)) {
//                    write(companyList);
//                    companyList(companyList);
//                }
//            }
            for (Element e : categoryList2) {
                String companyList = e.attr("href");
                write(companyList);
                companyList(companyList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void companyList(String url) {
        try {
            String html = httpGet(url, "马可波罗-精准采购搜索引擎");
            Document document = Jsoup.parse(html);
            Elements companyUrlList = document.select(".h_com.clearfix .corp_info .h_com_info h3 a");
            for (Element e : companyUrlList) {
                String companyUrl = e.attr("href") + "/contact_us.html";
                detail(companyUrl);
            }
            if (0 != document.select(".h_nextpage_r a:last-child").size()) {
                companyList(document.select(".h_nextpage_r a:last-child").attr("href"));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = httpGet(url, "马可波罗网郑重提醒");
            Document document = Jsoup.parse(html);
            Elements infoList = document.select(".y_contact_list ul li");
            for (Element e : infoList) {
                if (e.text().contains("联系人")) {
                    companyInfo.put("contact", e.text().split("：", 2)[1].trim());
                } else if (e.text().contains("公司网址")) {
                    companyInfo.put("website", e.text().split("：", 2)[1].trim());
                } else if (e.text().contains("联系电话")) {
                    companyInfo.put("phone", e.text().split("：", 2)[1].split("无", 2)[0].trim());
                } else if (e.text().contains("传真")) {
                    companyInfo.put("fax", e.text().split("：", 2)[1].trim());
                } else if (e.text().contains("公司地址")) {
                    companyInfo.put("address", e.text().split("：", 2)[1].trim());
                } else if (e.text().contains("Email")) {
                    companyInfo.put("email", e.text().split("：", 2)[1].trim());
                }
            }
            String companyName = document.select(".company_names").text().trim();
            String id = MD5Util.getMD5String(companyName);
            companyInfo.put("name", companyName);
            companyInfo.put("id", id);
            insertToMySQL(companyInfo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insertToMySQL(JSONObject companyInfo) {
        Map = (Map) companyInfo;
        if (TxtUpdateToMySQL.makepoloUpdate(Map)) {
            LOGGER.info("插入中 : " + Map.toString());
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.138:2181");
        makepoloCompany makepoloCompany = new makepoloCompany();
        makepoloCompany.category("http://corp.makepolo.com/");
        LOGGER.info("---完成了---");
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
