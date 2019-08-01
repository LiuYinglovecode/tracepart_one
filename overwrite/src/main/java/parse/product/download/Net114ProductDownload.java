package parse.product.download;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpUtil;
import util.MD5Util;
import util.mysqlUtil;


public class Net114ProductDownload {
    private final static Logger LOGGER = LoggerFactory.getLogger(Net114ProductDownload.class);


    //产品信息
    public void productInfo(String url) {
        JSONArray imgsList = new JSONArray();
        JSONObject productInfo = new JSONObject();
        try {
            productInfo.put("detailUrl", url);
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                productInfo.put("product_name", parse.select("dl.infobox-title dt h1").text().trim());//标题
                Elements elements = parse.select("dd.infobox-l-text p span");
                if (elements.size() != 0) {
                    for (Element element : elements) {
                        if (element.text().contains("￥")) {
                            productInfo.put("prices", element.text().split(" ")[0].replace("￥", ""));//价格
                        } else if (element.text().contains("面议")) {
                            productInfo.put("prices", element.text().trim());
                        } else if (element.text().contains("最小采购量：")) {
                            productInfo.put("mini_order", element.text().replace("最小采购量：", ""));//最小采购量
                        }
                    }
                } else {
                    productInfo.put("prices", "面议");
                }
                //正文 + 图片
                Elements elements1 = parse.select("dl.info-box-text dt p,dl.info-box-text dd p");
                for (Element e : elements1) {
                    if (e.text().contains("品牌：")) {
                        productInfo.put("product_brand", e.text().replace("品牌：", ""));
                    } else if (e.text().contains("所 在 地：")) {
                        productInfo.put("production_place", e.text().replace("所 在 地：", ""));
                    } else if (e.text().contains("产品卖点：")) {
                        productInfo.put("product_feature", e.text().replace("产品卖点：", ""));
                    } else if (e.text().contains("应用领域：")) {
                        productInfo.put("product_applications", e.text().replace("应用领域：", ""));
                    } else if (e.text().contains("详细参数：")) {
                        productInfo.put("product_feature", e.nextElementSibling().text().trim());
                    }
                }
                Elements text = parse.select("div.col-lg-12.col-md-12");
                productInfo.put("product_desc", text.text());
                Elements img = text.select("p img");
                if (img.size() != 0) {
                    for (Element imgs : img) {
                        imgsList.add(imgs.attr("src"));
                    }
                    productInfo.put("product_images", imgsList.toString());//图片
                }
                Elements attr = parse.select("a.btn.btn-danger");
                String href = attr.attr("href");

                String html1 = HttpUtil.httpGetwithJudgeWord(href, "net114");
                if (html1 != null) {
                    Document parse1 = Jsoup.parse(html1);
                    Elements name = parse1.select("#footer_default");
                    if (name.size() != 0) {
                        productInfo.put("company_name", name.text().split("地址")[0].replace("；", ""));
                        productInfo.put("company_id", MD5Util.getMD5String(name.text().split("地址")[0].replace("；", "")));
                    }
                    Elements name1 = parse1.select("#footer_diy_content_view");
                    if (name1.size() != 0) {
                        if (name1.text().contains("地址")) {
                            productInfo.put("company_name", name1.text().split("地址")[0].replace("；", ""));
                            productInfo.put("company_id", MD5Util.getMD5String(name1.text().split("地址")[0].replace("；", "")));
                        } else {
                            productInfo.put("company_name", name1.text().split("主营")[0].replace("；", ""));
                            productInfo.put("company_id", MD5Util.getMD5String(name1.text().split("主营")[0].replace("；", "")));
                        }
                    }
                    Elements name2 = parse1.select("#self_in_footer_diy_content p");
                    if (name2.size() != 0) {
                        productInfo.put("company_name", name2.text().replace("；", ""));
                        productInfo.put("company_id", MD5Util.getMD5String(name2.text().replace("；", "")));
                    }
                    Elements name3 = parse1.select("div.top_text_title");
                    if (name3.size() != 0) {
                        productInfo.put("company_name", name3.text());
                        productInfo.put("company_id", MD5Util.getMD5String(name3.text()));
                    }
                    Elements name4 = parse1.select("div.footerInfo.w > p");
                    if (name4.size() != 0) {
                        productInfo.put("company_name", name4.text().split("地址")[0].replace("；", ""));
                        productInfo.put("company_id", MD5Util.getMD5String(name4.text().split("地址")[0].replace("；", "")));
                    }

                } else {
                    LOGGER.info("页面无法解析！" + href);
                }

                productInfo.put("crawlerId", "18");
                mysqlUtil.insertProduct(productInfo);

            } else {
                LOGGER.info("页面为空！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}