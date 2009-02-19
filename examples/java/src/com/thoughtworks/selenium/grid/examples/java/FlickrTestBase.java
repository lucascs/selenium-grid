package com.thoughtworks.selenium.grid.examples.java;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.closeSeleniumSession;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.startSeleniumSession;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;


/**
 * Base class for all tests in Selenium Grid Java examples.
 */
public class FlickrTestBase {

    public static final String TIMEOUT = "120000";

    @BeforeMethod(groups = {"default", "example"}, alwaysRun = true)
    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})
    protected void startSession(String seleniumHost, int seleniumPort, String browser, String webSite) throws Exception {
        startSeleniumSession(seleniumHost, seleniumPort, browser, webSite);
        session().setTimeout(TIMEOUT);
    }

    @AfterMethod(groups = {"default", "example"}, alwaysRun = true)
    protected void closeSession() throws Exception {
        closeSeleniumSession();
    }

    protected void runFlickrScenario(String searchString) {
        session().open("/");
        assertTrue(session().getLocation(), session().getLocation().startsWith("http://flickr.com"));
        session().type("q", searchString);
        session().click("//form[@action='/search/']//input[@type='submit']");
        session().waitForPageToLoad(TIMEOUT);
        session().click("link=Advanced Search");
        session().waitForPageToLoad(TIMEOUT);
        session().click("media_photos");
        session().click("//input[@value='SEARCH']");
        session().waitForPageToLoad(TIMEOUT);
        assertTrue(session().isTextPresent(searchString.split(" ")[0]));
    }

}
