package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.FundManager;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FundManagerVO {
	
	private String id;
	
	private String fullName;

	private String headPath;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date practiceTime;

	private String intro;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastModifyTime;
	
	public static List<FundManagerVO> convertFor(List<FundManager> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FundManagerVO> vos = new ArrayList<>();
		for (FundManager po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FundManagerVO convertFor(FundManager po) {
		if (po == null) {
			return null;
		}
		FundManagerVO vo = new FundManagerVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
