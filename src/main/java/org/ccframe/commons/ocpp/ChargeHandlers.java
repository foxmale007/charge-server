package org.ccframe.commons.ocpp;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ccframe.commons.helper.ChargeMQTTHelper;
import org.ccframe.commons.helper.SpringContextHelper;
import org.ccframe.commons.util.JsonUtil;
import org.ccframe.commons.util.UtilDateTime;
import org.ccframe.config.GlobalEx;
import org.ccframe.subsys.charge.domain.code.ChargeTransactionStatusCodeEnum;
import org.ccframe.subsys.charge.domain.entity.ChargePointEquipment;
import org.ccframe.subsys.charge.domain.entity.ChargePointNotice;
import org.ccframe.subsys.charge.domain.entity.ChargePointTransaction;
import org.ccframe.subsys.charge.mqtt.ChargeMessage;
import org.ccframe.subsys.charge.service.ChargePointEquipmentSearchService;
import org.ccframe.subsys.charge.service.ChargePointEquipmentService;
import org.ccframe.subsys.charge.service.ChargePointNoticeService;
import org.ccframe.subsys.charge.service.ChargePointTransactionSearchService;
import org.ccframe.subsys.charge.service.ChargePointTransactionService;
import org.ccframe.subsys.core.domain.entity.User;
import org.ccframe.subsys.core.service.UserSearchService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Sort.Order;

import com.fasterxml.jackson.databind.node.TextNode;

import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementEventHandler;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RegistrationStatus;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;

public class ChargeHandlers {

	private boolean riggedToFail;

	private Confirmation receivedConfirmation;

	private String currentIdentifier;
	private UUID currentSessionIndex;

	private Logger log = LogManager.getLogger(getClass());
	
	public ServerCoreEventHandler createServerCoreEventHandler() {

		return new ServerCoreEventHandler() {
			@Override
			public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) { //授权
//				AuthorizeRequest receivedRequest = (AuthorizeRequest)request; //receivedRequest.getIdTag() 一定存在，所以不管
				
				IdTagInfo tagInfo = new IdTagInfo(AuthorizationStatus.Accepted);
				tagInfo.setExpiryDate(UtilDateTime.convertDateToString(UtilDateTime.addYears(new Date(), 1), GlobalEx.OCPP_DATE_FORMAT));
				return failurePoint(new AuthorizeConfirmation(tagInfo));
			}

			@Override
			public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex, BootNotificationRequest request) { //设备启动，注册
//				Request receivedRequest = request;
				BootNotificationConfirmation confirmation = new BootNotificationConfirmation(LocalDateTime.now().toString() , GlobalEx.OCPP_HEARBEAT_INTERVAL, RegistrationStatus.Accepted);
				SpringContextHelper.getBean(ChargePointEquipmentService.class).bootSave(sessionIndex, request);
				return failurePoint(confirmation);
			}

			@Override
			public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
				DataTransferRequest receivedRequest = (DataTransferRequest)request;
				Map<String, Object> dataMap = receivedRequest.getData();
				switch(receivedRequest.getMessageId()) {
					case "currentrecord":{
						CurrentrecordData currentrecordData = new CurrentrecordData();
						BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(currentrecordData);
						wrapper.setPropertyValues(dataMap);
						ChargePointTransactionService chargePointTransactionService = SpringContextHelper.getBean(ChargePointTransactionService.class);
						ChargePointTransaction chargePointTransaction = chargePointTransactionService.getById(currentrecordData.getTransactionId());
						chargePointTransaction.setCostEnergy(currentrecordData.getCostenergy());
						chargePointTransaction.setCostMoney(currentrecordData.getCostmoney());
						chargePointTransactionService.save(chargePointTransaction);
						
						ChargePointEquipment chargePointEquipment = SpringContextHelper.getBean(ChargePointEquipmentService.class).getById(chargePointTransaction.getChargePointEquipmentId());
						
						//MQTT推送
						ChargeMessage chargeMessage = new ChargeMessage();
						chargeMessage.setType(ChargeMessage.Type.currentrecord);
						
						chargeMessage.setData(JsonUtil.buildNonNullBinder().objectToNode(currentrecordData));
						chargeMessage.setDate(new Date());
						SpringContextHelper.getBean(ChargeMQTTHelper.class).sendTopicPath(chargePointEquipment.getPlatformId() + "/" + chargePointEquipment.getChargePointSerialNumber(), JsonUtil.buildNormalBinder().toJson(chargeMessage)); // /chargePoint/<设备号>
						break;
					}
					default: {
						
					}
				}
				DataTransferConfirmation confirmation = new DataTransferConfirmation(DataTransferStatus.Accepted);
				return failurePoint(confirmation);
			}

			@Override
			public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
