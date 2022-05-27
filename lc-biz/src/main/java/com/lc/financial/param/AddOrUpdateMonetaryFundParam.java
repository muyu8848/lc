package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.MonetaryFund;

import lombok.Data;

@Data
public class AddOrUpdateMonetaryFundParam {

	private String id;

	@NotBlank
	private String fundName;

	@NotBlank
	private String fundCode;

	@NotBlank
	private String fundCategory;

	@NotBlank
	private String operationMode;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date establishedTime;

	@NotBlank
	private String fundSize;

	@NotBlank
	private String fundCustody;

	@NotBlank
	private String issuingAgency;

	@NotBlank
	private String fundIntro;

	public MonetaryFund convertToPo() {
		MonetaryFund po = new MonetaryFund();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setLastModifyTime(po.getCreateTime());
		po.setDeletedFlag(false);
		return po;
	}

}
