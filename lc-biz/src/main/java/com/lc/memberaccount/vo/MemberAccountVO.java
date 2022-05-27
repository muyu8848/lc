package com.lc.memberaccount.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lc.dictconfig.DictHolder;
import com.lc.memberaccount.domain.MemberAccount;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MemberAccountVO {

	private String id;

	private String userName;

	private String state;

	private String stateName;

	private Double balance;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	public static List<MemberAccountVO> convertFor(List<MemberAccount> userAccounts) {
		if (CollectionUtil.isEmpty(userAccounts)) {
			return new ArrayList<>();
		}
		List<MemberAccountVO> vos = new ArrayList<>();
		for (MemberAccount userAccount : userAccounts) {
			vos.add(convertFor(userAccount));
		}
		return vos;
	}

	public static MemberAccountVO convertFor(MemberAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		MemberAccountVO vo = new MemberAccountVO();
		BeanUtils.copyProperties(userAccount, vo);
		vo.setStateName(DictHolder.getDictItemName("accountState", vo.getState()));
		return vo;
	}

}
