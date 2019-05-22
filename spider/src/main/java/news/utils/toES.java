package news.utils;

import com.alibaba.fastjson.JSONObject;
import es.ESClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class toES {
    private static final Logger LOGGER = LoggerFactory.getLogger(toES.class);

    public static void writeToES(JSONObject info, String index, String type) {
        String date = String.format("%tY", new Date()) + "." + String.format("%tm", new Date()) + "." + String.format("%td", new Date());
        toES(info, index + date, type);
    }

    private static void toES(JSONObject info, String index, String type) {
        try {
            TransportClient transportClient = new ESClient.ESClientBuilder().createESClient().getClient();
            transportClient.prepareIndex(index, type)
                    .setSource(info, XContentType.JSON)
                    .execute()
                    .actionGet();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
