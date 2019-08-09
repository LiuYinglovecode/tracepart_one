package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

public class Pm8ToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pm8ToRedis.class);
    private static final String baseUrl = "http://www.pm8.cn/news/";



    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "制药设备网");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select(" tr > td > a");
                for (Element e : categoryList) {
                    if (e.text().contains("更多>>")) {
                        String href = "http://www.pm8.cn" + e.attr("href");
                        paging(href);
                    }
                }
            } else {
                LOGGER.info("homepage null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    //分页
    private void paging(String url) {
        try {
            String replace = url.split("list_")[1].replace(".html","");
            String replace1 = url.split("_list")[0];
            String html = HttpUtil.httpGetwithJudgeWord(url, "制药设备网");
            Document parse = Jsoup.parse(html);
            String pagesNumber = parse.select("td.recruit_kj > table > tbody > tr > td[height=20]")
                    .first().text().replace("共有","").replace("条记录","");//获取总结数
            int ceil = (int) Math.ceil(Double.parseDouble(pagesNumber) / 50);
            int number = 1;
            for (number = 1; number <= ceil; number++) {
                String link = replace1 + "_list.php?gopage=" + number + "&news_sort_id=" + replace;//拼接链接地址
                newsList(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "制药设备网");
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("table.f14 > tbody > tr > td > a");
            for (Element e : select) {
                String link = baseUrl + e.attr("href");
                RedisUtil.insertUrlToSet("toCatchUrl", link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
