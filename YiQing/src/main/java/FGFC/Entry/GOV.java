package FGFC.Entry;

import FGFC.Utils.Auxiliary;
import FGFC.Utils.ElementExist;
import com.alibaba.fastjson.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static spider.patent.baiteng.util.Auxiliary.waitTime;

/**
 * @author liyujie
 * 中华人民共和国中央人民政府网
 * 政策-政府信息公开
 **/
public class GOV {
    private static final Logger LOGGER = LoggerFactory.getLogger(GOV.class);
    private static String FILELIST = "http://www.gov.cn/zhengce/xxgkzl.htm";

    public static void main(String[] args) {
        GOV gov = new GOV();
        gov.fileList();
    }

    private void fileList() {
        WebDriver driver = null;
        try {
            driver = Auxiliary.getDriver(FILELIST);
            if (null != driver) {
//                if (ElementExist.isElementExist(driver, ".dataBox")) {
                List<WebElement> fileList = driver.findElements(By.cssSelector(".info>a"));
                WebElement fileList2 = driver.findElement(By.className("dataBox"));
                if (null != fileList) {
                    for (WebElement category : fileList) {
                        try {
                            category.click();
                            waitTime();
                            patent(driver);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), "fileList click err");
                        }
                    }
                }
                try {
                    Auxiliary.closeDriver(driver);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), "categoryList close err");
                }
//                } else {
//                    LOGGER.error("categoryList null");
////                    fileList();
//                }
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
                if (ElementExist.isElementExist(driver, ".Js_outerList .u-list-div .c-blue.nl-an")) {
                    List<WebElement> patentList = driver.findElements(By.cssSelector(".Js_outerList .u-list-div .c-blue.nl-an"));
                    for (WebElement patent : patentList) {
                        try {
                            patent.click();
                            waitTime();
                            detail(driver);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), "patent click err");
                        }
                    }
                }
                try {
                    if (ElementExist.isclick(driver, ".paging-next.icon-paging")) {
                        WebElement nextClick = driver.findElement(By.cssSelector(".paging-next.icon-paging"));
                        nextClick.click();
                        waitTime();
                        patent(driver);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), "nextClick err");
                }
            }
            try {
                Auxiliary.closeWindows(driver, "categoryPage", "patentPage");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), "patentList close err");
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
                if (ElementExist.isElementExist(driver, ".g-mn1c.m-list")) {
                    info.put("patentName", driver.findElement(By.cssSelector(".title.Js_hl")).getText().trim());
                    info.put("abstract", driver.findElement(By.cssSelector(".abstract.contenttext")).getText().trim());
                    info.put("crawlerId", "21");
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
                }
            }
            try {
                Auxiliary.closeWindows(driver, "patentPage", "detailPage");
                Thread.sleep(100000);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), "detail 关闭失败");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
