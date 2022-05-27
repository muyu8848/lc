package com.lc.financial.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.financial.domain.FundManagerAchievement;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FundManagerAchievementVO {
	
	private String id;
	
	private String fundName;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date startManageTime;

	private Double rateOfReturn;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastModifyTime;
	
	public static List<FundManagerAchievementVO> convertFor(List<FundManagerAchievement> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FundManagerAchievementVO> vos = new ArrayList<>();
		for (FundManagerAchievement po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FundManagerAchievementVO convertFor(FundManagerAchievement po) {
		if (po == null) {
			return null;
		}
		FundManagerAchievementVO vo = new FundManagerAchievementVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
