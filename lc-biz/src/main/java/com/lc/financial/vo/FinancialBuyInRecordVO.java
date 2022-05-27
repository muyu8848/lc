package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.dictconfig.DictHolder;
import com.lc.financial.domain.FinancialBuyInRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FinancialBuyInRecordVO {

	private String id;

	private String orderNo;

	private Double buyInAmount;

	private Double buyInQuota;

	private Double buyInNpv;

	private Double availableAmount;

	private Double availableQuota;

	private Double income;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date buyInDate;

	private String financialProductId;

	private String productName;

	private Integer productTerm;

	private String userName;

	private String incomeType;

	private String incomeTypeName;

	public static List<FinancialBuyInRecordVO> convertFor(List<FinancialBuyInRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FinancialBuyInRecordVO> vos = new ArrayList<>();
		for (FinancialBuyInRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FinancialBuyInRecordVO convertFor(FinancialBuyInRecord po) {
		if (po == null) {
			return null;
		}
		FinancialBuyInRecordVO vo = new FinancialBuyInRecordVO();
		BeanUtils.copyProperties(po, vo);
		vo.setStateName(DictHolder.getDictItemName("buyInRecordState", vo.getState()));
		if (po.getFinancialProduct() != null) {
			vo.setProductName(po.getFinancialProduct().getProductName());
			vo.setProductTerm(po.getFinancialProduct().getProductTerm());
			vo.setIncomeType(po.getFinancialProduct().getIncomeType());
			vo.setIncomeTypeName(DictHolder.getDictItemName("incomeType", vo.getIncomeType()));
		}
		if (po.getAccount() != null) {
			vo.setUserName(po.getAccount().getUserName());
		}
		return vo;
	}

}
