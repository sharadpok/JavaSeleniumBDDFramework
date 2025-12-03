package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Screenshot utilities (WebDriver + Desktop).
 * Storage path comes from global.properties -> screenshotStorePath.
 */
public final class ScreenshotUtils {

    private ScreenshotUtils(){}

    // ------------ Generate timestamp ------------
    private static String timestamp() {
        return new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());
    }

    // ------------ Build screenshot folder using global.properties ------------

    private static File buildScreenshotFolder() {

        // Read from global.properties
        String basePath = CommonUtils.readPropertyFromFile("global", "screenshotStorePath");

        // If null/empty → fallback to project/target/screenshots_store
        if (basePath == null || basePath.trim().isEmpty()) {
            basePath = System.getProperty("user.dir") + File.separator + "target" + File.separator + "screenshots_store";
        }

        // Support placeholder "user.dir"
        else if (basePath.toLowerCase().contains("user.dir")) {
            basePath = System.getProperty("user.dir");
        }

        // Build final folder path
        String folder = basePath + File.separator + "AutomationReportScreenshots" + File.separator + timestamp() + "_" + Thread.currentThread().getId();

        // Create folders if missing
        File dir = new File(folder);
        if (!dir.exists()) dir.mkdirs();

        return dir;
    }

    // ============================================================
    // 1️⃣ Save WebDriver screenshot (BYTES → PNG)
    // ============================================================
    public static String captureWebDriverScreenshot(WebDriver driver) throws Exception {

        // SAFETY CHECK — if driver is null, simply return null.
        if (driver == null) {
            return null;   // logger() will automatically try desktop screenshot
        }

        // Build the folder first
        File dir = buildScreenshotFolder();

        // Create filename
        String fileName = "WebDriver_" + timestamp() + ".png";
        File out = new File(dir, fileName);

        // Direct screenshot capture (NO VALIDATION)
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Save screenshot file
        try (FileOutputStream fos = new FileOutputStream(out)) {
            fos.write(bytes);
        }

        return out.getAbsolutePath();
    }

    // ============================================================
    // 2️⃣ Capture full DESKTOP screenshot (Robot API)
    // ============================================================
    public static String captureDesktopScreenshot() throws Exception {

        File dir = buildScreenshotFolder();

        String fileName = "Desktop_" + timestamp() + ".png";
        File out = new File(dir, fileName);

        Robot r = new Robot();

        Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        BufferedImage img = r.createScreenCapture(rect);

        ImageIO.write(img, "png", out);

        return out.getAbsolutePath();
    }
}
