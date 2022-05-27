package com.lc.rbac.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alicp.jetcache.anno.Cached;
import com.lc.common.valid.ParamValid;
import com.lc.constants.Constant;
import com.lc.rbac.domain.AccountRole;
import com.lc.rbac.domain.Menu;
import com.lc.rbac.domain.Role;
import com.lc.rbac.domain.RoleMenu;
import com.lc.rbac.param.AssignMenuParam;
import com.lc.rbac.param.AssignRoleParam;
import com.lc.rbac.param.MenuParam;
import com.lc.rbac.param.RoleParam;
import com.lc.rbac.repo.AccountRoleRepo;
import com.lc.rbac.repo.MenuRepo;
import com.lc.rbac.repo.RoleMenuRepo;
import com.lc.rbac.repo.RoleRepo;
import com.lc.rbac.vo.MenuVO;
import com.lc.rbac.vo.RoleVO;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class RbacService {

	@Autowired
	private MenuRepo menuRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private RoleMenuRepo roleMenuRepo;

	@Autowired
	private AccountRoleRepo accountRoleRepo;

	@Cached(name = "findMenuTreeByAccountId_", key = "args[0]", expire = 3600)
	@Transactional(readOnly = true)
	public List<MenuVO> findMenuTreeByAccountId(String accountId) {
		List<String> roleIds = new ArrayList<>();
		List<AccountRole> accountRoles = accountRoleRepo.findByAccountIdAndRoleDeletedFlagFalse(accountId);
		if (CollectionUtil.isEmpty(accountRoles)) {
			return findMenuTree();
		}
		for (AccountRole accountRole : accountRoles) {
			roleIds.add(accountRole.getRoleId());
		}
		List<String> menuIds = new ArrayList<>();
		List<RoleMenu> roleMenus = roleMenuRepo.findByRoleIdIn(roleIds);
		for (RoleMenu roleMenu : roleMenus) {
			menuIds.add(roleMenu.getMenuId());
		}
		List<Menu> menus = menuRepo.findByIdInAndDeletedFlagFalseOrderByOrderNo(menuIds);
		return buildMenuTree(MenuVO.convertFor(menus));
	}

	@ParamValid
	@Transactional
	public void assignRole(AssignRoleParam param) {
		List<AccountRole> assignRoles = accountRoleRepo.findByAccountId(param.getAccountId());
		accountRoleRepo.deleteAll(assignRoles);
		for (String roleId : param.getRoleIds()) {
			accountRoleRepo.save(AccountRole.build(param.getAccountId(), roleId));
		}
	}

	@Transactional(readOnly = true)
	public List<RoleVO> findRoleByAccountId(String accountId) {
		List<RoleVO> roleVOs = new ArrayList<>();
		List<AccountRole> accountRoles = accountRoleRepo.findByAccountId(accountId);
		for (AccountRole accountRole : accountRoles) {
			roleVOs.add(RoleVO.convertFor(accountRole.getRole()));
		}
		return roleVOs;
	}

	@ParamValid
	@Transactional
	public void assignMenu(AssignMenuParam param) {
		List<RoleMenu> roleMenus = roleMenuRepo.findByRoleIdIn(Arrays.asList(param.getRoleId()));
		roleMenuRepo.deleteAll(roleMenus);
		for (String menuId : param.getMenuIds()) {
			roleMenuRepo.save(RoleMenu.build(param.getRoleId(), menuId));
		}
	}

	@Transactional(readOnly = true)
	public List<MenuVO> findMenuByRoleId(String roleId) {
		List<MenuVO> menuVOs = new ArrayList<>();
		List<RoleMenu> roleMenus = roleMenuRepo.findByRoleIdIn(Arrays.asList(roleId));
		for (RoleMenu roleMenu : roleMenus) {
			menuVOs.add(MenuVO.convertFor(roleMenu.getMenu()));
		}
		return menuVOs;
	}

	@Transactional(readOnly = true)
	public List<RoleVO> findAllRole() {
		return RoleVO.convertFor(roleRepo.findByDeletedFlagFalseOrderByCreateTimeDesc());
	}

	@Transactional(readOnly = true)
	public RoleVO findRoleById(@NotBlank String id) {
		return RoleVO.convertFor(roleRepo.getById(id));
	}

	@Transactional
	public void delRole(@NotBlank String id) {
		Role role = roleRepo.getById(id);
		role.deleted();
		roleRepo.save(role);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateRole(RoleParam param) {
		if (StrUtil.isBlank(param.getId())) {
			Role role = param.convertToPo();
			roleRepo.save(role);
		} else {
			Role role = roleRepo.getById(param.getId());
			BeanUtils.copyProperties(param, role);
			roleRepo.save(role);
		}
	}

	@Transactional(readOnly = true)
	public MenuVO findMenuById(@NotBlank String id) {
		return MenuVO.convertFor(menuRepo.getById(id));
	}

	@Transactional
	public void delMenu(@NotBlank String id) {
		List<Menu> subMenus = menuRepo.findByParentIdAndDeletedFlagFalse(id);
		for (Menu subMenu : subMenus) {
			List<Menu> btns = menuRepo.findByParentIdAndDeletedFlagFalse(subMenu.getId());
			for (Menu btn : btns) {
				btn.deleted();
				menuRepo.save(btn);
			}
		}
		if (CollectionUtil.isNotEmpty(subMenus)) {
			for (Menu subMenu : subMenus) {
				subMenu.deleted();
				menuRepo.save(subMenu);
			}
		}
		Menu menu = menuRepo.getById(id);
		menu.deleted();
		menuRepo.save(menu);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateMenu(MenuParam param) {
		if (StrUtil.isBlank(param.getId())) {
			Menu menu = param.convertToPo();
			menuRepo.save(menu);
		} else {
			Menu menu = menuRepo.getById(param.getId());
			BeanUtils.copyProperties(param, menu);
			menuRepo.save(menu);
		}
	}

	@Transactional(readOnly = true)
	public List<MenuVO> findMenuTree() {
		List<Menu> menus = menuRepo.findByDeletedFlagFalseOrderByOrderNo();
		List<MenuVO> menuVOs = MenuVO.convertFor(menus);
		return buildMenuTree(menuVOs);
	}

	public List<MenuVO> buildMenuTree(List<MenuVO> menuVOs) {
		List<MenuVO> menu1s = new ArrayList<>();
		List<MenuVO> menu2s = new ArrayList<>();
		for (MenuVO m : menuVOs) {
			if (Constant.菜单类型_主菜单.equals(m.getType())) {
				menu1s.add(m);
			}
			if (Constant.菜单类型_二级菜单.equals(m.getType())) {
				menu2s.add(m);
			}
		}
		for (MenuVO menu1 : menu1s) {
			for (MenuVO menu2 : menu2s) {
				if (menu1.getId().equals(menu2.getParentId())) {
					menu1.getSubMenus().add(menu2);
				}
			}
		}
		return menu1s;

	}

}
