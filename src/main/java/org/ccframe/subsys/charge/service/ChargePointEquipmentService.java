package org.ccframe.subsys.charge.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.ccframe.commons.base.BaseService;
import org.ccframe.commons.helper.OCPPHelper;
import org.ccframe.commons.util.BusinessException;
import org.ccframe.config.GlobalEx;
import org.ccframe.config.ResGlobalEx;
import org.ccframe.subsys.charge.domain.code.ChargeTransactionStatusCodeEnum;
import org.ccframe.subsys.charge.domain.entity.ChargePointEquipment;
import org.ccframe.subsys.charge.domain.entity.ChargePointPlatformInf;
import org.ccframe.subsys.charge.domain.entity.ChargePointTransaction;
import org.ccframe.subsys.charge.domain.entity.ChargePointUserRel;
import org.ccframe.subsys.charge.repository.ChargePointEquipmentRepository;
import org.ccframe.subsys.core.domain.entity.User;
import org.ccframe.subsys.core.service.UserSearchService;
import org.ccframe.subsys.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core.ChargingProfile;
import eu.chargetime.ocpp.model.core.ChargingProfileKindType;
import eu.chargetime.ocpp.model.core.ChargingProfilePurposeType;
import eu.chargetime.ocpp.model.core.ChargingRateUnitType;
import eu.chargetime.ocpp.model.core.ChargingSchedule;
import eu.chargetime.ocpp.model.core.ChargingSchedulePeriod;
import eu.chargetime.ocpp.model.core.RemoteStartStopStatus;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort.Order;

@Service
@Slf4j
public class ChargePointEquipmentService extends BaseService<ChargePointEquipment, ChargePointEquipmentRepository> {

	@Autowired
	private ChargePointEquipmentSearchService chargePointEquipmentSearchService;
	
	@Autowired
	private ChargePointUserRelSearchService chargePointUserRelSearchService;
	
	@Autowired
	private ChargePointUserRelService chargePointUserRelService;
	
	@Autowired
	private UserSearchService userSearchService;
	
	@Autowired
	private ChargePointPlatformInfSearchService chargePointPlatformInfSearchService;
	
	@Autowired
	private ChargePointTransactionService chargePointTransactionService;
	
	@Autowired
	private ChargePointTransactionSearchService chargePointTransactionSearchService;
	
	@Autowired
	private OCPPHelper ocppHelper;
	
	@Autowired
	private UserService userService;
	
//	@Autowired
//	private RedissonClient redissonClient;

	@Transactional
	public void bootSave(UUID sessionIndex, BootNotificationRequest request) {
		ChargePointEquipment chargePointEquipment = chargePointEquipmentSearchService.getByKey(ChargePointEquipment.CHARGE_POINT_SERIAL_NUMBER, request.getChargePointSerialNumber());
		if(chargePointEquipment == null) { //设备注册
			chargePointEquipment = new ChargePointEquipment();
			chargePointEquipment.setChargePointStatus(ChargePointStatus.Available.toString());
			chargePointEquipment.setChargePointError(ChargePointErrorCode.NoError.toString());
			chargePointEquipment.setStatusTime(new Date());
		}else {
			chargePointEquipment = getById(chargePointEquipment.getChargePointEquipmentId());
		}
		ChargePointPlatformInf chargePointPlatformInf = chargePointPlatformInfSearchService.getByKey(ChargePointPlatformInf.CHARGE_POINT_VENDOR, request.getChargePointVendor());
		if(chargePointPlatformInf == null) {
			chargePointEquipment.setPlatformId(GlobalEx.DEFAULT_PLATFORM_ID); //找不到则默认
		} else {
			chargePointEquipment.setPlatformId(chargePointPlatformInf.getPlatformId());
		}
		chargePointEquipment.setEquipmentSession(sessionIndex.toString());
		chargePointEquipment.setChargePointVendor(request.getChargePointVendor());
		chargePointEquipment.setChargePointModel(request.getChargePointModel());
		chargePointEquipment.setChargePointSerialNumber(request.getChargePointSerialNumber());
		chargePointEquipment.setFirmwareVer(request.getFirmwareVersion());
		save(chargePointEquipment);
	}

