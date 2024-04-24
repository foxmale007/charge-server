package org.ccframe.subsys.charge.dto;

import org.ccframe.subsys.charge.domain.entity.ChargePointEquipment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel("充电桩 设备 绑定 信息")
public class ChargePointEquipmentRow extends ChargePointEquipment {
	
	private static final long serialVersionUID = 6725439751776768995L;

	@ApiModelProperty("源用户手机，可以用于判断是否来源于分享")
	private String sourceUserMobile;
	
	@ApiModelProperty("设备是否在线，判断条件为3分钟内有心跳")
	private boolean online;
}
