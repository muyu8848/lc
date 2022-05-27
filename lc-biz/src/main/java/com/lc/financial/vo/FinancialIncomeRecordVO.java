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
public class FinancialIncomeRecordVO {

	private String id;

	private Double generateIncomeAmount;

	private Double rateOfReturn;

	private Double generateIncomeQuota;

	private Double chg;

	private Double income;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date theDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date settlementTime;

	public static List<FinancialIncomeRecordVO> convertFor(List<FinancialIncomeRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FinancialIncomeRecordVO> vos = new ArrayList<>();
		for (FinancialIncomeRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FinancialIncomeRecordVO convertFor(FinancialIncomeRecord po) {
		if (po == null) {
			return null;
		}
		FinancialIncomeRecordVO vo = new FinancialIncomeRecordVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