	@Transactional
	public void bindUser(Integer userId, String chargePointSerialNumber, String sourceUserMobile, int platformId) {
		ChargePointEquipment chargePointEquipment = chargePointEquipmentSearchService.getByKey(ChargePointEquipment.CHARGE_POINT_SERIAL_NUMBER, chargePointSerialNumber);
		if(chargePointEquipment == null) {
    		throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"设备尚未注册"});
		}
		User sourceUser = null;
		if(StringUtils.isNotBlank(sourceUserMobile)) {
			sourceUser = userSearchService.getByMultiKey(new String[] {User.USER_MOBILE, User.PLATFORM_ID}, sourceUserMobile, platformId);
			if(sourceUser == null) {
	    		throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"分享用户不存在"});
			}
		}

		ChargePointUserRel chargePointUserRel = null;
		if(sourceUser == null) {
			chargePointUserRel = chargePointUserRelSearchService.getByMultiKey(new String[] {ChargePointUserRel.SYS_USER_ID, ChargePointUserRel.CHARGE_POINT_EQUIPMENT_ID}, userId, chargePointEquipment.getChargePointEquipmentId());
		} else {
			chargePointUserRel = chargePointUserRelSearchService.getByMultiKey(new String[] {ChargePointUserRel.SYS_USER_ID, ChargePointUserRel.CHARGE_POINT_EQUIPMENT_ID, ChargePointUserRel.SOURCE_USER_ID}, userId, chargePointEquipment.getChargePointEquipmentId(), sourceUser.getUserId());
		}
		
		if(chargePointUserRel == null) { //如果未绑定才需要绑定
			chargePointUserRel = new ChargePointUserRel();
			chargePointUserRel.setChargePointEquipmentId(chargePointEquipment.getChargePointEquipmentId());
			chargePointUserRel.setSysUserId(userId);
			if(sourceUser != null) {
				chargePointUserRel.setSourceUserId(sourceUser.getUserId());
			}
			chargePointUserRelService.save(chargePointUserRel);
		}
	}

	public void remoteStartTransaction(String chargePointSerialNumber, Integer userId, Integer platformId) {
		ChargePointEquipment chargePointEquipment = chargePointEquipmentSearchService.getByMultiKey(new String[] {ChargePointEquipment.CHARGE_POINT_SERIAL_NUMBER, ChargePointEquipment.PLATFORM_ID}, chargePointSerialNumber, platformId);
		if(chargePointEquipment == null) {
    		throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"设备不存在"});
		}
		if(chargePointEquipment.getChargePointStatus().equals(ChargePointStatus.Faulted.toString())){
			throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"设备故障"});
		}
		//TODO 设备被预约
		User user = userService.getById(userId);
		
		ChargePointTransaction chargePointTransaction = new ChargePointTransaction();
		chargePointTransaction.setChargePointEquipmentId(chargePointEquipment.getChargePointEquipmentId());
		chargePointTransaction.setSysUserId(userId);
		chargePointTransaction.setRequestTransactionTime(new Date());
		chargePointTransaction.setChargeTransactionStatusCode(ChargeTransactionStatusCodeEnum.REQUESTED.toCode());
		chargePointTransactionService.save(chargePointTransaction);

		ChargingSchedule chargingSchedule = new ChargingSchedule(ChargingRateUnitType.A, new ChargingSchedulePeriod[] {
			new ChargingSchedulePeriod(1, 32.0) //默认numberPhases=3，功率限制32A
		});
		chargingSchedule.setDuration(0);
		ChargingProfile chargingProfile = new ChargingProfile(1, 1, ChargingProfilePurposeType.TxProfile, ChargingProfileKindType.Recurring, chargingSchedule);
		chargingProfile.setTransactionId(chargePointTransaction.getChargePointTransactionId());
		
		RemoteStartTransactionRequest remoteStartTransactionRequest = new RemoteStartTransactionRequest(user.getUserMobile());
		remoteStartTransactionRequest.setConnectorId(1); //可选的。 启动事务的连接器的数量。 连接器ID应>0
		remoteStartTransactionRequest.setChargingProfile(chargingProfile);
		try {
			ocppHelper.send(UUID.fromString(chargePointEquipment.getEquipmentSession()), remoteStartTransactionRequest).whenComplete((confirmation, throwable) -> {
				RemoteStartTransactionConfirmation remoteStartTransactionConfirmation = (RemoteStartTransactionConfirmation)confirmation;
				if(remoteStartTransactionConfirmation.getStatus() == RemoteStartStopStatus.Accepted) { //记录充电事务
					log.info("设备收到充电请求");
				}
			});
		} catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException e) {
			log.error("启动充电失败", e);
			throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"启动充电失败"});
		}
	}

	public void remoteStopTransaction(String chargePointSerialNumber, Integer userId, Integer platformId) {
		ChargePointEquipment chargePointEquipment = chargePointEquipmentSearchService.getByMultiKey(new String[] {ChargePointEquipment.CHARGE_POINT_SERIAL_NUMBER, ChargePointEquipment.PLATFORM_ID}, chargePointSerialNumber, platformId);
		if(chargePointEquipment == null) {
    		throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"设备不存在"});
		}
		if(chargePointEquipment.getChargePointStatus().equals(ChargePointStatus.Faulted.toString())){
			throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"设备故障"});
		}
		//停止该设备该用户的最后一个未停止的事务
		List<ChargePointTransaction> chargePointTransactionList = chargePointTransactionSearchService.findByMultiKey(
			new String[] {ChargePointTransaction.CHARGE_POINT_EQUIPMENT_ID, ChargePointTransaction.SYS_USER_ID}, 
			new Object[] {chargePointEquipment.getChargePointEquipmentId(), userId}, 
			Order.desc(ChargePointTransaction.REQUEST_TRANSACTION_TIME)
		);
		if(chargePointTransactionList.isEmpty()) {
			throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"当前用户没有进行的充电会话"});
		}
		ChargePointTransaction chargePointTransaction = chargePointTransactionList.get(0);
		chargePointTransaction.setRequestStopTime(new Date());
		chargePointTransaction.setChargeTransactionStatusCode(ChargeTransactionStatusCodeEnum.STOPPING.toCode());
		chargePointTransactionService.save(chargePointTransaction);
		
		RemoteStopTransactionRequest remoteStopTransactionRequest = new RemoteStopTransactionRequest(chargePointTransaction.getChargePointTransactionId());
		try {
			ocppHelper.send(UUID.fromString(chargePointEquipment.getEquipmentSession()), remoteStopTransactionRequest).whenComplete((confirmation, throwable) -> {
				RemoteStartTransactionConfirmation remoteStartTransactionConfirmation = (RemoteStartTransactionConfirmation)confirmation;
				if(remoteStartTransactionConfirmation.getStatus() == RemoteStartStopStatus.Accepted) { //记录充电事务
					log.info("设备收到停止充电请求");
				}
			});
		} catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException e) {
			log.error("停止充电失败", e);
			throw new BusinessException(ResGlobalEx.ERRORS_USER_DEFINED, new String[]{"停止充电失败"});
		}
	}

}
