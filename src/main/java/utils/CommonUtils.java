package utils;

import org.openqa.selenium.By;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utilities for loading properties (classpath) and returning locators/test data.
 * Small, thread-safe cache of loaded Properties files.
 */
public class CommonUtils {

    private static final ConcurrentMap<String, Properties> PROPS_CACHE = new ConcurrentHashMap<>();

    public static Properties loadPropertiesFileFromClasspath(String resourcePath) throws IOException {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            throw new IllegalArgumentException("resourcePath must not be null/empty");
        }

        // fast-path from cache
        Properties cached = PROPS_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        // load resource from classpath
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Properties resource not found on classpath: " + resourcePath);
            }
            Properties props = new Properties();
            props.load(is);

            // cache (if another thread won the race, keep their instance)
            PROPS_CACHE.putIfAbsent(resourcePath, props);
            return PROPS_CACHE.get(resourcePath);
        }
    }

    /**
     * Read a single property from an aliased properties file.
     * Supported aliases: "global" -> config/global.properties, "object" -> objects/object.properties
     *
     * @param fileName alias name
     * @param key       property key
     * @return trimmed property value or null if key not present
     */

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
                throw new IllegalArgumentException("Unknown properties file alias: " + fileName);
        }

        try {
            Properties props = loadPropertiesFileFromClasspath(resourcePath);
            String val = props.getProperty(key);
            return val == null ? null : val.trim();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from resource: " + resourcePath, e);
        }
    }

    /**
     * Module-specific test data getter.
     * Example: getTestData("login", "username") loads testdata/login/testdata.properties.
     *
     * @param module module folder name under testdata (case-insensitive)
     * @param key    property key inside module's testdata file
     * @return trimmed value
     */

    public static String getTestData(String module, String key) {
        if (module == null || key == null) {
            throw new IllegalArgumentException("module and key cannot be null");
        }

        String mod = module.trim().toLowerCase(Locale.ROOT);
        String resourcePath = "testdata/" + mod + "/testdata.properties";

        try {
            Properties props = loadPropertiesFileFromClasspath(resourcePath);
            if (!props.containsKey(key)) {
                throw new IllegalArgumentException("Missing test data key '" + key + "' in module '" + module + "'");
            }
            String val = props.getProperty(key);
            return val == null ? null : val.trim();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load test data for module '" + module + "' from " + resourcePath, e);
        }
    }

    /**
     * Read locator entry from object.properties and split into {type, value}
     * Locator format expected: "type:value" (e.g. xpath://div[@id='x'])
     *
     * @param locatorKey key in object.properties
     * @return [0]=type, [1]=value
     */
    public static String[] readObjectPropertyFile(String locatorKey) {
        if (locatorKey == null || locatorKey.trim().isEmpty()) {
            throw new IllegalArgumentException("locatorKey must not be null/empty");
        }

        String locatorValue = readPropertyFromFile("object", locatorKey);
        if (locatorValue == null) {
            throw new IllegalArgumentException("No locator found for key: '" + locatorKey + "' in object.properties");
        }
        locatorValue = locatorValue.trim();

        int sep = locatorValue.indexOf(':');
        if (sep <= 0) {
            throw new IllegalArgumentException("Invalid locator format for key '" + locatorKey + "'. Expected 'type:value' but found: '" + locatorValue + "'");
        }

        String type = locatorValue.substring(0, sep).trim();
        String value = locatorValue.substring(sep + 1).trim();

        if (type.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("Locator type or value empty for key: " + locatorKey);
        }

        return new String[]{type, value};
    }

    /**
     * Convert locator type+value into Selenium By instance.
     */
    public static By returnByClass(String locatorType, String locatorValue) {
        if (locatorType == null || locatorType.trim().isEmpty()) {
            throw new IllegalArgumentException("Locator type cannot be null or empty");
        }
        if (locatorValue == null || locatorValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Locator value cannot be null or empty");
        }

        String type = locatorType.trim().toLowerCase(Locale.ROOT);
        switch (type) {
            case "xpath": return By.xpath(locatorValue);
            case "id": return By.id(locatorValue);
            case "name": return By.name(locatorValue);
            case "classname":
            case "class": return By.className(locatorValue);
            case "tagname":
            case "tag": return By.tagName(locatorValue);
            case "linktext":
            case "link": return By.linkText(locatorValue);
            case "partiallinktext":
            case "partiallink": return By.partialLinkText(locatorValue);
            case "css":
            case "cssselector": return By.cssSelector(locatorValue);
            default:
                throw new IllegalArgumentException("Unsupported locator type: '" + locatorType + "'. Allowed types: xpath, id, name, classname, tagname, linktext, partiallinktext, css");
        }
    }

    /**
     * Public helper: returns By for locator key from object.properties
     */
    public static By getLocator(String locatorKey) {
        try {
            String[] data = readObjectPropertyFile(locatorKey);
            return returnByClass(data[0], data[1]);
        } catch (RuntimeException e) {
            // rethrow with context
            throw new RuntimeException("Failed to build locator for key: " + locatorKey + " - " + e.getMessage(), e);
        }
    }

    /**
     * Convenience: read visible text from element by locator key (object.properties).
     * This method interacts with BaseTest.getDriver() — ensure driver is initialized before calling.
     */
    public static String getText(String locatorKey) {
        try {
            return BaseTest.getDriver().findElement(getLocator(locatorKey)).getText();
        } catch (Exception e) {
            throw new RuntimeException("Unable to get text for locator '" + locatorKey + "': " + e.getMessage(), e);
        }
    }

    /**
     * Clear cache (useful during development if you edit properties)
     */
    public static void clearPropertiesCache() {
        PROPS_CACHE.clear();
    }
}
