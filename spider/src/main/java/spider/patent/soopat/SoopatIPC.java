package spider.patent.soopat;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoopatIPC {
    private final static Logger LOGGER = LoggerFactory.getLogger(SoopatIPC.class);

    public static JSONArray getMainClassify() {
        try {
            JSONArray mainClassify = new JSONArray();
            mainClassify.add("/IPC/Parent/A");
            mainClassify.add("/IPC/Parent/B");
            mainClassify.add("/IPC/Parent/C");
            mainClassify.add("/IPC/Parent/D");
            mainClassify.add("/IPC/Parent/E");
            mainClassify.add("/IPC/Parent/F");
            mainClassify.add("/IPC/Parent/G");
            mainClassify.add("/IPC/Parent/H");
            return mainClassify;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
}
