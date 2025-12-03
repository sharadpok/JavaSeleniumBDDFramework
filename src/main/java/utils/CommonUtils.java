package utils;

import org.openqa.selenium.By;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * CORE utility file for:
 *  - property loading
 *  - testdata loading
 *  - locator parsing
 *  - returning Selenium By
 *
 * NOTE: This file should NOT contain reporting/logging/screenshot code.
 */
public class CommonUtils {

    private static final ConcurrentMap<String, Properties> PROPS_CACHE = new ConcurrentHashMap<>();

    // ---------------- PROPERTY LOADING ----------------

    public static Properties loadPropertiesFileFromClasspath(String resourcePath) throws IOException {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            throw new IllegalArgumentException("resourcePath must not be null/empty");
        }

        Properties cached = PROPS_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Properties resource not found: " + resourcePath);
            }
            Properties props = new Properties();
            props.load(is);
            PROPS_CACHE.putIfAbsent(resourcePath, props);
            return PROPS_CACHE.get(resourcePath);
        }
    }

    public static String readPropertyFromFile(String fileName, String key) {
        if (fileName == null || key == null) {
            throw new IllegalArgumentException("fileName and key must not be null");
        }
        final String resourcePath;
        switch (fileName.trim().toLowerCase(Locale.ROOT)) {
            case "global":
                resourcePath = "config/global.properties";
                break;
            case "object":
                resourcePath = "objects/object.properties";
                break;
            default:
                throw new IllegalArgumentException("Unknown properties alias: " + fileName);
        }

        try {
            Properties props = loadPropertiesFileFromClasspath(resourcePath);
            String val = props.getProperty(key);
            return val == null ? null : val.trim();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties: " + resourcePath, e);
        }
    }

    // ---------------- TEST DATA LOADING ----------------

    public static String getTestData(String module, String key) {
        if (module == null || key == null)
            throw new IllegalArgumentException("module/key cannot be null");

        String mod = module.toLowerCase(Locale.ROOT).trim();
        String path = "testdata/" + mod + "/testdata.properties";

        try {
            Properties p = loadPropertiesFileFromClasspath(path);
            if (!p.containsKey(key))
                throw new IllegalArgumentException("Missing key '" + key + "' in module: " + module);

            return p.getProperty(key).trim();

        } catch (IOException e) {
            throw new RuntimeException("Unable to load testdata: " + path, e);
        }
    }

    // ---------------- LOCATOR PARSING ----------------

    public static String[] readObjectPropertyFile(String locatorKey) {
        if (locatorKey == null || locatorKey.trim().isEmpty())
            throw new IllegalArgumentException("locatorKey cannot be null/empty");

        String locator = readPropertyFromFile("object", locatorKey);
        if (locator == null)
            throw new IllegalArgumentException("Invalid locator key: " + locatorKey);

        locator = locator.trim();
        int sep = locator.indexOf(":");

        if (sep <= 0)
            throw new IllegalArgumentException("Invalid locator format for: " + locatorKey);

        String type = locator.substring(0, sep).trim();
        String value = locator.substring(sep + 1).trim();

        return new String[]{type, value};
    }

    public static By returnByClass(String locatorType, String locatorValue) {
        switch (locatorType.toLowerCase(Locale.ROOT)) {
            case "xpath": return By.xpath(locatorValue);
            case "id": return By.id(locatorValue);
            case "name": return By.name(locatorValue);
            case "classname": return By.className(locatorValue);
            case "tagname": return By.tagName(locatorValue);
            case "linktext": return By.linkText(locatorValue);
            case "partiallinktext": return By.partialLinkText(locatorValue);
            case "css": return By.cssSelector(locatorValue);
            default:
                throw new IllegalArgumentException("Unsupported locator type: " + locatorType);
        }
    }

    public static By getLocator(String locatorKey) {
        String[] data = readObjectPropertyFile(locatorKey);
        return returnByClass(data[0], data[1]);
    }

    public static String getText(String locatorKey) {
        try {
            return BaseTest.getDriver().findElement(getLocator(locatorKey)).getText();
        } catch (Exception e) {
            throw new RuntimeException("Unable to get text for: " + locatorKey, e);
        }
    }

    public static void clearPropertiesCache() {
        PROPS_CACHE.clear();
    }

    public static String getCurrentDateTimeStamp() {
        SimpleDateFormat sdateFormat = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_aaa(zzz)");
        Date curdate = new Date();
        return sdateFormat.format(curdate);
    }

    public static String getReportNameWithCurrentDateTimeStamp() {
        return "AutomationReport_" + getCurrentDateTimeStamp().
                replaceAll("\\(IST\\)", "").
                replaceAll("\\s", "") + ".html";
    }

}
