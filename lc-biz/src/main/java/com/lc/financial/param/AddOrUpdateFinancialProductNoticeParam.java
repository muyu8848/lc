package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.FinancialProductNotice;

import lombok.Data;

@Data
public class AddOrUpdateFinancialProductNoticeParam {
	
	private String id;
	
	@NotBlank
	private String title;

	@NotBlank
	private String link;
	
	@NotBlank
	private String financialProductId;
	
	public FinancialProductNotice convertToPo() {
		FinancialProductNotice po = new FinancialProductNotice();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setLastModifyTime(po.getCreateTime());
		po.setDeletedFlag(false);
		return po;
	}

}
