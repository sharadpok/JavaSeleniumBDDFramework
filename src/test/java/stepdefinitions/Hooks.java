package stepdefinitions;

import utils.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks  {

    @Before
    public void setUp()
    {
        System.out.println("Launching browser before scenario...");
        BaseTest.initDriver();

    }

    @After
    public void tearDown()
    {
        System.out.println("Closing browser after scenario...");
        BaseTest.quitDriver();
    }


}
