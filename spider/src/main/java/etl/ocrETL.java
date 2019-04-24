package etl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class ocrETL {
    private final static Logger LOGGER = LoggerFactory.getLogger(ocrETL.class);
    private static String weifuwuPDF = "http://172.17.20.119:8081/1/ocr/pdf?pdf=";
    private static String weifuwuImage = "http://172.17.20.119:8081/1/ocr/baiduImage?image=";

    public static void main(String[] args) {
        ocrETL ocr = new ocrETL();
        ocr.getInfoFromPDF("C:/Users/liyujie/Desktop/neimeng/image.pdf");
    }

    public void getInfoFromPDF(String pdfPath) {
        try {
            String result = HttpRequestUtils.httpPost(weifuwuPDF + pdfPath, "");
            if (null != result) {
                JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
                String info = jsonObject.getString("res");
                String[] list = info.split("\\n");
            } else {
                LOGGER.info("http post result null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
