package spider.patent.baiteng.util;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class CreatChromeDriver {

    public static ChromeDriverService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(CreatChromeDriver.class);

    public static WebDriver getChromeDriver() {
        try {
            // 设置 chrome 的路径
            String chromePath = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
            System.setProperty("webdriver.chrome.driver", chromePath);
            // 创建一个 ChromeDriver 的接口，用于连接 Chrome（chromedriver.exe 的路径可以任意放置，只要在newFile（）的时候写入你放的路径即可）
            service = new ChromeDriverService.Builder().usingDriverExecutable(new File("E:\\chromedriver_win32\\chromedriver.exe")).usingAnyFreePort().build();
            service.start();
            return new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
//            return new ChromeDriver(service, setChromeOptions());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            service.stop();
            return null;
        }
    }

    private static ChromeOptions setChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        // --start-maximized 浏览器最大化
        options.addArguments("--start-maximized");
        options.addArguments("--headless");
        options.addArguments("--proxy-server=http://" + "80.76.240.168:32813");
        return options;
    }
}