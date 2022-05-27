package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.dictconfig.DictHolder;
import com.lc.financial.domain.FinancialProduct;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FinancialProductVO {

	private String id;

	private String productFullName;

	private String productName;

	private String productCode;

	private String productType;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date establishDate;

	private String productIntroduce;

	private String incomeType;

	private String incomeTypeName;

	private Double minSubscribeAmount;

	private Double minRateOfReturn;

	private Double maxRateOfReturn;

	private Double minChg;

	private Double maxChg;

	private Integer productTerm;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String financialCompanyId;

	public static List<FinancialProductVO> convertFor(List<FinancialProduct> products) {
		if (CollectionUtil.isEmpty(products)) {
			return new ArrayList<>();
		}
		List<FinancialProductVO> vos = new ArrayList<>();
		for (FinancialProduct product : products) {
			vos.add(convertFor(product));
		}
		return vos;
	}

	public static FinancialProductVO convertFor(FinancialProduct product) {
		if (product == null) {
			return null;
		}
		FinancialProductVO vo = new FinancialProductVO();
		BeanUtils.copyProperties(product, vo);
		vo.setIncomeTypeName(DictHolder.getDictItemName("incomeType", vo.getIncomeType()));
		return vo;
	}

}
