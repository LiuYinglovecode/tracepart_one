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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class yellowurlCompany {
    private final static Logger LOGGER = LoggerFactory.getLogger(yellowurlCompany.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void category(String url) {
        try {
//            String html = httpGet(url, "中国黄页网");
            String html = httpGetWithProxy(url, "中国黄页网");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("#category > div > ul.typical > li > a");
            for (Element e : select) {
                String href = e.attr("href");
                Thread.sleep(7000);
                company(href);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void company(String url) {
        try {
            String html = httpGet(url, "中国黄页网");
            Document document = Jsoup.parse(html);
            Elements select = document.select("div.searchTermsDetail > h2 > a");
            for (Element link : select) {
                String href = link.attr("href");
                Thread.sleep(7000);
                info(href);
            }
            //下一页
            Elements paging = document.select("div.pagination.middle > div > a");
            for (Element element : paging) {
                if (" 下一页» ".contains(element.text())) {
                    String links = element.attr("href");
                    System.out.println(" 下一页» " + links);
                    Thread.sleep(7000);
                    company(links);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void info(String url) {
        JSONObject companyInfo = new JSONObject();
        //companyInfo.put("website_name", "中国黄页网");
        try {
            String s = url + "/contact/";
            String html = httpGet(s, "中国黄页网");
            Document document = Jsoup.parse(html);
            Elements tr = document.select("tbody > tr");
            for (Element e : tr) {
                Elements tr1 = e.select("td:nth-child(1)");
                Elements tr2 = e.select("td:nth-child(2)");
                for (int i = 0; i < tr2.size(); i++) {
                    if ("公司名称：".equals(tr1.eq(i).text())) {
                        companyInfo.put("name", tr2.eq(i).text());
                        companyInfo.put("id", MD5Util.getMD5String(tr2.eq(i).text()));
                    } else if ("公司地址：".equals(tr1.eq(i).text())) {
                        companyInfo.put("address", tr2.eq(i).text());
                    } else if ("公司电话：".equals(tr1.eq(i).text())) {
                        companyInfo.put("landline", tr2.eq(i).text());
                    } else if ("公司网址：".equals(tr1.eq(i).text())) {
                        companyInfo.put("website", tr2.eq(i).text());
                    } else if ("联 系 人：".equals(tr1.eq(i).text())) {
                        companyInfo.put("contact", tr2.eq(i).text());
                    } else if ("手机号码：".equals(tr1.eq(i).text())) {
                        companyInfo.put("phone", tr2.eq(i).text());
                    }
                }
            }
            companyInfo.put("crawlerId", "8");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            insert(companyInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
//        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.141:2181");
        yellowurlCompany yellowurlCompany = new yellowurlCompany();
        yellowurlCompany.category("http://company.yellowurl.cn/");
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
