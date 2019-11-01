package parse.news.toRedis;

import Utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

public class ThepaperToRedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThepaperToRedis.class);

    public static void main(String[] args) {
        ThepaperToRedis thepaperToRedis = new ThepaperToRedis();
        thepaperToRedis.homepage("https://www.thepaper.cn/channel_25951");
    }
    public void homepage(String url) {
        //新闻列表
        try {
            String link = url.replace("channel_25951", "");
            while (true) {
                Random rand = new Random();
                StringBuffer flag = new StringBuffer();
                for (int j = 0; j < 7; j++) {
                    flag.append(rand.nextInt(10));
                }
                String href = link + "newsDetail_forward_" + (flag.toString());
                Thread.sleep(500);
                RedisUtil.insertUrlToSet("toCatchUrl",href);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
