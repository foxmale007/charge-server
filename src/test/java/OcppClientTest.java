import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.RandomUtils;
import org.ccframe.commons.util.JsonUtil;
import org.ccframe.commons.util.UtilDateTime;
import org.ccframe.config.GlobalEx;
import org.joda.time.LocalTime;
import org.junit.Test;

import com.alibaba.druid.support.json.JSONUtils;

import eu.chargetime.ocpp.ClientEvents;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.profile.ClientCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ClientCoreProfile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.AvailabilityStatus;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityConfirmation;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core.ClearCacheConfirmation;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.ClearCacheStatus;
import eu.chargetime.ocpp.model.core.ConfigurationStatus;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import eu.chargetime.ocpp.model.core.GetConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RemoteStartStopStatus;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetConfirmation;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetStatus;
import eu.chargetime.ocpp.model.core.SampledValue;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import eu.chargetime.ocpp.model.core.UnlockConnectorConfirmation;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.core.UnlockStatus;
import eu.chargetime.ocpp.model.core.ValueFormat;
import eu.chargetime.ocpp.test.JSONTestClient;
import queue.IConfirmationProcessor;
import queue.OcppAction;
import queue.OcppActionQueueHelper;

public class OcppClientTest {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	private static final Integer CONNECT_INDEX = 1; //设备端的充电头编号

