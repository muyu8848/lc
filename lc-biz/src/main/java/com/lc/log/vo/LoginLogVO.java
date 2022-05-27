package com.lc.log.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.dictconfig.DictHolder;
import com.lc.log.domain.LoginLog;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class LoginLogVO {

	private String id;

	private String sessionId;

	private String loginSystem;

	private String state;

	private String stateName;

	private String ipAddr;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date loginTime;

	private String browser;

	private String os;

	private String msg;

	private String userName;

	public static List<LoginLogVO> convertFor(List<LoginLog> loginLogs) {
		if (CollectionUtil.isEmpty(loginLogs)) {
			return new ArrayList<>();
		}
		List<LoginLogVO> vos = new ArrayList<>();
		for (LoginLog loginLog : loginLogs) {
			vos.add(convertFor(loginLog));
		}
		return vos;
	}

	public static LoginLogVO convertFor(LoginLog loginLog) {
		if (loginLog == null) {
			return null;
		}
		LoginLogVO vo = new LoginLogVO();
		BeanUtils.copyProperties(loginLog, vo);
		vo.setStateName(DictHolder.getDictItemName("loginState", vo.getState()));
		return vo;
	}

}
