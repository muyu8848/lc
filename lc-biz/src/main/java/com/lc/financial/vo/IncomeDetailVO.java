package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.FinancialIncomeRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class IncomeDetailVO {
	
	private Double generateIncomeAmount = 0d;
	
	private Double income = 0d;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date theDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date buyInDate;
	
	public static IncomeDetailVO buildTheDate(Double income, Date theDate) {
		IncomeDetailVO vo = new IncomeDetailVO();
		vo.setIncome(income);
		vo.setTheDate(theDate);
		return vo;
	}
	
	public static List<IncomeDetailVO> convertFor(List<FinancialIncomeRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<IncomeDetailVO> vos = new ArrayList<>();
		for (FinancialIncomeRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static IncomeDetailVO convertFor(FinancialIncomeRecord po) {
		if (po == null) {
			return null;
		}
		IncomeDetailVO vo = new IncomeDetailVO();
		BeanUtils.copyProperties(po, vo);
		vo.setBuyInDate(po.getBuyInRecord().getBuyInDate());
		return vo;
	}

}
