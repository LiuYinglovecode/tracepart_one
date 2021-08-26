package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;


public class EapadToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(EapadToRedis.class);
    private static final String beasUrl = "http://news.eapad.cn";

    public static void main(String[] args) {
        EapadToRedis eapadToRedis = new EapadToRedis();
        eapadToRedis.homepage("http://www.eapad.cn/");
    }
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("ul.list.clearfix li a");
                for (Element e : categoryList) {
                    if (e.text().contains("口碑头条") || e.text().contains("最新资讯")
                            ||e.text().contains("口碑警示") || e.text().contains("电商")
                        || e.text().contains("人物对话") || e.text().contains("原创专栏")) {
                        String href = e.attr("href");
                        paging(href);
//                        newsList(href);
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
    private void paging(String href) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(href, "联系我们");
            if (html != null) {
                Document parse = Jsoup.parse(html);
                int number = 1;
                String attr = parse.select("div.pagelist a,#pager a").last().attr("href");
                String url = attr.split("=")[0];
                String Number = attr.split("=")[1];
                int total = Integer.valueOf(Number).intValue();//类型转换
                for (number = 1; number <= total; number++) {
                    if (url.contains("Interact")) {
                        String link = "http://www.eapad.cn" + url + "=" + number;//拼接链接地址
//                        System.out.println("下一页：" + link);
                        newsList(link);
                    } else {
                        String link = beasUrl + url + "=" + number;//拼接链接地址
//                        System.out.println("下一页：" + link);
                        newsList(link);
                    }
                }
            } else {
                System.out.println("页面不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "联系我们");
            if (html!=null) {
            Document parse = Jsoup.parse(html);
            Elements select = parse.select("div.t > a,dl.mt_bottom20 dt a,dl.mt_bottom20 dd h2 a");
                for (Element e : select) {
                    if (e.attr("href").contains("http")) {
                        String link = e.attr("href");
//                    System.out.println(link);
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                    } else {
                        String link = beasUrl + e.attr("href");
//                    System.out.println(link);
                        RedisUtil.insertUrlToSet("toCatchUrl", link);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
