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
                        if (element.text().contains("??? ??? ??????")) {
                            companyInfo.put("contact", element.text().replace("??? ??? ??????", ""));
                        } else if (element.text().contains("???????????????")) {
                            companyInfo.put("main_product", element.text().replace("???????????????", ""));
                        } else if (element.text().contains("???????????????")) {
                            companyInfo.put("website", element.text().replace("???????????????", ""));
                        } else if (element.text().contains("??? ??????")) {
                            companyInfo.put("postcode", element.text().replace("??? ??????", ""));
                        } else if (element.text().contains("??? ??????")) {
                            companyInfo.put("address", element.text().replace("??? ??????", ""));
                        }
                    }
                    Elements select = doc.select("div.company-info > table > tbody > tr");
                    for (Element element : select) {
                        if (element.text().contains("????????????")) {
                            companyInfo.put("name", element.text().replace("????????????", ""));
                            String s = MD5Util.getMD5String(element.text().replace("????????????", ""));
                            list.add(s);
                            companyInfo.put("id", s);
                        } else if (element.text().contains("????????????")) {
                            companyInfo.put("industry", element.text().replace("????????????", ""));
                        } else if (element.text().contains("????????????")) {
                            companyInfo.put("register_capital", element.text().replace("????????????", ""));
                        } else if (element.text().contains("????????????")) {
                            companyInfo.put("register_capital", element.text().replace("????????????", ""));
                        } else if (element.text().contains("????????????")) {
                            companyInfo.put("main_product", element.text().replace("????????????", ""));
                        } else if (element.text().contains("???????????????")) {
                            companyInfo.put("from_where_table_id", element.text().replace("???????????????", ""));
                        } else if (element.text().contains("????????????")) {
                            companyInfo.put("company_model", element.text().replace("????????????", ""));
                        } else if (element.text().contains("????????????")) {
                            companyInfo.put("company_register_time", element.text().replace("????????????", ""));
                        } else if (element.text().contains("????????????")) {
                            companyInfo.put("main_product", element.text().replace("????????????", ""));
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
                            if (element.text().contains("??????????????? ")) {
                                companyInfo.put("name", element.text().replace("??????????????? ", ""));
                                String nameId1 = MD5Util.getMD5String(element.text().replace("??????????????? ", ""));
                                list.add(nameId1);
                                companyInfo.put("id", nameId1);

                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("company_model", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("management_model", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("register_capital", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("incorporator", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("company_register_time", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("industry", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("employees", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("website", element.text().replace("??????????????? ", ""));
                            } else if (element.text().contains("??????????????? ")) {
                                companyInfo.put("main_product", element.text().replace("??????????????? ", ""));
                            }
                        }
                        String about = HttpUtil.httpGetwithJudgeWord(s1, "51sole");
                        Document doc = Jsoup.parse(about);
                        if (null != doc) {
                            Elements select1 = doc.select("#contact > ul > li,#contact > ul > li");
                            for (Element element : select1) {
                                if (element.text().contains("??????????????????")) {
                                    companyInfo.put("address", element.text().replace("??????????????????", ""));
                                } else if (element.text().contains("??????????????????")) {
                                    companyInfo.put("postcode", element.text().replace("??????????????????", ""));
                                } else if (element.text().contains("??????????????????")) {
                                    companyInfo.put("landline", element.text().replace("??????????????????", ""));
                                } else if (element.text().contains("??????????????????")) {
                                    companyInfo.put("fax", element.text().replace("??????????????????", ""));
                                } else if (element.text().contains("??????????????????")) {
                                    companyInfo.put("phone", element.text().replace("??????????????????", ""));
                                } else if (element.text().contains("???  ???  ?????????")) {
                                    companyInfo.put("contact", element.text().replace("???  ???  ?????????", ""));
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