//				Request receivedRequest = request;
				ChargePointEquipmentSearchService chargePointEquipmentSearchService = SpringContextHelper.getBean(ChargePointEquipmentSearchService.class);
				ChargePointEquipment chargePointEquipment = chargePointEquipmentSearchService.getByKey(ChargePointEquipment.EQUIPMENT_SESSION, sessionIndex.toString());
				if(chargePointEquipment == null) {
					log.warn("Equipment session {} not exists.", sessionIndex.toString());
				} else {
					chargePointEquipmentSearchService.doHearbeat(chargePointEquipment.getChargePointEquipmentId()); //记录心跳状况
				}
				return failurePoint(new HeartbeatConfirmation(LocalDateTime.now().toString()));
			}

			@Override
			public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {
				MeterValuesRequest receivedRequest = (MeterValuesRequest)request;

				ChargePointEquipment chargePointEquipment = SpringContextHelper.getBean(ChargePointEquipmentSearchService.class).getByKey(ChargePointEquipment.EQUIPMENT_SESSION, sessionIndex.toString());
				if(chargePointEquipment == null) { //正常应该不会不存在，因为是重连更新SESSION后才能充电
					log.error("chargepoint in session {} not exists.", sessionIndex.toString());
					throw new RuntimeException();
				}
				//MQTT推送
				ChargeMessage chargeMessage = new ChargeMessage();
				chargeMessage.setType(ChargeMessage.Type.meterValue);
				String jsonString = JsonUtil.buildNormalBinder().toJson(receivedRequest.getMeterValue());
				chargeMessage.setData(JsonUtil.buildNonNullBinder().toNode(jsonString));
				chargeMessage.setDate(new Date());
				SpringContextHelper.getBean(ChargeMQTTHelper.class).sendTopicPath(chargePointEquipment.getPlatformId() + "/" + chargePointEquipment.getChargePointSerialNumber(), JsonUtil.buildNormalBinder().toJson(chargeMessage)); // /chargePoint/<设备号>

				return failurePoint(new MeterValuesConfirmation());
			}

			@Override
			public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex,
					StartTransactionRequest request) {
				StartTransactionRequest receivedRequest = (StartTransactionRequest)request;
				IdTagInfo tagInfo = new IdTagInfo(AuthorizationStatus.Accepted);
				tagInfo.setExpiryDate(receivedRequest.getTimestamp());
				tagInfo.setParentIdTag(receivedRequest.getIdTag());

				User user = SpringContextHelper.getBean(UserSearchService.class).getByKey(User.USER_MOBILE, receivedRequest.getIdTag());
				if(user == null) {
					log.error("user {} not exists.", receivedRequest.getIdTag());
					throw new RuntimeException();
				}

				ChargePointEquipment chargePointEquipment = SpringContextHelper.getBean(ChargePointEquipmentSearchService.class).getByKey(ChargePointEquipment.EQUIPMENT_SESSION, sessionIndex.toString());
				if(chargePointEquipment == null) { // 正常应该不会不存在，因为是重连更新SESSION后才能充电
					log.error("chargepoint in session {} not exists.", sessionIndex.toString());
					throw new RuntimeException();
				}
				List<ChargePointTransaction> chargePointTransactionList = SpringContextHelper.getBean(ChargePointTransactionSearchService.class).findByMultiKey(
					new String[] {ChargePointTransaction.SYS_USER_ID, ChargePointTransaction.CHARGE_POINT_EQUIPMENT_ID, ChargePointTransaction.CHARGE_TRANSACTION_STATUS_CODE},
					new Object[] {user.getUserId(), chargePointEquipment.getChargePointEquipmentId(), ChargeTransactionStatusCodeEnum.REQUESTED.toCode()},
					Order.desc(ChargePointTransaction.REQUEST_TRANSACTION_TIME)
				);
				if(chargePointTransactionList.isEmpty()) {
					log.error("no requested transaction in user {}.", receivedRequest.getIdTag());
					throw new RuntimeException();
				} else {
					// MQTT推送,APP界面可以跳转了
					ChargeMessage chargeMessage = new ChargeMessage();
					chargeMessage.setType(ChargeMessage.Type.status);
					String jsonString = JsonUtil.buildNormalBinder().toJson(ChargeMessage.START_CHAGE_NOTIFY);
					chargeMessage.setData(JsonUtil.buildNonNullBinder().toNode(jsonString));
					chargeMessage.setDate(new Date());

					StartTransactionConfirmation confirmation = new StartTransactionConfirmation(tagInfo, chargePointTransactionList.get(0).getChargePointTransactionId());
					return failurePoint(confirmation);
				}
			}

			@Override
			public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex,
					StatusNotificationRequest request) {
				StatusNotificationRequest receivedRequest = (StatusNotificationRequest) request;
				
				//记录数据库
				ChargePointEquipment chargePointEquipment = SpringContextHelper.getBean(ChargePointEquipmentSearchService.class).getByKey(ChargePointEquipment.EQUIPMENT_SESSION, sessionIndex.toString());
				if(chargePointEquipment == null) { //正常应该不会不存在，因为是重连更新SESSION后才能充电
					log.error("chargepoint in session {} not exists.", sessionIndex.toString());
					throw new RuntimeException();
				}
				ChargePointEquipmentService chargePointEquipmentService = SpringContextHelper.getBean(ChargePointEquipmentService.class);
				chargePointEquipment = chargePointEquipmentService.getById(chargePointEquipment.getChargePointEquipmentId());
				chargePointEquipment.setChargePointStatus(receivedRequest.getStatus().toString());
				chargePointEquipment.setChargePointError(receivedRequest.getErrorCode().toString());
				chargePointEquipment.setStatusTime(new Date());
				chargePointEquipmentService.save(chargePointEquipment);
				
				ChargePointNotice chargePointNotice = new ChargePointNotice();
				chargePointNotice.setChargePointEquipmentId(chargePointEquipment.getChargePointEquipmentId());
				chargePointNotice.setChargePointErrorCode(receivedRequest.getErrorCode().toString());
				chargePointNotice.setChargePointStatus(receivedRequest.getStatus().toString());
				SpringContextHelper.getBean(ChargePointNoticeService.class).save(chargePointNotice);

				//MQTT推送
				ChargeMessage chargeMessage = new ChargeMessage();
				chargeMessage.setType(ChargeMessage.Type.status);
				chargeMessage.setData(new TextNode(receivedRequest.getStatus() + "|" + receivedRequest.getErrorCode()));
				chargeMessage.setDate(new Date());

				SpringContextHelper.getBean(ChargeMQTTHelper.class).sendTopicPath(chargePointEquipment.getPlatformId() + "/" +chargePointEquipment.getChargePointSerialNumber(), JsonUtil.buildNormalBinder().toJson(chargeMessage)); // /chargePoint/<设备号>

				//更改设备状态
				StatusNotificationConfirmation confirmation = new StatusNotificationConfirmation();
				return failurePoint(confirmation);
			}

			@Override
			public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex,
					StopTransactionRequest request) {
				// MQTT推送,APP界面可以更新
				ChargeMessage chargeMessage = new ChargeMessage();
				chargeMessage.setType(ChargeMessage.Type.status);
				String jsonString = JsonUtil.buildNormalBinder().toJson(ChargeMessage.STOP_CHAGE_NOTIFY);
				chargeMessage.setData(JsonUtil.buildNonNullBinder().toNode(jsonString));
				chargeMessage.setDate(new Date());

				//设备成功停止
				return failurePoint(new StopTransactionConfirmation());
			}
		};
	}

	public ServerFirmwareManagementEventHandler createServerFirmwareManagementEventHandler() {
		return new ServerFirmwareManagementEventHandler() {
			@Override
			public DiagnosticsStatusNotificationConfirmation handleDiagnosticsStatusNotificationRequest(UUID sessionId,
					DiagnosticsStatusNotificationRequest request) {
				Request receivedRequest = request;
				DiagnosticsStatusNotificationConfirmation confirmation = new DiagnosticsStatusNotificationConfirmation();
				return failurePoint(confirmation);
			}

			@Override
			public FirmwareStatusNotificationConfirmation handleFirmwareStatusNotificationRequest(UUID sessionId,
					FirmwareStatusNotificationRequest request) {
				Request receivedRequest = request;
				FirmwareStatusNotificationConfirmation confirmation = new FirmwareStatusNotificationConfirmation();
				return failurePoint(confirmation);
			}
		};
	}

	public ServerEvents generateServerEventsHandler() {
		return new ServerEvents() {
			@Override
			public void newSession(UUID sessionIndex, SessionInformation information) {
				currentSessionIndex = sessionIndex;
				currentIdentifier = information.getIdentifier();
			}

			@Override
			public void lostSession(UUID identity) {
				currentSessionIndex = null;
				currentIdentifier = null;
				// clear
				receivedConfirmation = null;
//				receivedRequest = null;
			}
		};
	}

	public BiConsumer<Confirmation, Throwable> generateWhenCompleteHandler() {
		return (confirmation, throwable) -> receivedConfirmation = confirmation;
	}

	private <T extends Confirmation> T failurePoint(T confirmation) {
		if (riggedToFail) {
			riggedToFail = false;
			throw new RuntimeException();
		}
		return confirmation;
	}

//	public boolean wasLatestRequest(Type requestType) {
//		return requestType != null && receivedRequest != null && requestType.equals(receivedRequest.getClass());
//	}
//
//	public <T extends Request> T getReceivedRequest(T requestType) {
//		if (wasLatestRequest(requestType.getClass()))
//			return (T) receivedRequest;
//		return null;
//	}

	public boolean wasLatestConfirmation(Type confirmationType) {
		return confirmationType != null && receivedConfirmation != null
				&& confirmationType.equals(receivedConfirmation.getClass());
	}

	public <T extends Confirmation> T getReceivedConfirmation(T confirmationType) {
		if (wasLatestConfirmation(confirmationType.getClass()))
			return (T) receivedConfirmation;
		return null;
	}

	public void setRiggedToFail(boolean riggedToFail) {
		this.riggedToFail = riggedToFail;
	}

	public boolean isRiggedToFail() {
		return riggedToFail;
	}

	public String getCurrentIdentifier() {
		return currentIdentifier;
	}

	public UUID getCurrentSessionIndex() {
		return currentSessionIndex;
	}

}
