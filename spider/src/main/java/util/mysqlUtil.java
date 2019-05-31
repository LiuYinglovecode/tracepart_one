package util;

import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class mysqlUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(mysqlUtil.class);
    private static java.util.Map<String, String> Map;

    public static void insertNews(JSONObject info, String tablename, String title) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.exist2(Map, tablename, title, "title")) {
                if (updateToMySQL.newsUpdate(Map, title, "title")) {
                    LOGGER.info("更新中 : " + Map.toString());
                }
            } else {
                if (updateToMySQL.newsInsert(Map)) {
                    LOGGER.info("插入中 : " + Map.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void insertPatent(JSONObject info) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.patentInsert(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
