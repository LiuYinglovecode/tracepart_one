package news.utils;

import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class toMySQL {
    private static final Logger LOGGER = LoggerFactory.getLogger(toMySQL.class);
    private static java.util.Map<String, String> Map;

    public static void insert(JSONObject info, String tablename, String title, String type) {
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
}
