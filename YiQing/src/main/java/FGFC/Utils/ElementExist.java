package FGFC.Utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于判断页面元素是否存在
 */
public class ElementExist {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElementExist.class);

    public static boolean isclick(WebDriver driver, String parmeter) {
        try {
            if (null != driver && null != parmeter) {
                for (int i = 0; i < 5; i++) {
                    WebElement isclick = new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.cssSelector(parmeter)));
                    if (isclick.isEnabled()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public static boolean isElementExist(WebDriver driver, String parmeter) {
        try {
            if (null != driver && null != parmeter) {
                for (int i = 0; i < 5; i++) {
                    WebDriverWait wait = new WebDriverWait(driver, 10);
                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(parmeter)));
                    if (element.isDisplayed()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }


    // 检测 WebElement
    public static boolean WebElementExist(WebDriver driver, WebElement element, String parmeter1, String parmeter2) {
        if (null == element) {
            if (null == parmeter2) {
                for (int i = 0; i < 20; i++) {
                    try {
                        if (null != new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(parmeter1)))) {
                            return true;
                        }
                    } catch (Exception e) {
                        LOGGER.info(parmeter1 + parmeter2 + "元素 定位失败");
                    }
                }
                LOGGER.error("元素不存在！");
                return false;
            }
            for (int i = 0; i < 20; i++) {
                try {
                    driver.findElement(By.cssSelector(parmeter1)).findElement(By.cssSelector(parmeter2)).getText();
                    return true;
                } catch (Exception e) {
                    LOGGER.info(parmeter1 + parmeter2 + "等待元素加载……");
                }
            }
            LOGGER.error("元素不存在！");
            return false;

        } else {
            if (null == driver) {
                if (null == parmeter2) {

                    for (int i = 0; i < 20; i++) {
                        try {
                            element.findElement(By.cssSelector(parmeter1)).getText();
                            return true;
                        } catch (Exception e) {
                            LOGGER.info(parmeter1 + parmeter2 + "等待元素加载……");
                        }
                        LOGGER.error("元素不存在！");
                        return false;
                    }
                }

                for (int i = 0; i < 20; i++) {
                    try {
                        element.findElement(By.cssSelector(parmeter1)).findElement(By.cssSelector(parmeter2)).getText();
                        return true;
                    } catch (Exception e) {
                        LOGGER.info(parmeter1 + parmeter2 + "等待元素加载……");
                    }
                    LOGGER.error("元素不存在！");
                    return false;
                }
            }
        }
        return false;
    }

    // 检测 WebElements
    public static boolean WebElementsExist(WebDriver driver, WebElement element, String parmeter1, String parmeter2) {
        Auxiliary.waitTime();
        if (null == element) {
            if (null == parmeter2) {

                for (int i = 0; i < 20; i++) {
                    try {
                        driver.findElement(By.cssSelector(parmeter1));
                        return true;
                    } catch (Exception e) {
                        LOGGER.info(parmeter1 + parmeter2 + "等待元素加载……");
                    }
                    LOGGER.error("元素不存在！");
                    return false;
                }

            }
            for (int i = 0; i < 20; i++) {
                try {
                    driver.findElement(By.cssSelector(parmeter1)).findElements(By.cssSelector(parmeter2));
                    return true;
                } catch (Exception e) {
                    LOGGER.info(parmeter1 + parmeter2 + "等待元素加载……");
                }
                LOGGER.error("元素不存在！");
                return false;
            }
        }
        return false;
    }


    // 检测 elements
    public static boolean elementExist(Document document, Element element, String parmeter1, String parmeter2) {
        Auxiliary.waitTime();
        if (null == document) {
            if (null == parmeter2) {
                try {
                    element.select(parmeter1).text();
                    return true;
                } catch (Exception e) {
                    LOGGER.info("等待元素加载……");
                    return false;
                }
            }
            try {
                element.select(parmeter1).select(parmeter2).text();
                return true;
            } catch (Exception e) {
                LOGGER.info("等待元素加载……");
                return false;
            }
        }

        if (null == element) {
            if (null == parmeter2) {
                try {
                    document.select(parmeter1).text();
                    return true;
                } catch (Exception e) {
                    LOGGER.info("等待元素加载……");
                    return false;
                }
            }
            try {
                document.select(parmeter1).select(parmeter2).text();
                return true;
            } catch (Exception e) {
                LOGGER.info("等待元素加载……");
                return false;
            }
        }
        return false;
    }
}
