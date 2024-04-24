package org.ccframe.subsys.charge.mqtt;

import java.util.Date;

import org.ccframe.config.GlobalEx;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChargeMessage {
	
	public static final String START_CHAGE_NOTIFY = "startCharge|noError"; //充电开始

	public static final String STOP_CHAGE_NOTIFY = "stopCharge|noError"; //充电停止
	
	public static enum Type {
		status, //设备状态推送 格式为：主状态|子状态，除了标准的几个状态外，还增加2个状态：充电开始 和 充电停止。用于引导APP界面跳转到充电状态及充电完成跳转到结账状态
		meterValue, //电量状态推送，状态推送时为状态代码，仪表值时为List<MeterValue>
		currentrecord //结账账单
	}

    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = GlobalEx.STANDERD_DATE_FORMAT)
	private Date date;
	
	private Type type;
	
	private JsonNode data; //根据具体type来解析
}
