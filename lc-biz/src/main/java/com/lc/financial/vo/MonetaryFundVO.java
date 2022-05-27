package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.MonetaryFund;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MonetaryFundVO {

	private String id;

	private String fundName;

	private String fundCode;

	private String fundCategory;

	private String operationMode;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date establishedTime;

	private String fundSize;

	private String fundCustody;

	private String issuingAgency;

	private String fundIntro;

	public static List<MonetaryFundVO> convertFor(List<MonetaryFund> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MonetaryFundVO> vos = new ArrayList<>();
		for (MonetaryFund po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MonetaryFundVO convertFor(MonetaryFund po) {
		MonetaryFundVO vo = new MonetaryFundVO();
		if (po == null) {
			return vo;
		}
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
