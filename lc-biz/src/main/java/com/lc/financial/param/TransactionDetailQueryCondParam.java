package com.lc.financial.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionDetailQueryCondParam extends PageParam {
	
	private String accountId;
	
	private String type;
	
	private String financialProductId;
	
	@DateTimeFormat(pattern = "yyyy-MM")
	private Date theMonth;

}
