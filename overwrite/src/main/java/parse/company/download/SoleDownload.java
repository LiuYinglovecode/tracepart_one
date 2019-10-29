package parse.company.download;

import Utils.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ESUtil;
import Utils.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SoleDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoleDownload.class);
    private static SimpleDateFormat creatrTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat timestamp = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss ZZZ", Locale.US);
    private static SimpleDateFormat timestamp2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private static ESUtil esUtil = new ESUtil();

    public void info(String attr) {
        try {
            JSONObject companyInfo = new JSONObject();
            ArrayList<String> list = new ArrayList<>();
            companyInfo.put("crawlerId", "101");
            companyInfo.put("createTime", creatrTime.format(new Date()));
            companyInfo.put("timestamp", timestamp.format(new Date()));
            timestamp2.setTimeZone(TimeZone.getTimeZone("UTC"));
            companyInfo.put("@timestamp", timestamp2.format(new Date()));
            companyInfo.put("time_stamp", String.valueOf(System.currentTimeMillis()));

            Thread.sleep(5000);
            if (attr.contains("detail")) {
                String info = HttpUtil.httpGetwithJudgeWord(attr, "51sole");
                if (null!=info) {
                    Document doc = Jsoup.parse(info);
                    companyInfo.put("company_info", doc.select("div.article").text().trim());
                    Elements select1 = doc.select("div.profile-main > div > ul > li");
                    for (Element element : select1) {
                        if (element.text().contains("联 系 人：")) {
                            companyInfo.put("contact", element.text().replace("联 系 人：", ""));
                        } else if (element.text().contains("主营产品：")) {
                            companyInfo.put("main_product", element.text().replace("主营产品：", ""));
                        } else if (element.text().contains("商铺网址：")) {
                            companyInfo.put("website", element.text().replace("商铺网址：", ""));
                        } else if (element.text().contains("邮 编：")) {
                            companyInfo.put("postcode", element.text().replace("邮 编：", ""));
                        } else if (element.text().contains("地 址：")) {
                            companyInfo.put("address", element.text().replace("地 址：", ""));
                        }
                    }
                    Elements select = doc.select("div.company-info > table > tbody > tr");
                    for (Element element : select) {
                        if (element.text().contains("公司名称")) {
                            companyInfo.put("name", element.text().replace("公司名称", ""));
                            String s = MD5Util.getMD5String(element.text().replace("公司名称", ""));
                            list.add(s);
                            companyInfo.put("id", s);
                        } else if (element.text().contains("主营行业")) {
                            companyInfo.put("industry", element.text().replace("主营行业", ""));
                        } else if (element.text().contains("注册资本")) {
                            companyInfo.put("register_capital", element.text().replace("注册资本", ""));
                        } else if (element.text().contains("注册资本")) {
                            companyInfo.put("register_capital", element.text().replace("注册资本", ""));
                        } else if (element.text().contains("主营产品")) {
                            companyInfo.put("main_product", element.text().replace("主营产品", ""));
                        } else if (element.text().contains("工商注册号")) {
                            companyInfo.put("from_where_table_id", element.text().replace("工商注册号", ""));
                        } else if (element.text().contains("公司类型")) {
                            companyInfo.put("company_model", element.text().replace("公司类型", ""));
                        } else if (element.text().contains("成立日期")) {
                            companyInfo.put("company_register_time", element.text().replace("成立日期", ""));
                        } else if (element.text().contains("经营范围")) {
                            companyInfo.put("main_product", element.text().replace("经营范围", ""));
                        }
                    }
                    for (String s : list) {
                        mysqlUtil.insertCompany(companyInfo);
                        if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", s)) {
                            RedisUtil.insertUrlToSet("catchedUrl-Company", attr);
                        }
                    }
                }
            }else {
                String s = attr + "/companyabout.htm";
                String s1 = attr + "/companycontact.htm";
                String html = HttpUtil.httpGetwithJudgeWord(s, "51sole");
                Thread.sleep(5000);
                if (null != html) {
                    Document document = Jsoup.parse(html);
                    companyInfo.put("company_info", document.select("#aboutus > div > p").text().trim());
                    Elements select = document.select("#companyinfo > ul > li");
                    if (0 != select.size()) {
                        for (Element element : select) {
                            if (element.text().contains("公司名称： ")) {
                                companyInfo.put("name", element.text().replace("公司名称： ", ""));
                                String nameId1 = MD5Util.getMD5String(element.text().replace("公司名称： ", ""));
                                list.add(nameId1);
                                companyInfo.put("id", nameId1);

                            } else if (element.text().contains("企业类型： ")) {
                                companyInfo.put("company_model", element.text().replace("企业类型： ", ""));
                            } else if (element.text().contains("经营模式： ")) {
                                companyInfo.put("management_model", element.text().replace("经营模式： ", ""));
                            } else if (element.text().contains("注册资本： ")) {
                                companyInfo.put("register_capital", element.text().replace("注册资本： ", ""));
                            } else if (element.text().contains("法人代表： ")) {
                                companyInfo.put("incorporator", element.text().replace("法人代表： ", ""));
                            } else if (element.text().contains("成立时间： ")) {
                                companyInfo.put("company_register_time", element.text().replace("成立时间： ", ""));
                            } else if (element.text().contains("主营行业： ")) {
                                companyInfo.put("industry", element.text().replace("主营行业： ", ""));
                            } else if (element.text().contains("员工人数： ")) {
                                companyInfo.put("employees", element.text().replace("员工人数： ", ""));
                            } else if (element.text().contains("公司网站： ")) {
                                companyInfo.put("website", element.text().replace("公司网站： ", ""));
                            } else if (element.text().contains("主营产品： ")) {
                                companyInfo.put("main_product", element.text().replace("主营产品： ", ""));
                            }
                        }
                        String about = HttpUtil.httpGetwithJudgeWord(s1, "51sole");
                        Document doc = Jsoup.parse(about);
                        if (null != doc) {
                            Elements select1 = doc.select("#contact > ul > li,#contact > ul > li");
                            for (Element element : select1) {
                                if (element.text().contains("地　　址：　")) {
                                    companyInfo.put("address", element.text().replace("地　　址：　", ""));
                                } else if (element.text().contains("邮　　编：　")) {
                                    companyInfo.put("postcode", element.text().replace("邮　　编：　", ""));
                                } else if (element.text().contains("电　　话：　")) {
                                    companyInfo.put("landline", element.text().replace("电　　话：　", ""));
                                } else if (element.text().contains("传　　真：　")) {
                                    companyInfo.put("fax", element.text().replace("传　　真：　", ""));
                                } else if (element.text().contains("手　　机：　")) {
                                    companyInfo.put("phone", element.text().replace("手　　机：　", ""));
                                } else if (element.text().contains("联  系  人：　")) {
                                    companyInfo.put("contact", element.text().replace("联  系  人：　", ""));
                                }
                            }
                        }
                    }
//                    for (String id : list) {
//                        mysqlUtil.insertCompany(companyInfo);
//                        if (esUtil.writeToES(companyInfo, "crawler-company-", "doc", id)) {
//                            RedisUtil.insertUrlToSet("catchedUrl-Company", attr);
//                        }
//                    }
                    if (mysqlUtil.insertCompany(companyInfo)){
                        RedisUtil.insertUrlToSet("catchedUrl-Company", attr);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
