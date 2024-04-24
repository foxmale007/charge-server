package org.ccframe.commons.ocpp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CurrentrecordData {
	
	private Integer id;

	private Integer connectorId;
	
	private Integer chargemode;
	
	private Double startengry;
	
	private Double endengry;
    
	private String starttime;
    
	private String endtime;
	
	private Double costenergy;
	
	private Double costmoney;
	
	private Integer transactionId;
	
	private String stopReason;
}
