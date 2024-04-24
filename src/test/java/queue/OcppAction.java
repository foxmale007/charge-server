package queue;

import eu.chargetime.ocpp.model.Request;

public class OcppAction {
	
	private Request request;
	
	private IConfirmationProcessor confirmationProcessor;

	public OcppAction(Request request, IConfirmationProcessor confirmationProcessor) {
		super();
		this.request = request;
		this.confirmationProcessor = confirmationProcessor;
	}

	public IConfirmationProcessor getConfirmationProcessor() {
		return confirmationProcessor;
	}

	public void setConfirmationProcessor(IConfirmationProcessor confirmationProcessor) {
		this.confirmationProcessor = confirmationProcessor;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
}