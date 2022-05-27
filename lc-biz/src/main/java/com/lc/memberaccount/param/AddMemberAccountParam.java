package com.lc.memberaccount.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.lc.common.utils.IdUtils;
import com.lc.constants.Constant;
import com.lc.memberaccount.domain.MemberAccount;

import lombok.Data;

@Data
public class AddMemberAccountParam {

	@NotBlank
	private String userName;

	private String accountType;
	
	@NotBlank
	private String loginPwd;

	public MemberAccount convertToPo() {
		MemberAccount po = new MemberAccount();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setState(Constant.账号状态_启用);
		po.setDeletedFlag(false);
		po.setRegisteredTime(new Date());
		po.setBalance(20000d);
		return po;
	}

}
