package com.yunlu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");

    public static String getTime() {
        try {
            return dateFormat.format(new Date());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

}

