package org.ccframe.subsys.charge.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ccframe.commons.base.BaseEntity;
import org.ccframe.config.GlobalEx;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
  产品 充电桩 设备
  @Author Auto
  generate at 2021-12-14 12:26:59
*/

@Entity
@Table(name = "PRD_CHARGE_POINT_EQUIPMENT")
@Document(indexName = "chargepointequipment")
@Setting(settingPath = GlobalEx.ES_DEFAULT_ANALYSER)
@Getter
@Setter
@ToString
@ApiModel("产品 充电桩 设备")
public class ChargePointEquipment extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	public static final String CHARGE_POINT_EQUIPMENT_ID = "chargePointEquipmentId"; //充电桩 设备 ID
	public static final String PLATFORM_ID = "platformId"; //平台 ID
	public static final String CHARGE_POINT_VENDOR = "chargePointVendor"; //充电桩 厂商
	public static final String CHARGE_POINT_MODEL = "chargePointModel"; //充电桩 型号
	public static final String CHARGE_POINT_SERIAL_NUMBER = "chargePointSerialNumber"; //充电桩 序列号
	public static final String FIRMWARE_VER = "firmwareVer"; //固件 版本
	public static final String CHARGE_POINT_STATUS = "chargePointStatus"; //充电桩 状态
	public static final String CHARGE_POINT_ERROR = "chargePointError"; //充电桩 错误
	public static final String STATUS_TIME = "statusTime"; //状态 时间
	public static final String EQUIPMENT_SESSION = "equipmentSession"; //设备 会话
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // hibernate 5 的 MYSQL 下 AUTO 策略无法对应自增，等修复
	// elasticsearch
	@org.springframework.data.annotation.Id
	@Column(name = "CHARGE_POINT_EQUIPMENT_ID", nullable = false, length = 10)
	@ApiModelProperty("充电桩 设备 ID")
	private Integer chargePointEquipmentId; //充电桩 设备 ID

	@Column(name = "PLATFORM_ID", nullable = false)
	@Field(type = FieldType.Integer)
	@ApiModelProperty("平台 ID")
	private Integer platformId; //平台 ID

	@Column(name = "CHARGE_POINT_VENDOR", nullable = false, length = 50)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电桩 厂商")
	private String chargePointVendor; //充电桩 厂商

	@Column(name = "CHARGE_POINT_MODEL", nullable = false, length = 50)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电桩 型号")
	private String chargePointModel; //充电桩 型号

	@Column(name = "CHARGE_POINT_SERIAL_NUMBER", nullable = false, length = 50)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电桩 序列号")
	private String chargePointSerialNumber; //充电桩 序列号

	@Column(name = "FIRMWARE_VER", nullable = false, length = 50)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("固件 版本")
	private String firmwareVer; //固件 版本

	@Column(name = "CHARGE_POINT_STATUS", nullable = false, length = 15)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电桩 状态")
	private String chargePointStatus; //充电桩 状态

	@Column(name = "CHARGE_POINT_ERROR", nullable = false, length = 20)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电桩 错误")
	private String chargePointError; //充电桩 错误

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_TIME", nullable = false)
	// elasticsearch
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = GlobalEx.ES_DATE_PATTERN)
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = GlobalEx.STANDERD_DATE_FORMAT, timezone = GlobalEx.TIMEZONE)
	@ApiModelProperty("状态 时间")
	private Date statusTime; //状态 时间

	@Column(name = "EQUIPMENT_SESSION", nullable = false, length = 36)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("设备 会话")
	private String equipmentSession; //设备 会话



	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChargePointEquipment)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		ChargePointEquipment other = (ChargePointEquipment) obj;
		return new EqualsBuilder().append(getId(), other.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getChargePointEquipmentId()).toHashCode();
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return getChargePointEquipmentId();
	}
	
}
	
