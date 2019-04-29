package news.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * @author liyujie
 * http://www.cinn.cn/
 */
public class cinn {
    private static final Logger LOGGER = LoggerFactory.getLogger(cinn.class);
    private static Map<String, String> header;
    private static final String homepage = "http://www.cinn.cn/";

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        cinn cinn = new cinn();
        cinn.homepage(homepage);
    }

    private void homepage(String url) {
        try {
            String html = HttpUtil.httpGetwithJudgeWord(url, "关于我们");
            if (null != html) {
                Document document = Jsoup.parse(html);

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
