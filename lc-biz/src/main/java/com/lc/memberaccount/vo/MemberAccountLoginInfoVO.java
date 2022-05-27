package com.lc.memberaccount.vo;

import org.springframework.beans.BeanUtils;

import com.lc.memberaccount.domain.MemberAccount;

import lombok.Data;

@Data
public class MemberAccountLoginInfoVO {

	private String id;

	private String userName;

	private String loginPwd;

	private String state;

	public static MemberAccountLoginInfoVO convertFor(MemberAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		MemberAccountLoginInfoVO vo = new MemberAccountLoginInfoVO();
		BeanUtils.copyProperties(userAccount, vo);
		return vo;
	}

}
