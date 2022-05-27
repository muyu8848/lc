package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.dictconfig.DictHolder;
import com.lc.financial.domain.FinancialProduct;
import com.lc.financial.domain.TransactionDetail;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class TransactionDetailVO {

	private String id;

	private Double amount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String type;

	private String typeName;

	private String financialProductId;

	private String productFullName;

	private String buyInRecordId;
	
	private String takeOutRecordId;

	public static List<TransactionDetailVO> convertFor(List<TransactionDetail> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<TransactionDetailVO> vos = new ArrayList<>();
		for (TransactionDetail po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static TransactionDetailVO convertFor(TransactionDetail po) {
		if (po == null) {
			return null;
		}
		TransactionDetailVO vo = new TransactionDetailVO();
		BeanUtils.copyProperties(po, vo);
		vo.setTypeName(DictHolder.getDictItemName("transactionDetailType", vo.getType()));
		if (po.getFinancialProduct() != null) {
			FinancialProduct financialProduct = po.getFinancialProduct();
			vo.setProductFullName(financialProduct.getProductFullName());
		}
		return vo;
	}

}
