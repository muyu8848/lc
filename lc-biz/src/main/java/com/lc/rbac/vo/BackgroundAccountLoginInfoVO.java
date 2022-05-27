package com.lc.rbac.vo;

import org.springframework.beans.BeanUtils;

import com.lc.rbac.domain.BackgroundAccount;

import lombok.Data;

@Data
public class BackgroundAccountLoginInfoVO {

	private String id;

	private String userName;

	private String loginPwd;

	private String state;

	public static BackgroundAccountLoginInfoVO convertFor(BackgroundAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		BackgroundAccountLoginInfoVO vo = new BackgroundAccountLoginInfoVO();
		BeanUtils.copyProperties(userAccount, vo);
		return vo;
	}

}
