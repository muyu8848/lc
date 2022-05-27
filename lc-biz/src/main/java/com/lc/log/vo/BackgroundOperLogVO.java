package com.lc.log.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.log.domain.BackgroundOperLog;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class BackgroundOperLogVO {

	private String id;

	private String module;

	private String operate;

	private String requestMethod;
	
	private String requestUrl;

	private String requestParam;

	private String ipAddr;

	private String operAccountId;

	private String operAccountUserName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date operTime;

	public static List<BackgroundOperLogVO> convertFor(List<BackgroundOperLog> operLogs) {
		if (CollectionUtil.isEmpty(operLogs)) {
			return new ArrayList<>();
		}
		List<BackgroundOperLogVO> vos = new ArrayList<>();
		for (BackgroundOperLog operLog : operLogs) {
			vos.add(convertFor(operLog));
		}
		return vos;
	}

	public static BackgroundOperLogVO convertFor(BackgroundOperLog operLog) {
		if (operLog == null) {
			return null;
		}
		BackgroundOperLogVO vo = new BackgroundOperLogVO();
		BeanUtils.copyProperties(operLog, vo);
		return vo;
	}

}
