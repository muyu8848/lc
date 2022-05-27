package com.lc.memberaccount.param;

import com.lc.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MemberAccountQueryCondParam extends PageParam {
	
	private String userName;

}
