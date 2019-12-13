package parse.news.toRedis;

import Utils.HttpUtil;
import Utils.RedisUtil;
import Utils.SleepUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EastmoneyToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(EastmoneyToRedis.class);
    private static final String beasUrl = "http://finance.eastmoney.com/a/";

    public static void main(String[] args) {
        EastmoneyToRedis eastmoneyToRedis = new EastmoneyToRedis();
        eastmoneyToRedis.homepage("http://finance.eastmoney.com/");
    }

    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "eastmoney");
            if (null != html) {
                Document document = Jsoup.parse(html);
                Elements categoryList = document.select("#header > div.menu_wrap > ul > li > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("焦点")
                            && !e.text().contains("上市公司")
                            && !e.text().contains("数据")
                            && !e.text().contains("经济数据")
                            && !e.text().contains("基金数据")
                            && !e.text().contains("人物")
                            && !e.text().contains("评论")
                            && !e.text().contains("财经媒体")
                            && !e.text().contains("财经会议")
                            && !e.text().contains("财经专题")
                            && !e.text().contains("财经视频")
                            && !e.text().contains("股市日历")
                            && !e.text().contains("财富观察")) {
                        String href = e.attr("href");
//                        System.out.println(href);
//                        paging(href);
                        newsList(href);
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
//    private void paging(String href) {
//        try {
//            String html = HttpUtil.httpGetwithJudgeWord(href, "eastmoney");
//            if (html != null) {
//                Document parse = Jsoup.parse(html);
//                int number = 1;
//                String attr = parse.select("div.pagelist a,#pager a").last().attr("href");
//                String url = attr.split("=")[0];
//                String Number = attr.split("=")[1];
//                int total = Integer.valueOf(Number).intValue();//类型转换
//                for (number = 1; number <= total; number++) {
//                    if (url.contains("Interact")) {
//                        String link = "http://www.eapad.cn" + url + "=" + number;//拼接链接地址
////                        System.out.println("下一页：" + link);
//                        newsList(link);
//                    } else {
//                        String link = beasUrl + url + "=" + number;//拼接链接地址
////                        System.out.println("下一页：" + link);
//                        newsList(link);
//                    }
//                }
//            } else {
//                System.out.println("页面不存在");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void newsList(String href) {
        try {
            String replace = href.replace(".html", "");
            for (int i = 1; i <= 25; i++) {
                String concat = replace.concat("_").concat(String.valueOf(i)).concat(".html");
                System.out.println("下一页："+concat);
                Thread.sleep(SleepUtils.sleepMin());
                String html = HttpUtil.httpGetwithJudgeWord(concat, "eastmoney");
                if (html != null) {
                    Document parse = Jsoup.parse(html);
                    Elements select = parse.select("p.title > a");
                    for (Element e : select) {
                        String link = e.attr("href");
                        RedisUtil.insertUrlToSet("toCatchUrl", link);

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
