import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.test.DummyHandlers;

public class OcppServerTest {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	@Test
	public void testServer() throws InterruptedException {
		DummyHandlers dummyHandlers = new DummyHandlers();
		ServerCoreEventHandler handler = dummyHandlers.createServerCoreEventHandler();
		ServerCoreProfile coreProfile = new ServerCoreProfile(handler);
		JSONServer jsonServer = new JSONServer(coreProfile);
		jsonServer.open("localhost", 8080, dummyHandlers.generateServerEventsHandler());
		countDownLatch.await();
	}
}
