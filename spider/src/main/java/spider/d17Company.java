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
import util.IConfigManager;
import util.IpProxyUtil;
import util.MD5Util;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Set;

/**
 * @author liyujie
 * http://company.d17.cc/
 */
public class d17Company {
    private final static Logger LOGGER = LoggerFactory.getLogger(d17Company.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";
    private static String tableName = "original_company_d17";
    private String nameMD5;
    private static String zookeeper;


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    /**
     * @param url
     */
    private void guangdong(String url) {
        try {
            String html = httpGet(url, "付款方式");
            Document document = Jsoup.parse(html);
            Elements address = document.select("#cmy_dq .cmy_content ul li a");
            for (Element e : address) {
//                if ("广东省".equals(e.attr("title"))) {
                String addressList = e.attr("href").split("1.htm", 2)[0] + "%d.htm";
                company(addressList);
//                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void company(String url) {
        try {
            for (int i = 1; i <= 50; i++) {
                String html = httpGet(String.format(url, i), "付款方式");
                Document document = Jsoup.parse(html);
                Elements companyList = document.select(".companylist_style ul .clr .name.clr a:first-child");
                for (Element e : companyList) {
                    String companyUrl = e.attr("href") + "/introduce.html";
                    detail(companyUrl);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    private void detail(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = httpGet(url, "关于我们");
            Document document = Jsoup.parse(html);
            companyInfo.put("company_info", document.select(".item").text().trim());
            Elements infoList = document.select(".tps_con_mode.pagein_information .information.clr .clr li");
            for (Element e : infoList) {
                String key = e.text().split("：", 2)[0];
                switch (key) {
                    case "公司名称":
                        companyInfo.put("name", e.text().split("：", 2)[1]);
                        nameMD5 = MD5Util.getMD5String(e.text().split("：", 2)[1]);
                        companyInfo.put("id", nameMD5);
                        break;
                    case "联系人":
                        companyInfo.put("contact", e.text().split("：", 2)[1]);
                        break;
                    case "联系手机":
                        companyInfo.put("phone", e.text().split("：", 2)[1]);
                        break;
                    case "QQ":
                        companyInfo.put("qq", e.text().split("：", 2)[1]);
                        break;
                    case "邮箱":
                        companyInfo.put("email", e.text().split("：", 2)[1]);
                        break;
                    case "固定电话":
                        companyInfo.put("landline", e.text().split("：", 2)[1]);
                        break;
                    case "公司传真":
                        companyInfo.put("fax", e.text().split("：", 2)[1]);
                        break;
                    case "公司地址":
                        companyInfo.put("address", e.text().split("：", 2)[1]);
                        break;
                    case "邮政编码":
                        companyInfo.put("postcode", e.text().split("：", 2)[1]);
                        break;
                    case "公司网址":
                        companyInfo.put("website", e.text().split("：", 2)[1]);
                        break;
                    default:
                }
            }
            insert(companyInfo, tableName, nameMD5);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject companyInfo, String tableName, String MD5) {
        try {
            Map = (java.util.Map) companyInfo;
            if (updateToMySQL.exist(Map, tableName, MD5)) {
                if (updateToMySQL.d17Update(Map, MD5)) {
                    LOGGER.info("更新中 : " + Map.toString());
                    write(MD5, "F:\\DataCatch\\d17\\update.txt");
                }
            } else {
                if (updateToMySQL.d17Insert(Map)) {
                    LOGGER.info("插入中 : " + Map.toString());
                    write(MD5, "F:\\DataCatch\\d17\\insert.txt");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        d17Company d17Company = new d17Company();
        d17Company.guangdong("http://company.d17.cc/");
        LOGGER.info("------广东省完成了------");
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

    private void write(String file, String savePath) throws Exception {
        try {
            FileWriter out = new FileWriter(savePath, true);
            out.write(String.valueOf(file));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
