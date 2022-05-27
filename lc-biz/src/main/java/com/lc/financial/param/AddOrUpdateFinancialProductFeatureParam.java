package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.FinancialProductFeature;

import lombok.Data;

@Data
public class AddOrUpdateFinancialProductFeatureParam {

	private String id;

	@NotBlank
	private String icon;

	@NotBlank
	private String title;

	@NotBlank
	private String content;

	@NotBlank
	private String financialProductId;

	public FinancialProductFeature convertToPo() {
		FinancialProductFeature po = new FinancialProductFeature();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setLastModifyTime(po.getCreateTime());
		po.setDeletedFlag(false);
		return po;
	}

}
