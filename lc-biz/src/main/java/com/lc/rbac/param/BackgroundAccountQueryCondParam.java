package com.lc.rbac.param;

import com.lc.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BackgroundAccountQueryCondParam extends PageParam {
	
	private String userName;

}
