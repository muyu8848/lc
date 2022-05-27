package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.FinancialProductNotice;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FinancialProductNoticeVO {

	private String id;

	private String title;

	private String link;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastModifyTime;

	public static List<FinancialProductNoticeVO> convertFor(List<FinancialProductNotice> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FinancialProductNoticeVO> vos = new ArrayList<>();
		for (FinancialProductNotice po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FinancialProductNoticeVO convertFor(FinancialProductNotice po) {
		if (po == null) {
			return null;
		}
		FinancialProductNoticeVO vo = new FinancialProductNoticeVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
