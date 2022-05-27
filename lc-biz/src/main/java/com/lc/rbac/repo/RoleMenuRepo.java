package com.lc.rbac.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.rbac.domain.RoleMenu;

public interface RoleMenuRepo extends JpaRepository<RoleMenu, String>, JpaSpecificationExecutor<RoleMenu> {

	List<RoleMenu> findByRoleIdIn(List<String> roleIds);

}
