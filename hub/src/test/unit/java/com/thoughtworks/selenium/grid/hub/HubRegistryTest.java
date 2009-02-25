package com.thoughtworks.selenium.grid.hub;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;


public class HubRegistryTest {

    @Test
    public void remoteControlPoolReturnsAValidPool() {
        assertNotNull(new HubRegistry().remoteControlPool());
    }

    @Test
    public void environmentManagerReturnsAUniqueInstance() {
        HubRegistry registry = new HubRegistry();
		assertSame(registry.environmentManager(), registry.environmentManager());
    }

    @Test
    public void gridConfigurationReturnsAUniqueInstance() {
    	HubRegistry registry = new HubRegistry();
        assertSame(registry.gridConfiguration(), registry.gridConfiguration());
    }

    @Test
    public void lifecyleManagerReturnsAValidManager() {
        assertNotNull(new HubRegistry().lifecycleManager());
    }

    @Test
    public void lifecyleManagerReturnsAUniqueInstance() {
        HubRegistry registry = new HubRegistry();
		assertSame(registry.lifecycleManager(), registry.lifecycleManager());
    }

}
