package com.lc.financial.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FinancialProductBuyInRecordQueryCondParam extends PageParam {
	
	private String orderNo;
	
	private String state;

	private String accountId;
	
	private String userName;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTimeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTimeEnd;

}
