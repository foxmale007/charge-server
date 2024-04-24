package org.ccframe.commons.helper;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.ccframe.commons.ocpp.ChargeHandlers;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;

@Component
public class OCPPHelper implements InitializingBean{

	private JSONServer jsonServer;
	
	@Value("${app.ocpp.port:8081}")
	private int ocppPort;
	
	public CompletionStage<Confirmation> send(UUID session, Request request) throws OccurenceConstraintException, UnsupportedFeatureException, NotConnectedException{
		return jsonServer.send(session, request);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ChargeHandlers chargeHandlers = new ChargeHandlers();
		ServerCoreEventHandler handler = chargeHandlers.createServerCoreEventHandler();
		ServerCoreProfile coreProfile = new ServerCoreProfile(handler);
		jsonServer = new JSONServer(coreProfile);
		jsonServer.open("localhost", ocppPort, chargeHandlers.generateServerEventsHandler());
	}

}
