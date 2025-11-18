package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {

    // Keep it private so only BaseTest controls lifecycle
    private static WebDriver driver;

    public static void initDriver() {
        // safe init: only create if null
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            // add timeouts or desired capabilities if needed
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void quitDriver() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            // ensure we clear reference so next scenario starts fresh
            driver = null;
        }
    }
}
