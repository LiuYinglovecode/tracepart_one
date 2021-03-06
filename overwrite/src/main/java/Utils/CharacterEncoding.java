package Utils;


import info.monitorenter.cpdetector.io.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


/**
 * @author chenyan
 * 2019-10-29
 */
public class CharacterEncoding {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterEncoding.class);

    /**
     * 测试用例
     *
     * @param args
     */
    public static void main(String[] args) {

//        String charset = getEncodingByContentStream("https://www.51dzw.com/business/");
//        String charset = getEncodingByContentUrl("https://www.51dzw.com/business/");
        String charset = getEncodingByHeader("https://www.51dzw.com/business/");

        System.out.println(charset);
    }

    /**
     * 从header中获取页面编码
     *
     * @param strUrl
     * @return
     */
    public static String getEncodingByHeader(String strUrl) {
        String charset = null;
        try {
            URLConnection urlConn = new URL(strUrl).openConnection();
            // 获取链接的header
            Map<String, List<String>> headerFields = urlConn.getHeaderFields();
            // 判断headers中是否存在Content-Type
            if (headerFields.containsKey("Content-Type")) {
                //拿到header 中的 Content-Type ：[text/html; charset=utf-8]
                List<String> attrs = headerFields.get("Content-Type");
                String[] as = attrs.get(0).split(";");
                for (String att : as) {
                    if (att.contains("charset")) {
//                        System.out.println(att.split("=")[1]);
                        try {
                            charset = att.split("=")[1];
                        } catch (Exception e) {
                            LOGGER.info("链接超时");
                        }
                    }
                }
            }
            return charset;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return charset;
        } catch (IOException e) {
            e.printStackTrace();
            return charset;
        }
    }

    /**
     * 从meta中获取页面编码
     *
     * @param strUrl
     * @return
     */
    public static String getEncodingByMeta(String strUrl) {
        String charset = null;
        try {
            URLConnection urlConn = new URL(strUrl).openConnection();
            //避免被拒绝
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            // 将html读取成行,放入list
            List<String> lines = IOUtils.readLines(urlConn.getInputStream());
            for (String line : lines) {
                if (line.contains("http-equiv") && line.contains("charset")) {
//                    System.out.println(line);
                    String tmp = line.split(";")[1];
                    charset = tmp.substring(tmp.indexOf("=") + 1, tmp.indexOf("\""));
                } else {
                    continue;
                }
            }
            return charset;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return charset;
        } catch (IOException e) {
            e.printStackTrace();
            return charset;
        }
    }

    /**
     * 根据网页内容获取页面编码
     * case : 适用于可以直接读取网页的情况(例外情况:一些博客网站禁止不带User-Agent信息的访问请求)
     *
     * @param url
     * @return
     */
    public static String getEncodingByContentUrl(String url) {
        CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
        cdp.add(JChardetFacade.getInstance());// 依赖jar包 ：antlr.jar & chardet.jar
        cdp.add(ASCIIDetector.getInstance());
        cdp.add(UnicodeDetector.getInstance());
        cdp.add(new ParsingDetector(false));
        cdp.add(new ByteOrderMarkDetector());

        Charset charset = null;
        try {
            charset = cdp.detectCodepage(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(charset);
        return charset == null ? null : charset.name().toLowerCase();
    }

    /**
     * 根据网页内容获取页面编码
     * case : 适用于不可以直接读取网页的情况,通过将该网页转换为支持mark的输入流,然后解析编码
     *
     * @param strUrl
     * @return
     */
    public static String getEncodingByContentStream(String strUrl) {
        Charset charset = null;
        try {
            URLConnection urlConn = new URL(strUrl).openConnection();
            //打开链接,加上User-Agent,避免被拒绝
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");

            //解析页面内容
            CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
            cdp.add(JChardetFacade.getInstance());// 依赖jar包 ：antlr.jar & chardet.jar
            cdp.add(ASCIIDetector.getInstance());
            cdp.add(UnicodeDetector.getInstance());
            cdp.add(new ParsingDetector(false));
            cdp.add(new ByteOrderMarkDetector());
            ByteArrayInputStream bais = null;
            try {

                bais = new ByteArrayInputStream(IOUtils.toByteArray(urlConn.getInputStream()));
            } catch (IOException e) {
                LOGGER.info("链接超时");
            }
            // detectCodepage(InputStream in, int length) 只支持可以mark的InputStream
            try {
                charset = cdp.detectCodepage(bais, 2147483647);
            } catch (IOException e) {
               LOGGER.error(e.getMessage());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return charset == null ? null : charset.name().toLowerCase();
    }
}