	@Test
	public void doSend() throws OccurenceConstraintException, UnsupportedFeatureException, InterruptedException {
		OcppActionQueueHelper ocppActionQueueHelper = new OcppActionQueueHelper();
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");

		ClientCoreProfile coreProfile = new ClientCoreProfile(new ClientCoreEventHandler() {
			
			private Request receivedRequest;
			
            @Override
            public ChangeAvailabilityConfirmation handleChangeAvailabilityRequest(
                ChangeAvailabilityRequest request) {
              receivedRequest = request;
              return new ChangeAvailabilityConfirmation(AvailabilityStatus.Accepted);
            }

            @Override
            public GetConfigurationConfirmation handleGetConfigurationRequest(
                GetConfigurationRequest request) {
              receivedRequest = request;
              return new GetConfigurationConfirmation();
            }

            @Override
            public ChangeConfigurationConfirmation handleChangeConfigurationRequest(
                ChangeConfigurationRequest request) {
              receivedRequest = request;
              ChangeConfigurationConfirmation confirmation =
                  new ChangeConfigurationConfirmation();
              confirmation.setStatus(ConfigurationStatus.Accepted);
              return confirmation;
            }

            @Override
            public ClearCacheConfirmation handleClearCacheRequest(ClearCacheRequest request) {
              receivedRequest = request;
              ClearCacheConfirmation confirmation = new ClearCacheConfirmation();
              confirmation.setStatus(ClearCacheStatus.Accepted);
              return confirmation;
            }

            @Override
            public DataTransferConfirmation handleDataTransferRequest(
                DataTransferRequest request) {
              receivedRequest = request;
              DataTransferConfirmation confirmation = new DataTransferConfirmation();
              confirmation.setStatus(DataTransferStatus.Accepted);
              return confirmation;
            }

            @Override
            public RemoteStartTransactionConfirmation handleRemoteStartTransactionRequest(
                RemoteStartTransactionRequest request) {
            	final RemoteStartTransactionRequest receivedRequest = (RemoteStartTransactionRequest)request;
            	receivedRequest.getChargingProfile().getTransactionId();
              
				System.out.println("收到启动请求，发送授权");
            	//收到启动后执行授权
              	ocppActionQueueHelper.publishEvent(new OcppAction(
          			new AuthorizeRequest(request.getIdTag()),
      				new IConfirmationProcessor() {
						@Override
						public void process(Confirmation confirmation) {
							AuthorizeConfirmation authorizeConfirmation = (AuthorizeConfirmation)confirmation;
							if(authorizeConfirmation.getIdTagInfo().getStatus() == AuthorizationStatus.Accepted) { //如果授权接收，则执行启动并发送启动通知
								System.out.println("完成授权，启动充电");
								try {
									StartTransactionRequest startTransactionRequest = new StartTransactionRequest(
										CONNECT_INDEX,
										receivedRequest.getIdTag(), 
										0,
										UtilDateTime.convertDateToString(new Date(), GlobalEx.OCPP_DATE_FORMAT)
									);
									startTransactionRequest.setReservationId(0);
									ocppActionQueueHelper.publishEvent(new OcppAction(
										startTransactionRequest,
										new IConfirmationProcessor() {
											@Override
											public void process(Confirmation confirmation) {
												try {
													System.out.println("启动充电完成");
													StartTransactionConfirmation startTransactionConfirmation = (StartTransactionConfirmation)confirmation;
													if(startTransactionConfirmation.validate()) {
														System.out.println("----------- 传输电量 10 次 -----------");
														Date now = new Date();
														for(int i = 0; i < 10; i ++) {
															SampledValue[] sampledValues = new SampledValue[] {
																OcppClientTest.createSampleValue("0.00", "Energy.Active.Import.Register", null, "kWh"),
																OcppClientTest.createSampleValue("0.00", "Current.Export", "L1", "A"),
																OcppClientTest.createSampleValue("0.00", "Current.Export", "L2", "A"),
																OcppClientTest.createSampleValue("0.00", "Current.Export", "L3", "A"),
																OcppClientTest.createSampleValue(decimalFormat.format(RandomUtils.nextDouble(230, 231)), "Voltage", "L1", "V"),
																OcppClientTest.createSampleValue(decimalFormat.format(RandomUtils.nextDouble(230, 231)), "Voltage", "L2", "V"),
																OcppClientTest.createSampleValue(decimalFormat.format(RandomUtils.nextDouble(230, 231)), "Voltage", "L3", "V"),
																OcppClientTest.createSampleValue(decimalFormat.format(RandomUtils.nextDouble(33, 34)), "Temperature", null, "Celsius"),
															};
															
															MeterValue meterValue = new MeterValue(UtilDateTime.convertDateToString(UtilDateTime.addMilliseconds(now, 500 * i), GlobalEx.OCPP_DATE_FORMAT), sampledValues);
															
															MeterValuesRequest meterValuesRequest = new MeterValuesRequest(CONNECT_INDEX);
															meterValuesRequest.setTransactionId(startTransactionConfirmation.getTransactionId());
															meterValuesRequest.setMeterValue(new MeterValue[] {meterValue});
															ocppActionQueueHelper.publishEvent(new OcppAction(
																meterValuesRequest,
																new IConfirmationProcessor() {
																	@Override
																	public void process(Confirmation confirmation) {
																		System.out.println("数据发送成功");
																	}
																}
															));
														}
													}
												}catch(Exception e) {
													e.printStackTrace();
													throw e;
												}
											}
										}
									));
								}catch(Exception e) {
									e.printStackTrace();
									throw e;
								}

							}
						}
          			}
      			));
              	System.out.println("RemoteStartTransaction on " + request.getIdTag());

              	return new RemoteStartTransactionConfirmation(RemoteStartStopStatus.Accepted);
            }

            @Override
            public RemoteStopTransactionConfirmation handleRemoteStopTransactionRequest(RemoteStopTransactionRequest request) {
            	final RemoteStopTransactionRequest receivedRequest = (RemoteStopTransactionRequest)request;
              
				StopTransactionRequest stopTransactionRequest = new StopTransactionRequest(0, UtilDateTime.convertDateToString(new Date(), GlobalEx.OCPP_DATE_FORMAT), receivedRequest.getTransactionId());
				ocppActionQueueHelper.publishEvent(new OcppAction(
					stopTransactionRequest,
					new IConfirmationProcessor() {
						@Override
						public void process(Confirmation confirmation) {
							System.out.println("数据发送成功");
							
							//停止后，执行一次数据传输结账
							DataTransferRequest dataTransferRequest = new DataTransferRequest("TONLY");
							dataTransferRequest.setMessageId("currentrecord");
							String dataJson = "{\"id\":1,\"connectorId\":1,\"chargemode\":1,\"startengry\":0.00,\"endengry\":0.00,\"starttime\":\"2021-11-09 16:26:38\",\"endtime\":\"2021-11-09 16:27:08\",\"costenergy\":0.00,\"costmoney\":0.00,\"transactionId\":" + stopTransactionRequest.getTransactionId() + ",\"stopReason\":\"Stop 3\"}";
							Map<String, Object> dataMap = JsonUtil.buildNormalBinder().toBean(dataJson, Map.class);
							dataTransferRequest.setData(dataMap);
							ocppActionQueueHelper.publishEvent(new OcppAction(
								dataTransferRequest,
								new IConfirmationProcessor() {
									@Override
									public void process(Confirmation confirmation) {
										System.out.println("交易结账完成");
									}
								}
							));
						}
					}
				));
              
              return new RemoteStopTransactionConfirmation(RemoteStartStopStatus.Accepted);
            }

            @Override
            public ResetConfirmation handleResetRequest(ResetRequest request) {
              receivedRequest = request;
              return new ResetConfirmation(ResetStatus.Accepted);
            }

            @Override
            public UnlockConnectorConfirmation handleUnlockConnectorRequest(UnlockConnectorRequest request) {
              receivedRequest = request;
              return new UnlockConnectorConfirmation(UnlockStatus.Unlocked);
            }
			
		});
		JSONTestClient client = new JSONTestClient(coreProfile);
		client.connect("http://localhost:8081", new ClientEvents() {

			@Override
			public void connectionOpened() {
				System.out.println("conection opened.");
			}

			@Override
			public void connectionClosed() {
				System.out.println("conection closed.");
			}
		});
		ocppActionQueueHelper.setClient(client);
		
		BootNotificationRequest bootNotificationRequest = new BootNotificationRequest("TONLY", "TonlyCharger_48A");
		bootNotificationRequest.setChargePointSerialNumber("2103252121090001");
		bootNotificationRequest.setFirmwareVersion("AC48A_1PV100");

		ocppActionQueueHelper.publishEvent(new OcppAction(bootNotificationRequest, new IConfirmationProcessor() {
			@Override
			public void process(Confirmation confirmation) {

				StatusNotificationRequest statusNotificationRequest = new StatusNotificationRequest(1, ChargePointErrorCode.NoError, ChargePointStatus.Available); //空闲通知
				ocppActionQueueHelper.publishEvent(new OcppAction(statusNotificationRequest, new IConfirmationProcessor() {
					@Override
					public void process(Confirmation confirmation) {
						// TODO Auto-generated method stub
					}
				}));

				statusNotificationRequest = new StatusNotificationRequest(1, ChargePointErrorCode.NoError, ChargePointStatus.Preparing); //就绪通知
				ocppActionQueueHelper.publishEvent(new OcppAction(statusNotificationRequest, new IConfirmationProcessor() {
					@Override
					public void process(Confirmation confirmation) {
						// TODO Auto-generated method stub
					}
				}));

				HeartbeatRequest heartbeatRequest = new HeartbeatRequest(); //心跳
				ocppActionQueueHelper.publishEvent(new OcppAction(heartbeatRequest, new IConfirmationProcessor() {
					@Override
					public void process(Confirmation confirmation) {
						// TODO Auto-generated method stub
					}
				}));
			}
		}));
		
		ocppActionQueueHelper.init();
		countDownLatch.await();
		client.disconnect();
	}

	protected static SampledValue createSampleValue(String value, String measurand, String phase, String unit) {
		SampledValue sampledValue = new SampledValue(value);
		sampledValue.setContext("Sample.Periodic");
		sampledValue.setFormat(ValueFormat.Raw);
		sampledValue.setMeasurand(measurand);
		sampledValue.setLocation(null);
		if(phase != null) {
			sampledValue.setPhase(phase);
		}
		sampledValue.setUnit(unit);
		return sampledValue;
	}
	
}
