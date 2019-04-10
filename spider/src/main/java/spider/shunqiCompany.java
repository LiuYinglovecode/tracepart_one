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
import java.util.Set;

/**
 * @author liyujie
 * http://www.11467.com/
 */
public class shunqiCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(shunqiCompany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "F:\\DataCatch\\shunqi\\page.txt";

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    private void category(String url) {
        try {
            String html = httpGetWithProxy(url, "顺企网");
//            String html = httpGet(url, "顺企网");
            Document document = Jsoup.parse(html);
            Elements categoryList = document.select(".box.sidesubcat.t5");
            for (Element e : categoryList) {
                if ("按城市浏览全国公司黄页".equals(e.select(".boxtitle").text())) {
                    for (int i = 1; i < e.select(".boxcontent .listtxt dd a").size(); i++) {
                        write("城市 : " + String.valueOf(i), savePage);
                        String categoryUrl = "http:" + e.select(".boxcontent .listtxt dd a").eq(i).attr("href");
                        address(categoryUrl);
                    }
                    for (Element r : e.select(".boxcontent .listtxt dd a")) {
                        String categoryUrl = "http:" + r.attr("href");
                        address(categoryUrl);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void address(String url) {
        try {
            String html = httpGetWithProxy(url, "顺企网");
//            String html = httpGet(url, "顺企网");
            Document document = Jsoup.parse(html);
            Elements address = document.select(".box.sidesubcat.t5 .boxcontent .listtxt");
            for (Element e : address) {
                if (e.select("dt").text().contains("地区浏览")) {
                    for (Element r : address.select("dd a")) {
                        String addressUrl = r.attr("href");
                        company(addressUrl);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void company(String url) {
        try {
            String html = httpGetWithProxy(url, "顺企网");
//            String html = httpGet(url, "顺企网");
            Document document = Jsoup.parse(html);
            Elements companyList = document.select(".companylist li .f_l h4 a");
            for (Element e : companyList) {
                String addressUrl = "http:" + e.attr("href");
                detail(addressUrl);
            }
            Elements pages = document.select(".pages a");
            for (Element e : pages) {
                if ("下一页".equals(e.text())) {
                    company(e.attr("href"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = httpGetWithProxy(url, "顺企网");
//            String html = httpGet(url, "顺企网");
            Document document = Jsoup.parse(html);
            companyInfo.put("name", document.select("#logoco h1 span").text().trim());
            String id = MD5Util.getMD5String(document.select("#logoco h1 span").text().trim());
            companyInfo.put("id", id);
            companyInfo.put("detail", document.select("#aboutuscontent").text().trim());
            Elements contactDt = document.select("#contact .boxcontent .codl dt");
            Elements contactDd = document.select("#contact .boxcontent .codl dd");
            for (int i = 0; i < contactDd.size(); i++) {
                if ("公司地址：".equals(contactDt.eq(i).text())) {
                    companyInfo.put("address", contactDd.eq(i).text());
                } else if ("固定电话：".equals(contactDt.eq(i).text())) {
                    companyInfo.put("landline", contactDd.eq(i).text());
                } else if ("经理：".equals(contactDt.eq(i).text())) {
                    companyInfo.put("contact", contactDd.eq(i).text());
                } else if ("经理手机：".equals(contactDt.eq(i).text())) {
                    companyInfo.put("mobil_phone", contactDd.eq(i).text());
                } else if ("电子邮件：".equals(contactDt.eq(i).text())) {
                    companyInfo.put("emil", contactDd.eq(i).text());
                } else if ("邮政编码：".equals(contactDt.eq(i).text())) {
                    companyInfo.put("zip_code", contactDd.eq(i).text());
                } else if ("传真号码：".equals(contactDt.eq(i).text())) {
                    companyInfo.put("fax", contactDd.eq(i).text());
                }
            }
            Elements gongshang = document.select("#gongshang .boxcontent .codl tr");
            for (Element e : gongshang) {
                String key = e.select(".tdl").text();
                switch (key) {
                    case "主要经营产品：":
                        companyInfo.put("main_products", e.text().split("：", 2)[1]);
                        break;
                    case "所属分类：":
                        companyInfo.put("type", e.text().split("：", 2)[1]);
                        break;
                    case "所属城市：":
                        companyInfo.put("city", e.text().split("：", 2)[1]);
                        break;
                    default:
                }
            }
            insert(companyInfo, "original_company_shunqi", id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject companyInfo, String tableName, String MD5) {
        try {
            Map = (java.util.Map) companyInfo;
            if (TxtUpdateToMySQL.exist(Map, tableName, MD5)) {
                if (TxtUpdateToMySQL.shunqiUpdate(Map, MD5)) {
                    LOGGER.info("更新中 : " + Map.toString());
                    write(MD5, "F:\\DataCatch\\shunqi\\update.txt");
                }
            } else {
                if (TxtUpdateToMySQL.shunqiInsert(Map)) {
                    LOGGER.info("插入中 : " + Map.toString());
                    write(MD5, "F:\\DataCatch\\shunqi\\insert.txt");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        shunqiCompany shunqiCompany = new shunqiCompany();
        shunqiCompany.category("http://b2b.11467.com/");
//        shunqiCompany.address("http://www.11467.com/shenzhen/");
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
