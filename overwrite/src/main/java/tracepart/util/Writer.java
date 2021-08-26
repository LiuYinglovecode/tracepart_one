package tracepart.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * By haozhiqiang , 2018/8/14
 **/
public class Writer {

    /**
     * 进行写入操作
     *
     * @param PATH
     */
    public static void writer(String info, String PATH) {
        FileOutputStream fs;
        OutputStreamWriter ow;
        BufferedWriter writeFile = null;

        try {
            fs = new FileOutputStream(PATH, true);    // true:追加内容写入，不会覆盖已写入信息
            ow = new OutputStreamWriter(fs);
            writeFile = new BufferedWriter(ow);
            writeFile.write(info);
            writeFile.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writeFile) {
                try {
                    writeFile.close();              // 关闭 writeFile
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Writer.writer("234","G:/T.csv");
    }
}
