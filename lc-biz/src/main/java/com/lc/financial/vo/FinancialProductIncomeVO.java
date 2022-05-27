package com.lc.financial.vo;

import lombok.Data;

@Data
public class FinancialProductIncomeVO {

	private String id;

	private String productName;

	private Double income = 0d;

	public static FinancialProductIncomeVO build(String id, String productName) {
		FinancialProductIncomeVO vo = new FinancialProductIncomeVO();
		vo.setId(id);
		vo.setProductName(productName);
		return vo;
	}

}
