package com.lc.financial.vo;

import lombok.Data;

@Data
public class HoldingRecordGroupByBuyInDateVO {

	private Double asset = 0d;

	private Double totalIncome = 0d;

	private Double yesterdayIncome = 0d;
	
	private String issueNum;
	
	private Long daysRemaining;


}
