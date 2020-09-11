import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LinkFollowerTest {
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    @DataProvider(name = "urls", parallel = true)
    public Object[][] browsersProvider() {
        return new Object[][]{
                new Object[]{"https://www.selenium.dev/", BrowserType.FIREFOX},
                new Object[]{"https://www.seleniumconf.com/", BrowserType.CHROME},
                // new Object[]{"https://www.google.com/", BrowserType.OPERA}
        };
    }

    @BeforeMethod(alwaysRun = true)
    public void setupWebDriver(Method testMethod, Object[] testArgs) throws MalformedURLException {
        String browserName = (String) testArgs[1];
        MutableCapabilities caps;
        switch (browserName) {
            case BrowserType.FIREFOX:
                caps = new FirefoxOptions();
                break;
            // case BrowserType.OPERA:
            //     caps = new OperaOptions();
            //     break;
            default:
                caps = new ChromeOptions();
        }
        String testName = String.format("%s - %s", testArgs[0].toString(), browserName);
        System.out.println(String.format("Test name: %s", testName));
        URL gridUrl = new URL("http://selenium-hub:4444");
        webDriver.set(new RemoteWebDriver(gridUrl, caps));
    }

    private WebDriver getWebDriver(){
        return webDriver.get();
    }

    @Test(dataProvider = "urls")
    public void justFollowingLinksTest(String url, String browserName) throws InterruptedException {
        int pagesToNavigate = 3;
        int navigatedPages = 0;
        List<String> visitedUrls = new ArrayList<>();
        getWebDriver().get(url);
        visitedUrls.add(url);
        while (navigatedPages < pagesToNavigate) {
            navigatedPages++;
            // Just to have a couple of seconds to see the page
            Thread.sleep(5000);
            List<WebElement> elements = getWebDriver().findElements(By.cssSelector("a[href]"));
            for (WebElement element : elements) {
                String href = element.getAttribute("href");
                System.out.println(href);
                if (!href.contains("#") && !href.equals(getWebDriver().getCurrentUrl()) &&
                        href.startsWith("http") && !visitedUrls.contains(href)) {
                    visitedUrls.add(href);
                    System.out.println(String.format("%s - Browser: %s, Visiting url: %s",
                            Thread.currentThread().getId(), browserName, href));
                    getWebDriver().get(href);
                    break;
                }
            }

        }
    }

    @AfterMethod(alwaysRun = true)
    public void quitDriver() {
        getWebDriver().quit();
    }

}
