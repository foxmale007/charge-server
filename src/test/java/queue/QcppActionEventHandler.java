package queue;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.WorkHandler;

import eu.chargetime.ocpp.test.JSONTestClient;

public class QcppActionEventHandler implements WorkHandler<QcppActionEvent>{

	private Logger logger = LogManager.getLogger(this.getClass());

	private JSONTestClient client;
	
	public QcppActionEventHandler(JSONTestClient client) {
		this.client = client;
	}
	
	@Override
	public void onEvent(QcppActionEvent event) throws Exception {
		OcppAction ocppAction = event.getValue();
		TimeUnit.MILLISECONDS.sleep(500); //所有指令间隔0.5秒执行
		client.send(ocppAction.getRequest()).whenComplete((confirmation, throwable) -> {
			logger.info(confirmation.toString());
			ocppAction.getConfirmationProcessor().process(confirmation);
			if(throwable != null) {
				throwable.printStackTrace();
			}
		});
	}
}
