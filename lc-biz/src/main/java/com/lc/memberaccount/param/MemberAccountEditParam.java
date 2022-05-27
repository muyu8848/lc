package com.lc.memberaccount.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class MemberAccountEditParam {

	@NotBlank
	private String id;

	@NotBlank
	private String userName;

	@NotBlank
	private String state;

}
