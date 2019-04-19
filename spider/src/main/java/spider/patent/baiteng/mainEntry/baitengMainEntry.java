package spider.patent.baiteng.mainEntry;

import com.alibaba.fastjson.JSONObject;
import mysql.updateToMySQL;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spider.patent.baiteng.util.Auxiliary;
import spider.patent.baiteng.util.ElementExist;
import util.IConfigManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static spider.patent.baiteng.util.Auxiliary.waitTime;

/**
 * @author liyujie
 **/
public class baitengMainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(baitengMainEntry.class);
    private static String categoryListUrl = "https://www.baiten.cn/stdmode/locarno.html";
    SimpleDateFormat createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static java.util.Map<String, String> Map;

    public static void main(String[] args) {
        System.setProperty(IConfigManager.DEFUALT_CONFIG_PROPERTY, "172.17.60.213:2181");
        baitengMainEntry baitengMainEntry = new baitengMainEntry();
        baitengMainEntry.category();
    }

    private void category() {
        WebDriver driver = null;
        try {
            driver = Auxiliary.getDriver(categoryListUrl);
            if (null != driver) {
                if (!Auxiliary.setHandle(driver)) {
                    LOGGER.error("category setHandle err");
                }
                // 专利分类列表是否加载出来
                if (ElementExist.WebElementExist(driver, null, ".m-std-fields", null)) {
                    List<WebElement> categoryList = driver.findElements(By.cssSelector(".m-std-fields .m-std-item"));
                    if (null != categoryList) {
                        for (WebElement category : categoryList) {
                            try {
                                if (ElementExist.WebElementExist(driver, null, ".ui-button.ui-button-lblue.btn-loading.item-button.Js_major_search_btn", null)) {
                                    category.findElement(By.cssSelector(".ui-button.ui-button-lblue.btn-loading.item-button.Js_major_search_btn")).click();
                                    waitTime();
                                    patent(driver);
                                } else {
                                    LOGGER.error("categoryList click err");
                                }
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                    }
                    try {
                        Auxiliary.closeDriver(driver);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), "categoryList 关闭失败");
                    }
                } else {
                    LOGGER.error("分类列表 null");
                }
            } else {
                LOGGER.error("driver null");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                Auxiliary.closeDriver(driver);
                System.exit(0);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void patent(WebDriver driver) {
        try {
            // 切换窗口至专利列表
            if (!Auxiliary.setHandle(driver)) {
                LOGGER.error("patent setHandle err");
            }
            try {
                driver = Auxiliary.getHandleDriver(driver, "patentPage");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            if (null != driver) {
                if (ElementExist.WebElementExist(driver, null, ".Js_outerList .u-list-div", null)) {
                    List<WebElement> patentList = driver.findElements(By.cssSelector(".Js_outerList .u-list-div"));
                    for (WebElement patent : patentList) {
                        try {
                            if (ElementExist.WebElementExist(driver, null, ".c-blue.nl-an", null)) {
                                patent.findElement(By.cssSelector(".c-blue.nl-an")).click();
                                waitTime();
                                detail(driver);
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), "patent click err");
                        }
                    }
                }
                if (ElementExist.isclick(driver, ".paging-next.icon-paging")) {
                    driver.findElement(By.cssSelector(".paging-next.icon-paging")).click();
                    waitTime();
                    patent(driver);
                }
            }
            try {
                Auxiliary.closeWindows(driver, "categoryPage", "patentPage");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), "patentList 关闭失败");
            }
        } catch (Exception e1) {
            LOGGER.error(e1.getMessage());
        }
    }

    private void detail(WebDriver driver) {
        try {
            JSONObject info = new JSONObject();
            if (!Auxiliary.setHandle(driver)) {
                LOGGER.error("detail setHandle err");
            }
            driver = Auxiliary.getHandleDriver(driver, "detailPage");
            if (null != driver) {
                if (ElementExist.WebElementExist(driver, null, ".g-mn1c.m-list", null)) {
                    info.put("patentName", driver.findElement(By.cssSelector(".title.Js_hl")).getText().trim());
                    info.put("abstract", driver.findElement(By.cssSelector(".abstract.contenttext")).getText().trim());
                    info.put("crawlerId", "21");
                    info.put("createTime", createTime.format(new Date()));
                    List<WebElement> detailList = driver.findElements(By.cssSelector(".abst-info.fn-clear li"));
                    if (null != detailList) {
                        for (WebElement e : detailList) {
                            String key = e.findElement(By.cssSelector("label")).getText().trim();
                            switch (key) {
                                case "申请号":
                                    info.put("applicationNumber", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "申请日":
                                    info.put("applicationDate", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "公开号":
                                    info.put("publicNumber", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "授权公告日":
                                    info.put("publicDate", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "申请（专利权）人":
                                    info.put("applicant", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "发明人":
                                    info.put("inventor", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "主分类号":
                                    info.put("mainClassificationNumber", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "地址":
                                    info.put("address", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "国省代码":
                                    info.put("nationalCode", e.getText().trim().split(key, 2)[1]);
                                    break;
                                case "分类号":
                                    info.put("classificationNumber", e.getText().trim().split(key, 2)[1]);
                                    break;
                                default:
                            }
                        }
                    } else {
                        LOGGER.error("detailList null");
                    }
                    insert(info);
                }
            }
            try {
                Auxiliary.closeWindows(driver, "patentPage", "detailPage");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), "detail 关闭失败");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void insert(JSONObject info) {
        try {
            Map = (java.util.Map) info;
            if (updateToMySQL.baitengInsert(Map)) {
                LOGGER.info("插入中 : " + Map.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
