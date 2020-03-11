package Product_Company;


import Utils.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author liyujie
 * <p>
 * 网站 http://www.ok365.com/
 */
public class Ok365 {
    private final static Logger LOGGER = LoggerFactory.getLogger(Ok365.class);
    private static String LoginInUrl = "http://www.ok365.com";

    public static void main(String[] args) {
        try {
            Ok365 ok365 = new Ok365();
            ok365.category(LoginInUrl);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * 类型列表
     *
     * @param url
     */
    private void category(String url) {
        try {
            String categoryHtml = HttpUtil.httpGet(LoginInUrl, null);
            if (!"".equals(categoryHtml)) {
                Document document = Jsoup.parse(categoryHtml);
                Elements elements = document.select(".nav .container");
                elements.select("a").first().remove();
                for (Element e : elements) {
                    String categoryListUrl = LoginInUrl.concat(e.select("a").attr("href"));
                    categoryList(categoryListUrl);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 列表页
     *
     * @param url
     */
    private void categoryList(String url) {
        try {
            String categoryListHtml = HttpUtil.httpGet(url, null);
            if (!"".equals(categoryListHtml)) {
                Document document = Jsoup.parse(categoryListHtml);
                Elements elements = document.select(".panel-bd > .container > a");
                for (Element e : elements) {
                    String detailUrl = LoginInUrl.concat(e.attr("href"));
                    detail(detailUrl);
                }
                int nextLength = document.select(".next").size();
                if (1 == nextLength) {
                    categoryList(LoginInUrl.concat(document.select(".next a").attr("href")));
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 详情解析
     *
     * @param url
     */
    private void detail(String url) {
        try {
            JSONObject detailInfo = new JSONObject();
            String categoryListHtml = HttpUtil.httpGet(url, null);
            if (!"".equals(categoryListHtml)) {
                Document document = Jsoup.parse(categoryListHtml);
                detailInfo.put("leixing", document.select(".breadcrumbs a").eq(2).text());
                detailInfo.put("name", document.select(".name").text());
                Elements infobox2 = document.select(".container tbody").eq(2).select("tr");
                for (Element e : infobox2) {
                    detailInfo.put(e.select("dt").text().trim(), e.select("dl").text().trim());
                }
                Elements infobox = document.select(".page-bd .container .ml15.mr15 tbody tr td");
                for (Element e : infobox) {
                    detailInfo.put(e.select("p:first").text().trim(), e.select("p:first").text().trim());
                }
                detailInfo.put("source_id", 35);
                detailInfo.put("category_id", 1);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
