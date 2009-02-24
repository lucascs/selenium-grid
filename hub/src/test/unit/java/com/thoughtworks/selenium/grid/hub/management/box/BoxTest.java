package com.thoughtworks.selenium.grid.hub.management.box;

import java.util.Arrays;
import java.util.List;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Matcher;
import org.jbehave.core.mock.Mock;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.Hub;
import com.thoughtworks.selenium.grid.hub.management.box.Box.Status;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

public class BoxTest extends UsingClassMock {

	private Mock client;
	private Box box;
	@Before
	public void setUp() {
		client = mock(HttpClient.class);
		box = new Box("localhost", 5000, (HttpClient) client);
		box.setDefaultHubUrl("anyUrl");
		ensureThat(box.client(), sameInstanceAs(client));
	}
	@Test
	public void testThatSetUpStartsRemoteControlsWhenBoxIsOffLine() throws Exception {

		ensureThat(box.status(), is(Status.OFFLINE));
		
		Mock response = mock(Response.class);
		response.expects("statusCode").will(returnValue(200));
		
		client.expects("get").with(contains("localhost:5000")).will(returnValue(response));
		
		box.setUp();
		
		ensureThat(box.status(), is(Status.ONLINE));
		ensureThat(!box.getRcPorts().isEmpty());
		
		verifyMocks();
	}
	@Test
	public void testThatSetUpDoesNotStartRemoteControlsWhenBoxIsOnLine() throws Exception {
		box.setStatus(Status.ONLINE);
		
		client.expects("get").never();
		
		box.setUp();
		
		ensureThat(box.status(), is(Status.ONLINE));
		ensureThat(box.getRcPorts().isEmpty());
		
		verifyMocks();
	}
	@Test
	public void testThatSetDownWillRemoveAllRemotesFromThisBox() throws Exception {
		
		Mock remoteControlPool = mock(DynamicRemoteControlPool.class);
		
		Mock available = mock(RemoteControlProxy.class, "available");
		Mock reserved = mock(RemoteControlProxy.class, "reserved");
		
		remoteControlPool.expects("availableRemoteControls").will(returnValue(Arrays.asList(available)));
		remoteControlPool.expects("reservedRemoteControls").will(returnValue(Arrays.asList(reserved)));
		
		box.getRcPorts().add(1234);
		box.getRcPorts().add(5678);

		available.expects("host").times(2).will(returnValue("localhost"));
		available.expects("port").times(2).will(returnValue(1234));

		reserved.expects("host").times(2).will(returnValue("localhost"));
		reserved.expects("port").times(2).will(returnValue(5678));
		
		remoteControlPool.expects("unregister").with(sameInstanceAs(available)).will(returnValue(true));
		remoteControlPool.expects("unregister").with(sameInstanceAs(reserved)).will(returnValue(true));
		
		box.setDown((DynamicRemoteControlPool) remoteControlPool);
		
		verifyMocks();
	}
	@Test
	public void testThatStartRemoteControlsWillCallAnExpectedUrlAndStoreRCPorts() throws Exception {

		Mock response = mock(Response.class);
		client.expects("get").with(all(contains("localhost:5000/remote-control-manager/start"), contains("quantity=3"),
				contains("host=localhost"), contains("environment=abc"), contains("portStart=1000"),
				contains("hubURL=hubUrl")))
			.will(returnValue(response));
		
		response.expects("statusCode").will(returnValue(200));
		
		box.startRemoteControls(3, "abc", 1000, new Hub("hubUrl"));
		
		ensureThat(box.getRcPorts(), containsAll(1000, 1001, 1002));
		
		verifyMocks();
	}
	
	private Matcher containsAll(final Object... objects) {
		return new CustomMatcher("contains all " + Arrays.toString(objects)) {
			public boolean matches(Object arg) {
				List<Object> list = (List<Object>) arg;
				for (Object object : objects) {
					if (!list.contains(object)) {
						return false;
					}
				}
				return true;
			}
		};
	}
	private Matcher all(Matcher... matchers) {
		if (matchers.length == 1) {
			return matchers[0];
		}
		Matcher both = both(matchers[0],matchers[1]);
		for (int i = 2; i < matchers.length; i++) {
			both = both(both, matchers[i]);
		}
		return both;
	}
}
