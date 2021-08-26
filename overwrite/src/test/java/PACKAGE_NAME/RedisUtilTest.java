//package PACKAGE_NAME;//import Utils.RedisUtil;
//import org.junit.Test;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.*;
//
////
////public class RedisUtilTest {
////    @Test
////    public void main() {
//////        int a = RedisUtil.removeKey("tpCatched");
//////        int b = RedisUtil.getUrlNumber("tracepartToCatchUrl");
//////        System.out.println(b);
////
////        RedisUtil.isExist("tracepartToCatchUrl","https://www.traceparts.cn/zh/product/letoile-anchor-bolt-with-hook-16-x-300-threaded-1-nut?CatalogPath=TRACEPARTS%3ATP01001013007&Product=10-13012006-101849&PartNumber=570001161");
////
////        RedisUtil.insertUrlToSet("tracepatrTest","https://www.traceparts.cn/zh/product/letoile-anchor-bolt-with-hook-16-x-300-threaded-1-nut?CatalogPath=TRACEPARTS%3ATP01001013007&Product=10-13012006-101849&PartNumber=570001161");
////        int a = RedisUtil.getUrlNumber("tracepatrTest");
////        System.out.println(a);
////        String move = RedisUtil.getSrandmember("tracepatrTest");
////        RedisUtil.urlMove("tracepatrTest", "tracepartToCatchUrl", move);
////        a = RedisUtil.getUrlNumber("tracepatrTest2");
////        System.out.println(a);
////    }
////}
//public class RedisUtilTest {
//
//    @Test
//    public void main() {
//
//        // 第一步：创建一个JedisPool对象。需要指定服务端的ip及端口。
//        JedisPool jedisPool = new JedisPool("172.20.4.71", 12000);
//
//        // 第二步：从JedisPool中获得Jedis对象。
//        Jedis jedis = jedisPool.getResource();
//
//        // 第三步：使用Jedis操作redis服务器。
//        jedis.set("jedis","test");
//
//        String result = jedis.get("jedis");
//
//        System.out.println(result);
//
//        // 第四步：操作完毕后关闭jedis对象，连接池回收资源。
//        jedis.close();
//        // 第五步：关闭JedisPool对象。
//        jedisPool.close();
//
//    }
//}
