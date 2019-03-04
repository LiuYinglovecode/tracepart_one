package ipregion;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpUtil;
import util.IConfigManager;
import util.IpProxyUtil;

import java.util.*;

public class IpRegionTaobao {
    static int proxyListLength;
    private final static Logger LOGGER = LoggerFactory.getLogger(IpRegionTaobao.class);
    private static final String URL = "http://ip.taobao.com/service/getIpInfo.php?ip=";


    public static String httpGetToTaobao(String ip, String proxy) {
        String result = null;
        String url = URL + ip;
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        try {
            result = HttpUtil.HttpURLConnectionGet(url, header, proxy);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }


    public static Set<String> getProxy() {
        return ProxyDao.getProxyFromRedis();
    }

    public static Map<String, String> resultJsontoMap(String result) {
        /**
         *  解析结果内容
         */
        Map<String, String> dataMap = null;
        if (result != null) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(result);
                JSONObject data = jsonObject.getJSONObject("data");
                if (data != null) {
                    dataMap = JSONObject.parseObject(String.valueOf(data), Map.class);
                    return dataMap;
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }

    public static boolean storeIpRegion(Map<String, String> ipRegionMap) {
        return IpRegionTaobaoDao.insertIpRegion(ipRegionMap);
    }


    public static List<String> getIpArr(String start, String end) {
        List<String> ips = new ArrayList<String>();
        String[] ipfromd = start.split("\\.");
        String[] iptod = end.split("\\.");
        int[] int_ipf = new int[4];
        int[] int_ipt = new int[4];
        for (int i = 0; i < 4; i++) {
            int_ipf[i] = Integer.parseInt(ipfromd[i]);
            int_ipt[i] = Integer.parseInt(iptod[i]);
        }
        for (int A = int_ipf[0]; A <= int_ipt[0]; A++) {
            for (int B = (A == int_ipf[0] ? int_ipf[1] : 0); B <= (A == int_ipt[0] ? int_ipt[1] : 255); B++) {
                for (int C = (B == int_ipf[1] ? int_ipf[2] : 0); C <= (B == int_ipt[1] ? int_ipt[2] : 255); C++) {
                    for (int D = (C == int_ipf[2] ? int_ipf[3] : 0); D <= (C == int_ipt[2] ? int_ipt[3] : 255); D++) {
                        try {
                            ips.add(new String(A + "." + B + "." + C + "." + D));
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return ips;
    }

    public static String proxyList(Set<String> set, int i) {
        ArrayList<String> list = new ArrayList<>(set);
        proxyListLength = list.size();
        String proxyIP = list.get(i);
        return proxyIP;
    }

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "192.168.125.136:2181");

        /**
         *      取到代理 循环遍历
         *  循环拿到ip，拿到代理
         */

        String start = args[0];
        String end = args[1];

        List<String> ipAll = getIpArr(start, end);
        IpProxyUtil ipProxyList = new IpProxyUtil();
        String ipProxy = null;
        for (int i = 0; i < ipAll.size(); i++) {
            long startTime = System.currentTimeMillis();
            LOGGER.info("startTime :" + startTime);
            if (ipProxyList.isEmpty()) {
                LOGGER.info("ipProxyList is empty");
                Set<String> getProxy = getProxy();
                ipProxyList.addProxyIp(getProxy);
            }
            String ip = ipAll.get(i);
            ipProxy = ipProxyList.getProxyIp();
            String ipResult = null;
            for (int j = 0; j < 5; j++) {
                LOGGER.info("第 " + j + " 次");
                ipResult = httpGetToTaobao(ip, ipProxy);
                if (ipResult != null && ipResult.contains("code") && ipResult.contains("data")) {
                    LOGGER.info(ip + " : 第 " + j + " 次获取ipResult成功");
                    break;
                }
                ipProxyList.removeProxyIpByOne(ipProxy);
                ProxyDao.delectProxyByOne(ipProxy);
                ipProxy = ipProxyList.getProxyIp();
            }
            if (ipResult == null || !ipResult.contains("code") || !ipResult.contains("data")) {
                continue;
            }
            Map<String, String> dataMap = resultJsontoMap(ipResult);
            LOGGER.info("resultJsontoMap");
            if (storeIpRegion(dataMap)) {
                long endTime = System.currentTimeMillis();
                long spendTime = (endTime - startTime) / 1000;
                LOGGER.info("上传ip ：" + ip + " 消耗时间 ：" + spendTime);
            }
        }
        System.exit(0);
    }
}
