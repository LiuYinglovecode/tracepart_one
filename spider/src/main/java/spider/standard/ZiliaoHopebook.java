package spider.standard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spider.download.downLoadFromUrl;
import util.HttpUtil;

/**
 * @author liyujie
 * http://ziliao.hopebook.net/hjziliao/
 */
public class ZiliaoHopebook {
    private final static Logger LOGGER = LoggerFactory.getLogger(ZiliaoHopebook.class);
    private static String inUrl = "http://ziliao.hopebook.net/catalog.htm";
    private static String baseUrl = "http://ziliao.hopebook.net";

    public static void main(String[] args) {
        ZiliaoHopebook ziliaoHopebook = new ZiliaoHopebook();
        ziliaoHopebook.dataClassification(inUrl);
    }

    private void dataClassification(String inUrl) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(inUrl, "免责声明");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements classificationList = document.select(".f14.lh15 tbody tr a");
                for (Element e : classificationList) {
                    String classificationUrl = e.attr("href");
                    if (!classificationUrl.contains("-")) {
                        dataList(classificationUrl);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void dataList(String classificationUrl) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(classificationUrl, "免责声明");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements detailList = document.select(".f12.c6.lh25.st tbody tr");
                for (Element e : detailList) {
                    if ("5".equals(String.valueOf(e.select("td").size())) && "0分".equals(e.select("td:nth-child(3)").text().trim())) {
                        String detailUrl = e.select("td:nth-child(2) a").attr("href");
                        detail(detailUrl);
                    }
                }
                nextPage(document);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void nextPage(Document document) {
        int pageOn = Integer.valueOf(document.select(".f12.c6.lh25.st tbody tr .page_on").text().trim());
        int nextPage = pageOn + 1;
        int lastPage = Integer.valueOf(document.select(".f12.c6.lh25.st tbody tr:last-child a:last-child").attr("href").split("_", 2)[1].split("\\.", 2)[0]);
        if (pageOn < lastPage) {
            dataList("http://ziliao.hopebook.net/jzziliao/index_" + nextPage + ".htm");
        }
    }

    private void detail(String detailUrl) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(detailUrl, "免责声明");
            if (null != html) {
                Document document = Jsoup.parse(html);
                String download = baseUrl + document.select("#downLoad").attr("action");
                String name = document.select(".lh30.f14.c3.mt15").text().trim();
                String type = "pdf";
                Elements typeList = document.select(".f12.c9.lh15.mb10 tr td");
                for (Element e : typeList) {
                    if (e.text().trim().contains("资料格式")) {
                        type = e.text().trim().split("：", 2)[1];
                        break;
                    }
                }
                downLoadFromUrl.downLoadFromUrl(download, name + "." + type, "F:\\0701");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
