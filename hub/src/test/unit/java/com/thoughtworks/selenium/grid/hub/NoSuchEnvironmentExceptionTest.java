package com.thoughtworks.selenium.grid.hub;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;


public class NoSuchEnvironmentExceptionTest {

    @Test
    public void environmentReturnsTheOneProvidedInTheConstructor() {
      assertEquals("*safari", new NoSuchEnvironmentException("*safari").environment());
    }

    @Test
    public void messageIsExplicit() {
      assertEquals("com.thoughtworks.selenium.grid.hub.NoSuchEnvironmentException: Could not find any remote control providing the '*safari' environment. " +
                   "Please make sure you started some remote controls which registered as offering " +
                   "this environment.", new NoSuchEnvironmentException("*safari").toString());
    }

}
