package com.lc.memberaccount.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ModifyLoginPwdParam {

	@NotBlank
	private String oldLoginPwd;

	@NotBlank
	private String newLoginPwd;

	@NotBlank
	private String userAccountId;

}
