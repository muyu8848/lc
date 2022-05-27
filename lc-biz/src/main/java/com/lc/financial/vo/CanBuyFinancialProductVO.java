package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.common.utils.RedisUtils;
import com.lc.constants.Constant;
import com.lc.financial.domain.FinancialProduct;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class CanBuyFinancialProductVO {

	private String id;

	private String productFullName;

	private String productName;

	private String productCode;

	private String productType;

	@JsonFormat(pattern = "yyyy年MM月dd日", timezone = "GMT+8")
	private Date establishDate;
	
	private String productIntroduce;

	private Double minSubscribeAmount;
	
	private Integer productTerm;
	
	private String incomeType;
	
	private Double latestYearRateOfReturn = 0d;
	
	private Double latestYearNpvRate = 0d;
	
	public static List<CanBuyFinancialProductVO> convertFor(List<FinancialProduct> products) {
		if (CollectionUtil.isEmpty(products)) {
			return new ArrayList<>();
		}
		List<CanBuyFinancialProductVO> vos = new ArrayList<>();
		for (FinancialProduct product : products) {
			vos.add(convertFor(product));
		}
		return vos;
	}

	public static CanBuyFinancialProductVO convertFor(FinancialProduct product) {
		if (product == null) {
			return null;
		}
		CanBuyFinancialProductVO vo = new CanBuyFinancialProductVO();
		BeanUtils.copyProperties(product, vo);
		String latestYearRateOfReturn = RedisUtils.opsForValueGet(Constant.产品年化收益率_最新 + product.getId());
		if (StrUtil.isNotBlank(latestYearRateOfReturn)) {
			vo.setLatestYearRateOfReturn(Double.parseDouble(latestYearRateOfReturn));
		}
		String latestYearNpvRate = RedisUtils.opsForValueGet(Constant.产品年化净值增长率_最新 + product.getId());
		if (StrUtil.isNotBlank(latestYearNpvRate)) {
			vo.setLatestYearNpvRate(Double.parseDouble(latestYearNpvRate));
		}
		return vo;
	}

}
