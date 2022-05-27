package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.FinancialProductFeature;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FinancialProductFeatureVO {
	
	
	private String id;

	private String icon;

	private String title;

	private String content;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastModifyTime;
	
	public static List<FinancialProductFeatureVO> convertFor(List<FinancialProductFeature> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FinancialProductFeatureVO> vos = new ArrayList<>();
		for (FinancialProductFeature po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FinancialProductFeatureVO convertFor(FinancialProductFeature po) {
		if (po == null) {
			return null;
		}
		FinancialProductFeatureVO vo = new FinancialProductFeatureVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
