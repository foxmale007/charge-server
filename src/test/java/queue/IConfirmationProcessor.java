package queue;

import eu.chargetime.ocpp.model.Confirmation;

public interface IConfirmationProcessor {
	public void process(Confirmation confirmation);
}
