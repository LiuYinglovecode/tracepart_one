package spider.patent.baiteng.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static spider.patent.baiteng.util.ElementExist.isElementExist;

/**
 * @author liyujie
 **/
public class Auxiliary {
    private static final Logger LOGGER = LoggerFactory.getLogger(Auxiliary.class);
    private static Map<String, String> handleMap = new HashMap();
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取 driver
     *
     * @param url
     * @return
     */
    public static WebDriver driver = null;

    public static WebDriver getDriver(String url) {
        try {
            driver = CreatChromeDriver.getChromeDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            driver.get(url);
            waitTime();
            return driver;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (null != driver) {
                closeDriver(driver);
            }
        }
        return null;
    }


    /**
     * 关闭 chrome驱动 以及 driver
     *
     * @param driver
     */
    public static void closeDriver(WebDriver driver) {
        if (null != driver) {
            driver.quit();
            CreatChromeDriver.service.stop();
        }

    }


    /**
     * 延迟
     */
    public static void waitTime() {
        try {
            int time1 = 2000;
            int time2 = 1000;
            //等待数据加载的时间
            Random random = new Random(System.currentTimeMillis());
            Thread.sleep(time1 + random.nextInt(time2));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取新页面handle
     *
     * @param driver
     * @return
     */
    public static WebDriver getHandleDriver(WebDriver driver, String handle) {
        try {
            waitTime();
            String patentHandle = handleMap.get(handle);
            if (null != patentHandle) {
                driver.switchTo().window(patentHandle);
                waitTime();
                return driver;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            closeDriver(driver);
        }
        return null;
    }


    /**
     * 关闭新窗口，返回至商品列表窗口
     *
     * @param driver
     * @return
     */
    public static void closeWindows(WebDriver driver, String targetHandle, String nowHandle) {

        try {
            driver.close();
            String Handle = handleMap.get(targetHandle);
            if (null != Handle) {
                driver.switchTo().window(Handle);
                handleMap.remove(nowHandle);
                waitTime();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            closeDriver(driver);
        }
    }

    /**
     * handle放入map
     *
     * @param driver
     * @return
     */
    public static boolean setHandle(WebDriver driver) {
        try {
            if ("0".equals(String.valueOf(handleMap.size()))) {
                String categoryListHandle = driver.getWindowHandle();
                handleMap.put("categoryPage", categoryListHandle);
                return true;
            } else if ("1".equals(String.valueOf(handleMap.size()))) {
                Set<String> handles = driver.getWindowHandles();
                for (Object h : handles) {
                    if (handleMap.containsKey("categoryPage") && !h.equals(handleMap.get("categoryPage"))) {
                        handleMap.put("patentPage", String.valueOf(h));
                        return true;
                    }
                }
            } else if ("2".equals(String.valueOf(handleMap.size()))) {
                Set<String> handles = driver.getWindowHandles();
                for (Object h : handles) {
                    if (handleMap.containsKey("categoryPage") && handleMap.containsKey("patentPage") && !h.equals(handleMap.get("patentPage")) && !h.equals(handleMap.get("categoryPage"))) {
                        handleMap.put("detailPage", String.valueOf(h));
                        return true;
                    }
                }
            } else {
                LOGGER.error("handleMap size err");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), "setHandle err");
        }
        return false;
    }

    /**
     * 登录
     * ".Js_login"
     */
    public static boolean login(WebDriver driver, String parmeter) {
        try {
            if (ElementExist.isElementExist(driver, parmeter) && "登录".equals(driver.findElement(By.cssSelector(parmeter)).getText().trim())) {
                WebElement login = driver.findElement(By.cssSelector(parmeter));
                login.click();
                for (int i = 0; i < 5; i++) {
                    if (isElementExist(driver, "input[name='login_mobile") && isElementExist(driver, "input[name='login_pwd")) {
                        waitTime();
                        driver.findElement(By.cssSelector("input[name='login_mobile']")).sendKeys("15010756102");
                        driver.findElement(By.cssSelector("input[name='login_pwd']")).sendKeys("0313410224");
                        waitTime();
                        driver.findElement(By.cssSelector(".JS_accountLoginBtn")).click();
                        break;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }
}
