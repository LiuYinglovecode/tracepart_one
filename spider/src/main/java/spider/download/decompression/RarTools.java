package spider.download.decompression;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class RarTools {

    /**
     * 解压rar到指定位置
     * @param sourceRarPath 需要解压的rar文件全路径
     * @param destDirPath   需要解压到的文件目录
     * @throws Exception
     */
    public static void unrar(String sourceRarPath, String destDirPath) {
        File sourceRar = new File(sourceRarPath);
        File destDir = new File(destDirPath);
        System.out.println(sourceRarPath);
        Archive archive = null;
        FileOutputStream fos = null;
        System.out.println("Starting 开始解压...");
        try {
            archive = new Archive(new FileInputStream(sourceRar));
            FileHeader fh = archive.nextFileHeader();
            int count = 0;
            File destFileName = null;
            while (fh != null) {
                System.out.println((++count) + ") " + fh.getFileNameW().trim());
                String compressFileName = fh.getFileNameW().trim();
                destFileName = new File(destDir.getAbsolutePath() + "/" + compressFileName);
                if (fh.isDirectory()) {
                    if (!destFileName.exists()) {
                        destFileName.mkdirs();
                    }
                    fh = archive.nextFileHeader();
                    continue;
                }
                if (!destFileName.getParentFile().exists()) {
                    destFileName.getParentFile().mkdirs();
                }


                fos = new FileOutputStream(destFileName);
                archive.extractFile(fh, fos);
                fos.close();
                fos = null;
                fh = archive.nextFileHeader();
            }

            archive.close();
            archive = null;
            System.out.println("Finished 解压完成!");
        } catch (Exception e) {
            try {
                throw e;
            } catch (RarException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                }
            }
            if (archive != null) {
                try {
                    archive.close();
                    archive = null;
                } catch (Exception e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            int line = 1;
            while (true) {
                //从指定位置过去rar压缩包，解压到指定位置
                RarTools.unrar("C:\\Users\\cyan_\\Downloads\\URL\\" + line + ".rar", "C:\\Users\\cyan_\\Downloads\\URL");
                //                Thread.sleep(3000);
                line++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




