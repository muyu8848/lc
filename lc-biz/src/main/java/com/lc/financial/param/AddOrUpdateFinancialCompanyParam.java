package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.FinancialCompany;

import lombok.Data;

@Data
public class AddOrUpdateFinancialCompanyParam {

	private String id;

	@NotBlank
	private String companyName;

	@NotBlank
	private String companyAddress;

	private String weChatOfficialAccount;

	@NotBlank
	private String contactNumber;

	private String websiteUrl;

	@NotBlank
	private String basicInformation;

	private String investmentCapacity;

	private String ownershipStructure;

	public FinancialCompany convertToPo() {
		FinancialCompany po = new FinancialCompany();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setDeletedFlag(false);
		return po;
	}

}
