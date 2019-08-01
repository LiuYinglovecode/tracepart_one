package maima;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Maima {
    private static final Logger LOGGER = LoggerFactory.getLogger(Maima.class);
    private static String filePath = "C:\\Users\\liyujie\\Desktop\\3.txt";
    private static String base = "http://";
    private static String outPath = "C:\\Users\\liyujie\\Desktop\\3_out.csv";
    private static Map<String, String> header = null;

    static {
        header = new HashMap();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
    }

    public static void main(String[] args) {
        File file = new File(filePath);
        BufferedReader reader = null;
        String temp = null;
        int line = 1;
        try {
            reader = new BufferedReader(new FileReader(file));
            while (null != (temp = reader.readLine())) {
                if (temp.startsWith("1") || temp.startsWith("localhost")) {
                    System.out.println("line" + line + " : " + temp);
                    tracepart.util.Writer.writer("localhost && 1" + "," + temp.split("\\t", 3)[0] + temp.split("\\t", 3)[1], outPath);
                } else {
                    String url = base + temp.split("\\t", 3)[0] + temp.split("\\t", 3)[1];
                    try {
                        System.out.println("line" + line + " : " + url);
//                    url = "http://www.jxicloud.com/f/companyshow/serviceabilityDetail/c75ef6f5220943c5afadccc76b8813e8.html";
                        String html = HttpUtil.httpGet(url, header);
                        if (!"".equals(html) && html.contains("<script type=\"text/javascript\" src=\"https://stat.htres.cn")) {
                            String maima = html.substring(html.indexOf("<script type=\"text/javascript\" src=\"https://stat.htres.cn") + 36, html.indexOf("\"", html.indexOf("<script type=\"text/javascript\" src=\"https://stat.htres.cn") + 36));
                            String id = maima.split("\\?", 2)[1];
                            tracepart.util.Writer.writer("true" + "," + url + "," + maima + "," + id, outPath);
                        } else if (!"".equals(html) && html.contains("<script type=\"text/javascript\" src=\"http://stat.htres.cn")) {
                            String maima = html.substring(html.indexOf("<script type=\"text/javascript\" src=\"http://stat.htres.cn") + 36, html.indexOf("\"", html.indexOf("<script type=\"text/javascript\" src=\"http://stat.htres.cn") + 36));
                            String id = maima.split("\\?", 2)[1];
                            tracepart.util.Writer.writer("true" + "," + url + "," + maima + "," + id, outPath);
                        } else if (!"".equals(html) && !html.contains("<script type=\"text/javascript\" src=\"https://stat.htres.cn") && !html.contains("<script type=\"text/javascript\" src=\"http://stat.htres.cn")) {
                            tracepart.util.Writer.writer("false" + "," + url, outPath);
                        } else if ("".equals(html)) {
                            tracepart.util.Writer.writer("bad url" + "," + url, outPath);
                        }
                    } catch (Exception e) {
                        LOGGER.info("line:" + line + "\t;\t" + url + "\t;\t" + "Err Url");
                        tracepart.util.Writer.writer("err url" + "," + url, outPath);
                        LOGGER.error(e.getMessage());
                    }
                }
                line++;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        LOGGER.info("maima DONE");
    }
}
