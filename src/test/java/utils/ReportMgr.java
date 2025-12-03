package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ReportMgr {

    // ReportMgr - manages ExtentReports instance and ThreadLocal ExtentTest for parallel execution.

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ReportMgr() {
        // prevent instantiation
    }
    /** Initialize once in @BeforeSuite or first scenario before creating tests */
    public static void init(ExtentReports ext) {
        extent = ext;
    }
    /** Get global ExtentReports instance */
    public static ExtentReports getExtent() {
        return extent;
    }
    /** Set ExtentTest for the current thread */
    public static void setTest(ExtentTest t) {
        testThreadLocal.set(t);
    }
    /** Get ExtentTest associated with current thread */
    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }
    /** Clean up thread-local */
    public static void removeTest() {
        testThreadLocal.remove();
    }
}

