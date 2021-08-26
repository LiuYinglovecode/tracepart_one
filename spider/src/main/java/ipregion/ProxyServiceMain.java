package ipregion;

import org.apache.http.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ProxyServiceMain {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyServiceMain.class);
    private static final int PROXY_LIMIT = 10000;
    private static final String hangtianyunwang = "http://www.casicloud.com/article/zhigaifangan2/";
    private static final int sleepTime = 5000;
    private static Map<String, String> map = null;
    private static Map<String, String> header = null;
    private static String daili = "https://www.kuaidaili.com/login/?next=/getproxy/%3Forderid%3D902989670537257%26num%3D100%26area%3D%26area_ex%3D%26port%3D%26port_ex%3D%26ipstart%3D%26ipstart_ex%3D%26carrier%3D0%26an_ha%3D1%26an_an%3D1%26sp1%3D1%26protocol%3D1%26method%3D1%26quality%3D0%26sort%3D0%26b_pcchrome%3D1%26b_pcie%3D1%26b_pcff%3D1%26showtype%3D1";

    static {
        map = new HashMap<>();
        map.put("username", "liuzhiying@htyunwang.com");
        map.put("passwd", "daili123456");
        map.put("next", "https://www.kuaidaili.com/getproxy/?orderid=914994848507181&num=100&area=&area_ex=&port=&port_ex=&ipstart=&ipstart_ex=&carrier=0&an_ha=1&an_an=1&protocol=1&method=2&quality=0&sort=0&b_pcchrome=1&b_pcie=1&b_pcff=1&showtype=1");
    }

    static {
        header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Origin", "www.kuaidaili.com");
        header.put("Referer", daili);
        header.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        header.put("accept-encoding", "gzip, deflate, br");
    }

    public static void main(String[] args) {
        long startTime = 0;
        long endTime = 0;
        long spendTime = 0;
        while (true) {
            while (ProxyDao.getCountFromKey() < PROXY_LIMIT) {
                startTime = System.currentTimeMillis();
                LOGGER.info("startTime : " + startTime);
                Set<String> proxySet = HttpUtil.getProxysWithPost(daili, header, map);
                Set<String> set = new HashSet<>();
                for (String s : proxySet) {
                    /**
                     *  判断ip是否可用，如果可用加入set
                     *  上传Redis
                     */
                    try {
                        String result = HttpUtil.httpGetWithProxy(hangtianyunwang, null, s);
                        Document resultSoup = Jsoup.parse(result);
                        int size = resultSoup.select(".header_news.clearfix").size();
                        if (size != 0) {
                            set.add(s);
                            ProxyDao.insetProxyToRedis(set);
                            LOGGER.info("可用ip : " + s + "   insetProxyToRedis");
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage());
                    } catch (HttpException e) {
                        LOGGER.error(e.getMessage());
                    }
                }

                endTime = System.currentTimeMillis();
                spendTime = endTime - startTime;
                LOGGER.info("spendTime : " + spendTime);
                if (spendTime < 5000) {
                    try {
                        Thread.sleep(sleepTime);
                        LOGGER.info("sleep : " + sleepTime);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        }
    }
}

