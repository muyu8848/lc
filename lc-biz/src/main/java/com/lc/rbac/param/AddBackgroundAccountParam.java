package com.lc.rbac.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.lc.common.utils.IdUtils;
import com.lc.constants.Constant;
import com.lc.rbac.domain.BackgroundAccount;

import lombok.Data;

@Data
public class AddBackgroundAccountParam {

	@NotBlank
	private String userName;

	@NotBlank
	private String loginPwd;

	public BackgroundAccount convertToPo() {
		BackgroundAccount po = new BackgroundAccount();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setState(Constant.账号状态_启用);
		po.setDeletedFlag(false);
		po.setRegisteredTime(new Date());
		po.setSuperAdminFlag(false);
		return po;
	}

}
