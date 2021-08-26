package com.yunlu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BuildResult {
    private static Logger LOGGER = LoggerFactory.getLogger(BuildResult.class);

    public static Map<String, Object> buildResult(int code, Object result) {
        Map<String, Object> resultMap = null;
        try {
            resultMap = new HashMap<>();
            resultMap.put("code", code);
            resultMap.put("result", result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return resultMap;
    }
}
