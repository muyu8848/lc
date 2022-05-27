package com.lc.memberaccount.vo;

import org.springframework.beans.BeanUtils;

import com.lc.memberaccount.domain.MemberAccount;

import lombok.Data;

@Data
public class BalanceVO {

	private Double balance;

	public static BalanceVO convertFor(MemberAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		BalanceVO vo = new BalanceVO();
		BeanUtils.copyProperties(userAccount, vo);
		return vo;
	}

}
