package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.FinancialTakeOutRecord;

import lombok.Data;

@Data
public class TakeOutParam {
	
	@NotBlank
	private String financialProductId;

	private Double amount;
	
	private Double quota;

	@NotBlank
	private String accountId;
	
	public FinancialTakeOutRecord convertToPo() {
		FinancialTakeOutRecord po = new FinancialTakeOutRecord();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		return po;
	}

}
