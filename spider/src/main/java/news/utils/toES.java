package news.utils;

import com.alibaba.fastjson.JSONObject;
import es.ESClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class toES {
    private static final Logger LOGGER = LoggerFactory.getLogger(toES.class);

    public static void toES(JSONObject info, String index, String type) {
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
