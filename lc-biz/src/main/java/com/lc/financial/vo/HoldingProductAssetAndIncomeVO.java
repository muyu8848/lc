package com.lc.financial.vo;

import lombok.Data;

@Data
public class HoldingProductAssetAndIncomeVO {

	private String id;

	private String productName;
	
	private Double totalAsset = 0d;
	
	private Double totalQuota = 0d;
	
	private Double totalIncome = 0d;
	
	private Double yesterdayIncome = 0d;

	public static HoldingProductAssetAndIncomeVO build(String id, String productName) {
		HoldingProductAssetAndIncomeVO vo = new HoldingProductAssetAndIncomeVO();
		vo.setId(id);
		vo.setProductName(productName);
		return vo;
	}

}
