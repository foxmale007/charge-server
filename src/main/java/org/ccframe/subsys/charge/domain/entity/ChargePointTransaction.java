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
  产品 充电桩 事务
  @Author Auto
  generate at 2021-12-14 14:41:15
*/

@Entity
@Table(name = "PRD_CHARGE_POINT_TRANSACTION")
@Document(indexName = "chargepointtransaction")
@Setting(settingPath = GlobalEx.ES_DEFAULT_ANALYSER)
@Getter
@Setter
@ToString
@ApiModel("产品 充电桩 事务")
public class ChargePointTransaction extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	public static final String CHARGE_POINT_TRANSACTION_ID = "chargePointTransactionId"; //充电桩 事务 ID
	public static final String CHARGE_POINT_EQUIPMENT_ID = "chargePointEquipmentId"; //充电桩 设备 ID
	public static final String SYS_USER_ID = "sysUserId"; //系统 用户 ID
	public static final String REQUEST_TRANSACTION_TIME = "requestTransactionTime"; //请求 传输 时间
	public static final String START_TRANSACTION_TIME = "startTransactionTime"; //开始 传输 时间
	public static final String REQUEST_STOP_TIME = "requestStopTime"; //请求 停止 时间
	public static final String FINISH_STOP_TIME = "finishStopTime"; //完成 停止 时间
	public static final String CHARGE_TRANSACTION_STATUS_CODE = "chargeTransactionStatusCode"; //充电 事务 状态 代码
	public static final String COST_ENERGY = "costEnergy"; //消耗 电量
	public static final String COST_MONEY = "costMoney"; //计费
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // hibernate 5 的 MYSQL 下 AUTO 策略无法对应自增，等修复
	// elasticsearch
	@org.springframework.data.annotation.Id
	@Column(name = "CHARGE_POINT_TRANSACTION_ID", nullable = false, length = 10)
	@ApiModelProperty("充电桩 事务 ID")
	private Integer chargePointTransactionId; //充电桩 事务 ID

	@Column(name = "CHARGE_POINT_EQUIPMENT_ID", nullable = false)
	@Field(type = FieldType.Integer)
	@ApiModelProperty("充电桩 设备 ID")
	private Integer chargePointEquipmentId; //充电桩 设备 ID

	@Column(name = "SYS_USER_ID", nullable = false)
	@Field(type = FieldType.Integer)
	@ApiModelProperty("系统 用户 ID")
	private Integer sysUserId; //系统 用户 ID

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_TRANSACTION_TIME", nullable = false)
	// elasticsearch
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = GlobalEx.ES_DATE_PATTERN)
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = GlobalEx.STANDERD_DATE_FORMAT, timezone = GlobalEx.TIMEZONE)
	@ApiModelProperty("请求 传输 时间")
	private Date requestTransactionTime; //请求 传输 时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_TRANSACTION_TIME", nullable = true)
	// elasticsearch
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = GlobalEx.ES_DATE_PATTERN)
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = GlobalEx.STANDERD_DATE_FORMAT, timezone = GlobalEx.TIMEZONE)
	@ApiModelProperty("开始 传输 时间")
	private Date startTransactionTime; //开始 传输 时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_STOP_TIME", nullable = true)
	// elasticsearch
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = GlobalEx.ES_DATE_PATTERN)
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = GlobalEx.STANDERD_DATE_FORMAT, timezone = GlobalEx.TIMEZONE)
	@ApiModelProperty("请求 停止 时间")
	private Date requestStopTime; //请求 停止 时间

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FINISH_STOP_TIME", nullable = true)
	// elasticsearch
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = GlobalEx.ES_DATE_PATTERN)
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = GlobalEx.STANDERD_DATE_FORMAT, timezone = GlobalEx.TIMEZONE)
	@ApiModelProperty("完成 停止 时间")
	private Date finishStopTime; //完成 停止 时间

	@Column(name = "CHARGE_TRANSACTION_STATUS_CODE", nullable = false, length = 2)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电 事务 状态 代码")
	private String chargeTransactionStatusCode; //充电 事务 状态 代码

	@Column(name = "COST_ENERGY", nullable = true)
	@Field(type = FieldType.Double)
	@ApiModelProperty("消耗 电量")
	private Double costEnergy; //消耗 电量

	@Column(name = "COST_MONEY", nullable = true)
	@Field(type = FieldType.Double)
	@ApiModelProperty("计费")
	private Double costMoney; //计费



	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChargePointTransaction)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		ChargePointTransaction other = (ChargePointTransaction) obj;
		return new EqualsBuilder().append(getId(), other.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getChargePointTransactionId()).toHashCode();
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return getChargePointTransactionId();
	}
	
}
	
