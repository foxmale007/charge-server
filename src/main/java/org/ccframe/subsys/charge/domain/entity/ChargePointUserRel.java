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
  产品 充电桩 用户 关系
  @Author Auto
  generate at 2021-12-12 10:01:36
*/

@Entity
@Table(name = "PRD_CHARGE_POINT_USER_REL")
@Document(indexName = "chargepointuserrel")
@Setting(settingPath = GlobalEx.ES_DEFAULT_ANALYSER)
@Getter
@Setter
@ToString
public class ChargePointUserRel extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	public static final String CHARGE_POINT_USER_REL_ID = "chargePointUserRelId"; //充电桩 用户 关系 ID
	public static final String CHARGE_POINT_EQUIPMENT_ID = "chargePointEquipmentId"; //充电桩 设备 ID
	public static final String SYS_USER_ID = "sysUserId"; //系统 用户 ID
	public static final String SOURCE_USER_ID = "sourceUserId"; //来源 用户 ID
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // hibernate 5 的 MYSQL 下 AUTO 策略无法对应自增，等修复
	// elasticsearch
	@org.springframework.data.annotation.Id
	@Column(name = "CHARGE_POINT_USER_REL_ID", nullable = false, length = 10)
	private Integer chargePointUserRelId; //充电桩 用户 关系 ID

	@Column(name = "CHARGE_POINT_EQUIPMENT_ID", nullable = false)
	@Field(type = FieldType.Integer)
	private Integer chargePointEquipmentId; //充电桩 设备 ID

	@Column(name = "SYS_USER_ID", nullable = false)
	@Field(type = FieldType.Integer)
	private Integer sysUserId; //系统 用户 ID

	@Column(name = "SOURCE_USER_ID", nullable = true)
	@Field(type = FieldType.Integer)
	private Integer sourceUserId; //来源 用户 ID



	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChargePointUserRel)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		ChargePointUserRel other = (ChargePointUserRel) obj;
		return new EqualsBuilder().append(getId(), other.getId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getChargePointUserRelId()).toHashCode();
	}

	@Override
	@JsonIgnore
	public Integer getId() {
		return getChargePointUserRelId();
	}
	
}
	
