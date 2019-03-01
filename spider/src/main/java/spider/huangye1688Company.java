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

import static ipregion.IpRegionTaobao.getProxy;

public class huangye1688Company {
    private final static Logger LOGGER = (Logger) LoggerFactory.getLogger(huangye1688Company.class);
    private static java.util.Map<String, String> Map = null;
    private IpProxyUtil ipProxyList = new IpProxyUtil();
    private static java.util.Map<String, String> header = null;
    private static String savePage = "";


    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
    }

    /**
     * @param url
     */
    //黄页1688首页导航栏
    private void category(String url) {
        try {
            String html = httpGetWithProxy(url, "1688");
            Document document = Jsoup.parse(html);
            Elements address = document.select("dd.detail > div.normal-list > ul > li > dl > dd > ul > li > a[title]");
            for (Element element : address) {
                if (element != null) {
                    String href = "https:"+(element.attr("href"));
                    //Thread.sleep(10000);
                    companyList(href);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //1688子页导航栏
    private void companyList(String url) {
        try {
                String links = httpGetWithProxy(url, "1688");
                Document document = Jsoup.parse(links);
                Elements address = document.select("div.list-item-left > div > div> a.list-item-title-text");
                for (Element element : address) {
                        String href = element.attr("href");
                        Record(href);
                        //Thread.sleep(10000);
                }
                /**
                 *  下一页
                 */
                Elements pages = document.select("#sw_mod_pagination_content > div > a.page-next");
                String href = pages.attr("href");
                companyList(href);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private  void  Record(String url){
        try {
            String html = httpGetWithProxy(url ,"1688");
            Document parse = Jsoup.parse(html);
            Element first = parse.select("#topnav > div > ul > li.selected.creditdetail-page > a").first();
            if (first != null) {
                String href = first.attr("href");
                Details(href);
                //Thread.sleep(10000);
            }else {
                Element first1 = parse.select("#topnav > div > ul > li.creditdetail-page > a").first();
                String href1 = first1.attr("href");
                Details(href1);
                //Thread.sleep(10000);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //抓取相关数据
    private void Details(String url) {
        JSONObject companyInfo = new JSONObject();
        try {
            String html = httpGetWithProxy(url, "1688");
            Document parse = Jsoup.parse(html);
           //黄页
            String href = parse.select("#J_COMMON_GoToYellowPage").attr("href");
            String linkmanHtml = httpGetWithProxy(href, "1688");
            Document linkman = Jsoup.parse(linkmanHtml);
            companyInfo.put("name", linkman.select("div.m-content > div.head > div > h1").text());
            companyInfo.put("id", MD5Util.getMD5String(linkman.select("div.m-content > div.head > div > h1").text().trim()));
            companyInfo.put("company_info", linkman.select("div.m-content > div.content > span").text());
            //基本信息与详细信息
            Elements trs = linkman.select("div.m-content > div > ul > li > div > div > div > table > tbody > tr");
            for (Element key : trs) {
                Elements tdk = key.select("td.title > p");
                Elements tdv = key.select("td.info > div > p > a");
                Elements tdvvv = key.select("td.info > p > a");
                Elements tdvv = key.select("td.info > p");
                for (int i = 0; i < tdk.size(); i++) {
                    if ("主营产品或服务:".equals(tdk.eq(i).text())) {
                        companyInfo.put("main_product", tdv.eq(i).text());
                    } else if ("主营行业:".equals(tdk.eq(i).text())) {
                        companyInfo.put("industry", tdvvv.eq(i).text());
                    } else if ("经营模式:".equals(tdk.eq(i).text())) {
                        companyInfo.put("management_model", tdvv.eq(i).text());
                    } else if ("是否提供加工/定制服务:".equals(tdk.eq(i).text())) {
                        companyInfo.put("customized_service", tdvv.eq(i).text());
                    } else if ("注册资本:".equals(tdk.eq(i).text())) {
                        companyInfo.put("register_capital", tdvv.eq(i).text());
                    } else if ("公司成立时间:".equals(tdk.eq(i).text())) {
                        companyInfo.put("company_register_time", tdvv.eq(i).text());
                    } else if ("公司注册地:".equals(tdk.eq(i).text())) {
                        companyInfo.put("register_address", tdvv.eq(i).text());
                    } else if ("企业类型:".equals(tdk.eq(i).text())) {
                        companyInfo.put("company_model", tdvv.eq(i).text());
                    } else if ("法定代表人:".equals(tdk.eq(i).text())) {
                        companyInfo.put("incorporator", tdvv.eq(i).text());
                    } else if ("工商注册号/统一社会信用代码:".equals(tdk.eq(i).text())) {
                        companyInfo.put("from_where_table_id", tdvv.eq(i).text());
                    } else if ("加工方式:".equals(tdk.eq(i).text())) {
                        companyInfo.put("processing_method", tdvv.eq(i).text());
                    } else if ("工艺:".equals(tdk.eq(i).text())) {
                        companyInfo.put("technics", tdvv.eq(i).text());
                    } else if ("管理体系认证:".equals(tdk.eq(i).text())) {
                        companyInfo.put("quality_control", tdvv.eq(i).text());
                    } else if ("产品质量认证:".equals(tdk.eq(i).text())) {
                        companyInfo.put("product_quality", tdvv.eq(i).text());
                    } else if ("员工人数:".equals(tdk.eq(i).text())) {
                        companyInfo.put("employees", tdvv.eq(i).text());
                    } else if ("研发部门人数:".equals(tdk.eq(i).text())) {
                        companyInfo.put("research_staff", tdv.eq(i).text());
                    } else if ("厂房面积:".equals(tdk.eq(i).text())) {
                        companyInfo.put("company_area", tdvv.eq(i).text());
                    } else if ("主要销售区域:".equals(tdk.eq(i).text())) {
                        companyInfo.put("sell_area", tdvv.eq(i).text());
                    } else if ("主要客户群体:".equals(tdk.eq(i).text())) {
                        companyInfo.put("company_clients", tdvv.eq(i).text());
                    } else if ("月产量:".equals(tdk.eq(i).text())) {
                        companyInfo.put("monthly_production", tdvv.eq(i).text());
                    } else if ("年营业额:".equals(tdk.eq(i).text())) {
                        companyInfo.put("company_turnover", tdvv.eq(i).text());
                    } else if ("年出口额:".equals(tdk.eq(i).text())) {
                        companyInfo.put("export_fore", tdvv.eq(i).text());
                    } else if ("品牌名称:".equals(tdk.eq(i).text())) {
                        companyInfo.put("company_brand", tdvv.eq(i).text());
                    } else if ("质量控制:".equals(tdk.eq(i).text())) {
                        companyInfo.put("quality_controls", tdvv.eq(i).text());
                    } else if ("开户银行:".equals(tdk.eq(i).text())) {
                        companyInfo.put("open_bank", tdvv.eq(i).text());
                    } else if ("账号:".equals(tdk.eq(i).text())) {
                        companyInfo.put("open_account", tdvv.eq(i).text());
                    }else if ("公司主页:".equals(tdk.eq(i).text())) {
                        companyInfo.put("website", tdvv.eq(i).text());
                    }
                }
            }
            //公司联系信息
            String companyContact = href + "&tab=companyWeb_contact";
            String linkmanHtmls = httpGetWithProxy(companyContact, "1688");
            Document linkmans = Jsoup.parse(linkmanHtmls);
            Elements Routes = linkmans.select("div.m-content > div > ul > li > div > div > table> tbody > tr");
            for (Element keys : Routes) {
                Elements tdkey = keys.select("td.title > p");
                Elements tdvalue = keys.select("td.info > div");
                Elements tdvalues = keys.select("td.info > p");
                for (int i = 0; i < tdkey.size(); i++) {
                     if ("联系人 :".equals(tdkey.eq(i).text())) {
                         companyInfo.put("contacts", tdvalue.eq(i).text());
                     } else if ("电话 :".equals(tdkey.eq(i).text())) {
                         companyInfo.put("landline", tdvalues.eq(i).text());
                     } else if ("移动电话 :".equals(tdkey.eq(i).text())) {
                         companyInfo.put("telephone", tdvalues.eq(i).text());
                     } else if ("传真 :".equals(tdkey.eq(i).text())) {
                         companyInfo.put("fax", tdvalues.eq(i).text());
                     } else if ("地址 :".equals(tdkey.eq(i).text())) {
                         companyInfo.put("address", tdvalues.eq(i).text());
                     } else if ("邮编 :".equals(tdkey.eq(i).text())) {
                         companyInfo.put("postcode", tdvalues.eq(i).text());
                     }
                }
            }
            insert(companyInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.138:2181");
        huangye1688Company huangye1688Company = new huangye1688Company();
        huangye1688Company.category("https://company.1688.com/");
        LOGGER.info("---完成了---");
    }


    private void insert(JSONObject companyInfo) {
        Map = (java.util.Map) companyInfo;
        if (TxtUpdateToMySQL.ali1688Update(Map)) {
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
}
