package parse.news.toRedis;

import Utils.RedisUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Utils.HttpUtil;

public class CnmoToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(CnmoToRedis.class);

    public static void main(String[] args) {
        CnmoToRedis cnmoToRedis = new CnmoToRedis();
        cnmoToRedis.homepage("http://www.cnmo.com/news/");
    }

    /**
     * 获取到分类列表，拿到需要的分类url。
     * 通过分类url获取每一页的url链接
     * @param url
     */
    public void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cnmo");
            if (null != html) {
                Document document = Jsoup.parse(html);
                /**
                 * 分类：categoryList
                 */
                Elements categoryList = document.select("div.topbar_f > div.w1200 > ul > li > a");
                for (Element e : categoryList) {
                    if (!e.text().contains("手机中国")&&!e.text().contains("视频")
                            &&!e.text().contains("手机库")&&!e.text().contains("汽车库")
                            &&!e.text().contains("论坛")&&!e.text().contains("软件下载")
                            &&!e.text().contains("首页")&&!e.text().contains("美图库")) {
                        String href = "http:"+e.attr("href");

                        /**
                         * 分页：pages
                         */
                        int number = 100;
                        String links = null;
                        for (int pages = 1; pages <= number; pages++) {
                            if (!href.contains("com/")) {
                                links = href + "/"+ pages + "/";
                            }else {
                                links = href + pages + "/";
                            }
                            newsList(links);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * 获取每页新闻列表，拿到每个新闻页面的url链接，
     * 将url链接放到redis中
     * @param url
     */

    private void newsList(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "cnmo");
            if (null!=html) {
                Document parse = Jsoup.parse(html);
                Elements select = parse.select(".rbox.rbox-2 > a");
                if (0!=select.size()) {
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
