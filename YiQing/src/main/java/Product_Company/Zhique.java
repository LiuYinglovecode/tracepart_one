package Product_Company;

import Utils.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


public class Zhique {
    private final static Logger LOGGER = LoggerFactory.getLogger(Zhique.class);
    private static String KOUZHAO = "https://www.zhiqueip.com/result?keyword=%E5%8F%A3%E7%BD%A9";
    private static String BASEURL = "https://www.zhiqueip.com/";

    public static void main(String[] args) {
        Zhique zhique = new Zhique();
        zhique.patentList(KOUZHAO);
    }

    public void patentList(String url) {
        try {
            Document document2 = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
            String html = HttpUtil.httpGet(url, null);
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements patentUrlList = document.select(".deal-ul.content .deal-item .deal-item-right");
                for (Element e : patentUrlList) {
                    detail(BASEURL + e.select("> a").attr("href"), e.select("> a").text().trim(), e.select(".deal-item-lpr").text().trim());
                }
                //翻页
//                Element nextpage = document.select(".main-page a").last();
//                if ("下一页".equals(nextpage.text().trim())) {
//                    patentList(baseUrl + nextpage.attr("href"));
//                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void detail(String detailUrl, String patentName, String state) {
        JSONObject patentInfo = new JSONObject();
        JSONArray lawInfo = new JSONArray();
        JSONArray imgInfo = new JSONArray();
        JSONArray img2Info = new JSONArray();
        try {
            String html = HttpUtil.httpGetwithJudgeWord(detailUrl, "知雀");
            if (null != html) {
                Document document = Jsoup.parse(html);
                //source_id	数据来源，值为“kg_covid_datasource”表的 id
                patentInfo.put("source_id", 33);
                //category_id	产品类别，值为 kg_covid_product_category 表中的 id
                patentInfo.put("category_id", 1);
                //patent_id	数据在原平台的唯一标识，若没有，如爬虫数据，可为空
                patentInfo.put("patent_id", detailUrl);
                //name	专利名称
                patentInfo.put("name", patentName);
                //url	专利链接
                patentInfo.put("url", detailUrl);
                //summary	摘要
                patentInfo.put("summary", document.select(".item-detail-text").text().trim());
                Elements dealitemeach = document.select(".deal-item-each");
                for (Element e : dealitemeach) {
                    if (e.select(".deal-item-k").text().contains("案件状态")) {
                        //case_status	案件状态
                        patentInfo.put("case_status", e.select(".deal-item-v").text().trim());
                    } else if (e.select(".deal-item-k").text().contains("技术领域")) {
                        //technical_field	技术领域
                        patentInfo.put("technical_field", e.select(".deal-item-v").text().trim());
                    }
                }
                //trading_status	交易状态
                patentInfo.put("trading_status", document.select(".item-main-status-tip").text().trim());
                //price	价格
                patentInfo.put("price", document.select(".item-main-price").text().trim());
                Elements infoList = document.select(".item-detail-a-inner-wrapper .item-main-line .item-main-key");
                for (Element e : infoList) {
                    String key = e.text().trim();
                    switch (key) {
                        case "申请号：":
                            //application_number	申请号
                            patentInfo.put("application_number", e.nextElementSibling().text().trim());
                            break;
                        case "申请日：":
                            //application_date	申请日期
                            patentInfo.put("application_date", e.nextElementSibling().text().trim());
                            break;
                        case "公开（公告）号：":
                            //publication_number	公开（公告）号
                            patentInfo.put("publication_number", e.nextElementSibling().text().trim());
                            break;
                        case "公开日：":
                            //publication_date	公开（公告）日期
                            patentInfo.put("publication_date", e.nextElementSibling().text().trim());
                            break;
                        case "申请（专利权）人：":
                            //applicant	申请（专利权）人
                            patentInfo.put("applicant", e.nextElementSibling().text().trim());
                            break;
                        case "发明人：":
                            //inventor	发明人
                            patentInfo.put("inventor", e.nextElementSibling().text().trim());
                            break;
                        case "主分类号：":
                            //main_classification_number	主分类号
                            patentInfo.put("main_classification_number", e.nextElementSibling().text().trim());
                            break;
                        case "分类号：":
                            //classification_number	分类号
                            patentInfo.put("classification_number", e.nextElementSibling().text().trim());
                            break;
                        case "地址：":
                            //adress	地址
                            patentInfo.put("adress", e.nextElementSibling().text().trim());
                            break;
                        case "国省代码：":
                            //country_code	国省代码
                            patentInfo.put("country_code", e.nextElementSibling().text().trim());
                            break;
                        case "代理机构：":
                            //agency	代理机构
                            patentInfo.put("agency", e.nextElementSibling().text().trim());
                            break;
                        case "代理人：":
                            //agent	代理人
                            patentInfo.put("agent", e.nextElementSibling().text().trim());
                            break;
                        default:
                    }
                }
                //assertion	权利要求 / 主权项
                patentInfo.put("assertion", document.select("#item-detail-a-b-wrapper > .item-detail-text").text().trim());
                //instructions	说明书
                patentInfo.put("instructions", document.select("#item-detail-a-c-wrapper > .item-detail-text").text().trim());
                //law_info	"法律信息 ， 一个JSON 格式字符串，存储样式为：
                //[{""date"":""法律状态公告日"",""status"":""法律状态"", ""info"":""法律状态信息""},{......}]"
                Elements itemlegaltable = document.select(".item-legal-table tr");
                for (Element e : itemlegaltable) {
                    if (0 != e.select("th").size()) {
                        JSONObject law = new JSONObject();
                        law.put("date", e.select("th").eq(0).text().trim());
                        law.put("status", e.select("th").eq(1).text().trim());
                        law.put("info", e.select("th").eq(2).text().trim());
                        lawInfo.add(law);
                    } else if (0 != e.select("td").size()) {
                        JSONObject law = new JSONObject();
                        law.put("date", e.select("td").eq(0).text().trim());
                        law.put("status", e.select("td").eq(1).text().trim());
                        law.put("info", e.select("td").eq(2).text().trim());
                        lawInfo.add(law);
                    }
                }
                //law_status	当前法律状态
                patentInfo.put("law_status", document.select("#item-detail-b-wrapper > div:nth-child(2) > span:nth-child(2)"));
                //summary_picture_url	摘要附图，每个图片url之间用英文逗（,）链接
                Elements img = document.select("#item-detail-e-wrapper img");
                for (Element e : img) {
                    imgInfo.add(e.attr("src"));
                }
                patentInfo.put("summary_picture_url", imgInfo);
                //tfc_picture_url	交易流程图
                Elements img2 = document.select("#item-detail-f-wrapper img");
                for (Element e : img2) {
                    img2Info.add(e.attr("src"));
                }
                patentInfo.put("tfc_picture_url", imgInfo);
                //file_url	专利文件下载链接
                patentInfo.put("file_url", document.select("#item-detail-a-wrapper > div:nth-child(1) > a"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
