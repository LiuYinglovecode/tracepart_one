package spider.patent.soopat;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.text.SimpleDateFormat;

import static spider.patent.soopat.SoopatIPC.*;

/**
 * @author lyj
 * url : http://www.soopat.com/IPC/Parent/A
 */
public class Soopat {
    private final static Logger LOGGER = LoggerFactory.getLogger(Soopat.class);
    private static SimpleDateFormat crawlerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String baseUrl = "http://www.soopat.com/IPC/Parent/";

    public void crawlerStart() {
        try {
            JSONArray mainClassify = SoopatIPC.getMainClassify();
            if (null != mainClassify) {
                for (Object f : mainClassify) {
                    String classifyLv1Url = baseUrl + f;
                    classifyLv1(classifyLv1Url);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void classifyLv1(String url) {
        String html = HttpUtil.httpGetwithJudgeWord()
    }

}
