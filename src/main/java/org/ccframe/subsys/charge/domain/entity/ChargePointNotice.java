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
  产品 充电桩 通知
  @Author Auto
  generate at 2021-12-14 11:41:07
*/

@Entity
@Table(name = "PRD_CHARGE_POINT_NOTICE")
@Document(indexName = "chargepointnotice")
@Setting(settingPath = GlobalEx.ES_DEFAULT_ANALYSER)
@Getter
@Setter
@ToString
@ApiModel("产品 充电桩 通知")
public class ChargePointNotice extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	public static final String CHARGE_POINT_NOTICE_ID = "chargePointNoticeId"; //充电桩 通知 ID
	public static final String CHARGE_POINT_EQUIPMENT_ID = "chargePointEquipmentId"; //充电桩 设备 ID
	public static final String CHARGE_POINT_STATUS = "chargePointStatus"; //充电桩 状态
	public static final String CHARGE_POINT_ERROR_CODE = "chargePointErrorCode"; //充电桩 错误 代码
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // hibernate 5 的 MYSQL 下 AUTO 策略无法对应自增，等修复
	// elasticsearch
	@org.springframework.data.annotation.Id
	@Column(name = "CHARGE_POINT_NOTICE_ID", nullable = false, length = 10)
	@ApiModelProperty("充电桩 通知 ID")
	private Integer chargePointNoticeId; //充电桩 通知 ID

	@Column(name = "CHARGE_POINT_EQUIPMENT_ID", nullable = false)
	@Field(type = FieldType.Integer)
	@ApiModelProperty("充电桩 设备 ID")
	private Integer chargePointEquipmentId; //充电桩 设备 ID

	@Column(name = "CHARGE_POINT_STATUS", nullable = false, length = 15)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电桩 状态")
	private String chargePointStatus; //充电桩 状态

	@Column(name = "CHARGE_POINT_ERROR_CODE", nullable = false, length = 20)
	@Field(type = FieldType.Keyword)
	@ApiModelProperty("充电桩 错误 代码")
	private String chargePointErrorCode; //充电桩 错误 代码



	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChargePointNotice)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		ChargePointNotice other = (ChargePointNotice) obj;
		return new EqualsBuilder().append(getId(), other.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getChargePointNoticeId()).toHashCode();
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return getChargePointNoticeId();
	}
	
}
	
