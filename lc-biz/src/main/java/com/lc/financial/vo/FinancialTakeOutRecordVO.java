package com.lc.financial.vo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.FinancialTakeOutRecord;

import lombok.Data;

@Data
public class FinancialTakeOutRecordVO {

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double amount;

	private Double quota;

	private Double npv;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String financialProductId;

	private String productName;

	private String incomeType;

	public static FinancialTakeOutRecordVO convertFor(FinancialTakeOutRecord po) {
		if (po == null) {
			return null;
		}
		FinancialTakeOutRecordVO vo = new FinancialTakeOutRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getFinancialProduct() != null) {
			vo.setProductName(po.getFinancialProduct().getProductName());
			vo.setIncomeType(po.getFinancialProduct().getIncomeType());
		}
		return vo;
	}

}
