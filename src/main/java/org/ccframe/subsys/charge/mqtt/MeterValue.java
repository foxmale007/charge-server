package org.ccframe.subsys.charge.mqtt;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MeterValue {
	
	private double value;
	
	private String context;
	
	private String format;
	
	private String measurand;
	
	private String unit;
}
