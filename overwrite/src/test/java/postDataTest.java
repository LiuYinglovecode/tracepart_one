package Utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class postData {

    /**
     * @param url   请求接口
     * @param param 需要的json字符串
     * @return :java.lang.String
     * @author : cjd
     * @description : post接口 返回结果字符串
     * @params : [url, param]
     * @date : 17:31 2018/8/1
     */

    public static String sendPost(String url, String param) {

        try {
            OutputStreamWriter out = null;

            BufferedReader in = null;

            String result = "";
            try {
                String filePath = "F:\\test.txt";
                File file = new File(filePath);
                FileInputStream fileInputStream = new FileInputStream(file);
                // MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
                // 其中originalFilename,String contentType 旧名字，类型  可为空
                // ContentType.APPLICATION_OCTET_STREAM.toString() 需要使用HttpClient的包
                MultipartFile multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(),ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);
                System.out.println(multipartFile.getName()); // 输出copytest.txt

                URL realUrl = new URL(url);

                HttpURLConnection conn = null;            // 打开和URL之间的连接

                conn = (HttpURLConnection) realUrl.openConnection();            // 发送POST请求必须设置如下两行

                conn.setDoOutput(true);

                conn.setDoInput(true);

                conn.setRequestMethod("POST");    // POST方法


                // 设置通用的请求属性

                conn.setRequestProperty("accept", "*/*");

                conn.setRequestProperty("connection", "Keep-Alive");

                conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

                conn.connect();            // 获取URLConnection对象对应的输出流

                out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");            // 发送请求参数

                out.write(param);            // flush输出流的缓冲

                out.flush();            // 定义BufferedReader输入流来读取URL的响应

                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {

                    result += line;

                }

            } catch (Exception e) {

                System.out.println("发送 POST 请求出现异常！" + e);

                e.printStackTrace();

            }        //使用finally块来关闭输出流、输入流

            finally {
                try {
                    if (out != null) {

                        out.close();

                    }
                    if (in != null) {

                        in.close();

                    }

                } catch (IOException ex) {

                    ex.printStackTrace();

                }

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
