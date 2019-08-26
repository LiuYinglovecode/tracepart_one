package com.yunlu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

public class writeUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(writeUtil.class);

    public static synchronized void write(String file, String savePath) {
        try {
            FileWriter out = new FileWriter(savePath, true);
            out.write(String.valueOf(file));
            out.write("\r\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static synchronized void cleanFile(String savePath) {
        try {
            FileWriter fileWriter = new FileWriter(savePath, false);
            fileWriter.write(String.valueOf(""));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
