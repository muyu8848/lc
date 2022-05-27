package com.lc.financial.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IncomeDetailQueryCondParam extends PageParam {
	
	private String accountId;
	
	private String financialProductId;
	
	@DateTimeFormat(pattern = "yyyy-MM")
	private Date theMonth;

}
