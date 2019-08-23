import Utils.RedisUtil;
import org.junit.Test;

public class RedisUtilTest {
    @Test
    public static void main(String[] args) {
//        int a = RedisUtil.removeKey("tpCatched");
//        int b = RedisUtil.getUrlNumber("tracepartToCatchUrl");
//        System.out.println(b);

//        RedisUtil.isExist("tracepartToCatchUrl","https://www.traceparts.cn/zh/product/letoile-anchor-bolt-with-hook-16-x-300-threaded-1-nut?CatalogPath=TRACEPARTS%3ATP01001013007&Product=10-13012006-101849&PartNumber=570001161");

//        RedisUtil.insertUrlToSet("tracepatrTest","https://www.traceparts.cn/zh/product/letoile-anchor-bolt-with-hook-16-x-300-threaded-1-nut?CatalogPath=TRACEPARTS%3ATP01001013007&Product=10-13012006-101849&PartNumber=570001161");
        int a = RedisUtil.getUrlNumber("tracepatrTest");
        System.out.println(a);
        String move = RedisUtil.getSrandmember("tracepatrTest");
        RedisUtil.urlMove("tracepatrTest", "tracepartToCatchUrl", move);
        a = RedisUtil.getUrlNumber("tracepatrTest2");
        System.out.println(a);
    }
}
