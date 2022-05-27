package com.lc.rbac.param;

import java.util.List;

import lombok.Data;

@Data
public class AssignRoleParam {

	private String accountId;

	private List<String> roleIds;

}
