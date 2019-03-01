package simulated.landing;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Login1688 {

    public String click(String username, String password) throws InterruptedException {
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromeDriver.exe");
        //System.setProperty("webdriver.gecko.driver", "C:\\Java\\geckodriver.exe");
        //WebDriver webDriver = new FirefoxDriver();
        WebDriver webDriver = new ChromeDriver();
        //		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        String baseUrl = "https://login.1688.com/";
        webDriver.get(baseUrl + "/member/signin.htm?spm=0.0.0.0.ijAHe8&Done=https%3A%2F%2Fs.1688.com%2Fcompany%2Fcompany_search.htm%3Fkeywords%3D%25B7%25FE%25D7%25B0%26button_click%3Dtop%26n%3Dy%26sortType%3Dpop%26pageSize%3D30%26offset%3D3%26beginPage%3D1");
        // ERROR: Caught exception [ERROR: Unsupported command [selectFrame |  | ]]
        webDriver.switchTo().frame(0);
        webDriver.findElement(By.id("J_Quick2Static")).click();
        //休息5秒
        Thread.sleep(5000);
        webDriver.findElement(By.cssSelector("span.ph-label")).click();
        webDriver.findElement(By.id("TPL_username_1")).clear();
        webDriver.findElement(By.id("TPL_username_1")).sendKeys("");
        Thread.sleep(5000);
        webDriver.findElement(By.id("TPL_password_1")).clear();
        webDriver.findElement(By.id("TPL_password_1")).sendKeys("");
        webDriver.findElement(By.id("J_SubmitStatic")).click();
        //获取cookie，上面一跳出循环我认为就登录成功了，当然上面的判断不太严格，可以再进行修改
        Set<Cookie> cookies = webDriver.manage().getCookies();
        String cookieStr = "";
        for (Cookie cookie : cookies) {
            cookieStr += cookie.getName() + "=" + cookie.getValue() + "; ";
        }

        //退出，关闭浏览器，并返回cookie
//		webDriver.quit();
        return cookieStr;
    }
    //指定URL获取网页内容
    public String getHtml(String cookie,String url){
        try {
            Connection.Response orderResp = Jsoup.connect(url)
                    .header("authority", "company.1688.com")
//                    .header("Connection", "keep-alive")
//					.header("Cache-Control", "max-age=0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//					.header("Referer", "https://sec.1688.com/query.htm?smApp=searchweb2&smPolicy=searchweb2-company-anti_Spider-html-checkcode&smCharset=GBK&smTag=MTE0LjIxMy4yNTIuMjI2LDg2MDUzMTU4NywxNDk2NmY0ZjMwYTQ0YzJjOTViN2Q1ZWI2MTMwOWM3Mg%3D%3D&smReturn=https%3A%2F%2Fs.1688.com%2Fcompany%2Fcompany_search.htm%3Fkeywords%3D%25B7%25FE%25D7%25B0%26button_click%3Dtop%26n%3Dy%26sortType%3Dpop%26pageSize%3D30%26offset%3D3%26beginPage%3D1&smSign=Ga%2BGkbGodQJaa3wuJw1gyA%3D%3D")
                    .header("Upgrade-Insecure-Requests", "1")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36")
//                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .cookie("Cookie", cookie)
                    .execute();

            String doc = orderResp.parse().html();
            Thread.sleep(10000);
            System.out.println("休息10秒！！");
            return doc;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static <ShopinfoinputModel> void main(String[] args) throws InterruptedException, IOException, SQLException {
        Login1688 crawler = new Login1688();
        String cookie = crawler.click1("htylpc", "htyl@123");
        System.out.println(cookie);
        String initialurl="";
        String initialhtml = crawler.getHtml(cookie,initialurl);
        System.out.println(initialhtml);
        //获取页面总数
        int pagenumber=Integer.parseInt(Jsoup.parse(initialhtml).select("span[class=total-page]").text().replaceAll("", ""));
        //对每页内容循环，获取每页的信息
        List<ShopinfoinputModel> pagedata=new ArrayList<ShopinfoinputModel>();
        for (int i = 100; i < pagenumber; i++) {
            System.out.println("一共"+pagenumber+"页"+",当前爬的页面为第"+i+"页！");
            //拼接要爬的每页的url
            String everypageurl="https://s.1688.com/company/company_search.htm?keywords=%B7%FE%D7%B0&button_click=top&n=y&sortType=pop&pageSize=30&offset=3&beginPage="+i;
            //请求每页URL
            String everypagehtml = crawler.getHtml(cookie,everypageurl);
            //解析每页内容
            pagedata=ShopInfoParse.getFirstInfo(everypagehtml);
            MYSQLControl.executeInsert(pagedata);
            //为防止频繁请求，这里设置休息时间
            int sleeprandomtime=10+(int)(Math.random()*20);
            Thread.sleep(10000+sleeprandomtime*1000);
        }

    }
    //模拟登陆程序
    public String click1(String username, String password) throws InterruptedException {
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromeDriver.exe");
        //System.setProperty("webdriver.gecko.driver", "C:\\Java\\geckodriver.exe");
       // WebDriver webDriver = new FirefoxDriver();
        WebDriver webDriver = new ChromeDriver();
        String baseUrl = "https://login.1688.com/";
        webDriver.get(baseUrl + "/member/signin.htm?from=sm&Done=https%3A%2F%2Fsec.1688.com%2Fquery.htm%3Faction%3DQueryAction%26event_submit_do_login%3Dok%26smApp%3Dkylin%26smPolicy%3Dkylin-contactinfo-anti_Spider-seo-html-checklogin%26smCharset%3DGBK%26smTag%3DMTE0LjIxMy4yNTIuMjgsLDAzYzMyMWY4OTU2ZTRmOWRhYmEzZDIwM2Q0YjBmOGFm%26smReturn%3Dhttps%253A%252F%252Fgzmiduo.1688.com%252Fpage%252Fcontactinfo.htm%253Fspm%253Da2615.2177701.0.0.35688ae8pf2YUK%26smSign%3DeReOoGQW6AgoCI9HKAYjuA%253D%253D");
        webDriver.switchTo().frame(0);
        //webDriver.findElement(By.id("J_Static2Quick")).click();
        WebElement element = webDriver.findElement(By.id("J_Quick2Static"));
        System.out.println(element);
        System.out.println(element.getTagName());
        element.click();
        //休息5秒
        Thread.sleep(5000);
        webDriver.findElement(By.cssSelector("span.ph-label")).click();
        webDriver.findElement(By.id("TPL_username_1")).clear();
            webDriver.findElement(By.id("TPL_username_1")).sendKeys(username);
            webDriver.findElement(By.id("TPL_password_1")).clear();
            webDriver.findElement(By.id("TPL_password_1")).sendKeys(password);
            webDriver.findElement(By.id("J_SubmitStatic")).click();
            Set<Cookie> cookies = webDriver.manage().getCookies();
            String cookieStr = "";
            for (Cookie cookie : cookies) {
                cookieStr += cookie.getName() + "=" + cookie.getValue() + "; ";
        }

        //退出，关闭浏览器，并返回cookie
//			webDriver.quit();
        return cookieStr;
    }
}
