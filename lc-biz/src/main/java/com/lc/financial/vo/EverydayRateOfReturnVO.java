package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.EverydayRateOfReturn;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class EverydayRateOfReturnVO {

	private String id;

	private Double rateOfReturn;

	private Double tenThousandIncome = 0d;

	private Double yearRateOfReturn = 0d;

	private Double chg;

	private Double npv;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date theDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static List<EverydayRateOfReturnVO> convertFor(List<EverydayRateOfReturn> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<EverydayRateOfReturnVO> vos = new ArrayList<>();
		for (EverydayRateOfReturn po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static EverydayRateOfReturnVO convertFor(EverydayRateOfReturn po) {
		if (po == null) {
			return null;
		}
		EverydayRateOfReturnVO vo = new EverydayRateOfReturnVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
