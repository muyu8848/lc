package com.lc.rbac.param;

import java.util.List;

import lombok.Data;

@Data
public class AssignMenuParam {
	
	private String roleId;
	
	private List<String> menuIds;

}
