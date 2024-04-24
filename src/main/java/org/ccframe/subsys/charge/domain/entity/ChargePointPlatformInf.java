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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ccframe.commons.base.BaseEntity;
import org.ccframe.config.GlobalEx;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
  产品 充电桩 平台 信息
  @Author Auto
  generate at 2021-12-12 10:42:05
*/

@Entity
@Table(name = "PRD_CHARGE_POINT_PLATFORM_INF")
@Document(indexName = "chargepointplatforminf")
@Setting(settingPath = GlobalEx.ES_DEFAULT_ANALYSER)
@Getter
@Setter
@ToString
public class ChargePointPlatformInf extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	public static final String CHARGE_POINT_PLATFORM_INF_ID = "chargePointPlatformInfId"; //充电桩 平台 信息 ID
	public static final String PLATFORM_ID = "platformId"; //平台 ID
	public static final String CHARGE_POINT_VENDOR = "chargePointVendor"; //充电桩 厂商
	public static final String VENDOR_NAME = "vendorName"; //厂商 名称
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // hibernate 5 的 MYSQL 下 AUTO 策略无法对应自增，等修复
	// elasticsearch
	@org.springframework.data.annotation.Id
	@Column(name = "CHARGE_POINT_PLATFORM_INF_ID", nullable = false, length = 10)
	private Integer chargePointPlatformInfId; //充电桩 平台 信息 ID

	@Column(name = "PLATFORM_ID", nullable = false)
	@Field(type = FieldType.Integer)
	private Integer platformId; //平台 ID

	@Column(name = "CHARGE_POINT_VENDOR", nullable = false, length = 50)
	@Field(type = FieldType.Keyword)
	private String chargePointVendor; //充电桩 厂商

	@Column(name = "VENDOR_NAME", nullable = false, length = 100)
	@Field(type = FieldType.Keyword)
	private String vendorName; //厂商 名称



	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChargePointPlatformInf)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		ChargePointPlatformInf other = (ChargePointPlatformInf) obj;
		return new EqualsBuilder().append(getId(), other.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getChargePointPlatformInfId()).toHashCode();
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return getChargePointPlatformInfId();
	}
	
}
	
