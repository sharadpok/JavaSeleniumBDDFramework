package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Minimal thread-safe BaseTest: keeps driver in a ThreadLocal so it is safe for parallel runs
 * but usable in single-thread runs as well.
 *
 * Usage:
 *   BaseTest.initDriver();   // in @Before hook
 *   WebDriver driver = BaseTest.getDriver();
 *   BaseTest.quitDriver();   // in @After hook
 */
public class BaseTest {

    // ThreadLocal driver so each test thread gets its own WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Initialize driver for current thread (idempotent)
    public static void initDriver() {
        if (driverThreadLocal.get() == null) {
            WebDriverManager.chromedriver().setup();
            WebDriver driver = new ChromeDriver();
            driver.manage().window().maximize();
            driverThreadLocal.set(driver);
        }
    }

    // Return the driver for the current thread (may be null if not initialized)
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    // Quit and remove driver for the current thread
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            // If you have a logger, use it. For now print the error.
            System.err.println("Error quitting driver: " + e.getMessage());
        } finally {
            // Important: remove the ThreadLocal reference to avoid memory leaks
            driverThreadLocal.remove();
        }
    }

    // Optional helper to check if driver is initialized
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
}
