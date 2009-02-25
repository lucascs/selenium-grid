package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.File;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;

public class BoxPoolTest extends UsingClassMock {

	private BoxPool boxPool;
	private Mock remoteControlPool;
	@Before
	public void setUp() {
		remoteControlPool = mock(DynamicRemoteControlPool.class);
		boxPool = new BoxPool((DynamicRemoteControlPool) remoteControlPool);
	}
	@Test
	public void testRegisteringABoxStoresTheBox() throws Exception {
		Box box = new Box("localhost", 3222);
		boxPool.register(box);
		
		ensureThat(boxPool.getBoxes().contains(box));
	}
	@Test
	public void testLoadingABoxReturnsTheStoredBox() throws Exception {
		Box box = new Box("localhost", 3222);
		boxPool.register(box);
		
		Box loaded = boxPool.load(new Box("localhost", 3222));
		
		ensureThat(loaded, sameInstanceAs(box));
	}
	@Test
	public void testUnregisteringABoxSetsTheBoxDownAndUnStoresIt() throws Exception {
		Mock box = mock(Box.class);
		box.expects("host").will(returnValue("localhost"));
		box.expects("port").will(returnValue(1234));
		box.expects("setDown").with(sameInstanceAs(remoteControlPool));
		
		boxPool.register((Box) box);
		
		boxPool.unregister(new Box("localhost", 1234));
		
		ensureThat(!boxPool.getBoxes().contains(box));
	}
	@Test
	public void testStoreAndLoadFromAFileReturnsEqualsBoxes() throws Exception {
		boxPool.register(new Box("localhost", 2345));
		
		File file = File.createTempFile("test", "xml");
		
		boxPool.saveTo(file);
		
		BoxPool pool = new BoxPool((DynamicRemoteControlPool) remoteControlPool);
		pool.loadFrom(file);
		ensureThat(pool.getBoxes().size(), eq(1));
		ensureThat(pool.getBoxes().get(0).host(), eq("localhost"));
		ensureThat(pool.getBoxes().get(0).port(), eq(2345));
	}
}
