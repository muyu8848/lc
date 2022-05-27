package com.lc.log.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.lc.common.utils.IdUtils;
import com.lc.constants.Constant;

import cn.hutool.http.useragent.UserAgent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "login_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class LoginLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String sessionId;

	private String loginSystem;

	private String state;

	private String ipAddr;

	private Date loginTime;

	private String browser;

	private String os;

	private String msg;

	private String userName;

	public static LoginLog buildSuccessLog(String sessionId, String userName, String loginSystem, String ipAddr,
			UserAgent userAgent) {
		LoginLog loginLog = new LoginLog();
		loginLog.setId(IdUtils.getId());
		loginLog.setSessionId(sessionId);
		loginLog.setUserName(userName);
		loginLog.setLoginSystem(loginSystem);
		loginLog.setState(Constant.登录状态_成功);
		loginLog.setMsg(Constant.登录消息_登录成功);
		loginLog.setIpAddr(ipAddr);
		loginLog.setLoginTime(new Date());
		loginLog.setBrowser(userAgent.getBrowser().getName());
		loginLog.setOs(userAgent.getOs().getName());
		return loginLog;
	}

	public static LoginLog buildFailLog(String userName, String loginSystem, String ipAddr, UserAgent userAgent) {
		LoginLog loginLog = new LoginLog();
		loginLog.setId(IdUtils.getId());
		loginLog.setUserName(userName);
		loginLog.setLoginSystem(loginSystem);
		loginLog.setState(Constant.登录状态_失败);
		loginLog.setIpAddr(ipAddr);
		loginLog.setLoginTime(new Date());
		loginLog.setBrowser(userAgent.getBrowser().getName());
		loginLog.setOs(userAgent.getOs().getName());
		return loginLog;
	}

}
