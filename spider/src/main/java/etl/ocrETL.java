package etl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class ocrETL {
    private final static Logger LOGGER = LoggerFactory.getLogger(ocrETL.class);

    public static void main(String[] args) {
        try {
            String result = HttpRequestUtils.httpPost("http://172.17.20.119:8081/1/ocr/pdf?pdf=F:/5.pdf", "");
            if (null != result) {
                JSONObject jsonObject = new JSONObject(Boolean.parseBoolean(result));
            } else {
                LOGGER.info("http post result null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
}
