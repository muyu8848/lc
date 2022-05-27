package com.lc.financial.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.lc.common.utils.IdUtils;
import com.lc.financial.domain.FinancialProduct;

import lombok.Data;

@Data
public class AddOrUpdateFinancialProductParam {

	private String id;

	@NotBlank
	private String financialCompanyId;

	@NotBlank
	private String productFullName;

	@NotBlank
	private String productName;

	@NotBlank
	private String productCode;

	@NotBlank
	private String productType;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date establishDate;

	@NotBlank
	private String productIntroduce;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double minSubscribeAmount;

	@NotBlank
	private String incomeType;

	private Double minRateOfReturn;

	private Double maxRateOfReturn;

	private Double minChg;

	private Double maxChg;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer productTerm;

	public FinancialProduct convertToPo() {
		FinancialProduct po = new FinancialProduct();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setDeletedFlag(false);
		return po;
	}

}
