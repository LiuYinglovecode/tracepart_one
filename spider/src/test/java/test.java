import org.apache.http.HttpException;
import org.junit.Test;
import util.HttpUtil;

import java.io.IOException;

public class test {
    @Test
    public void main() {
        try {
            HttpUtil.httpGet("http://c.gb688.cn/bzgk/gb/showGb?type=online&hcno=483F2A2C7D1BF28B4BDB33E9B91EADFD",null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
