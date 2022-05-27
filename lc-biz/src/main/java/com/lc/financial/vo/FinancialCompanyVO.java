package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.FinancialCompany;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FinancialCompanyVO {

	private String id;

	private String companyName;

	private String companyAddress;

	private String weChatOfficialAccount;

	private String contactNumber;

	private String websiteUrl;

	private String basicInformation;

	private String investmentCapacity;

	private String ownershipStructure;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static List<FinancialCompanyVO> convertFor(List<FinancialCompany> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FinancialCompanyVO> vos = new ArrayList<>();
		for (FinancialCompany po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FinancialCompanyVO convertFor(FinancialCompany po) {
		if (po == null) {
			return null;
		}
		FinancialCompanyVO vo = new FinancialCompanyVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
