import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import util.mysqlUtil;

public class mysqlTest {

    @Test
    public static void main(String[] args) {
        JSONObject info = new JSONObject();
        info.put("newsId","testId");
        info.put("title","title");
        mysqlUtil.insertNews(info, "crawler_news", "newsId");
    }
}
