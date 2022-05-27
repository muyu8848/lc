package com.lc.common.valid;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import com.lc.common.exception.BizError;
import com.lc.common.exception.PermissionValidException;
import com.lc.constants.Constant;
import com.lc.rbac.service.RbacService;
import com.lc.rbac.vo.BackgroundAccountUserDetails;
import com.lc.rbac.vo.MenuVO;

@Aspect
@Component
public class PermissionValidAspect {

	@Autowired
	private RbacService rbacService;

	@Pointcut("@annotation(com.lc.common.valid.PermissionValid)")
	public void permissionValidAspect() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before("permissionValidAspect()")
	public void before(JoinPoint joinPoint) {
		String url = "";
		try {
			Class clazz = joinPoint.getTarget().getClass();
			String methodName = joinPoint.getSignature().getName();
			Method method = clazz.getMethod(methodName);
			if (method.getAnnotation(GetMapping.class) != null) {
				String[] urls = method.getAnnotation(GetMapping.class).value();
				url = urls[0];
				String[] tmpUrls = method.getAnnotation(PermissionValid.class).value();
				if (tmpUrls.length > 0) {
					url = tmpUrls[0];
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		BackgroundAccountUserDetails user = (BackgroundAccountUserDetails) principal;
		permissionValid(url, rbacService.findMenuTreeByAccountId(user.getAccountId()));
	}

	public void permissionValid(String url, List<MenuVO> menuTrees) {
		Map<String, String> urlMap = new HashMap<>();
		for (MenuVO menu1 : menuTrees) {
			urlMap.put(menu1.getUrl(), menu1.getUrl());
			for (MenuVO subMenu : menu1.getSubMenus()) {
				if (Constant.菜单类型_二级菜单.equals(subMenu.getType())) {
				}
				urlMap.put(subMenu.getUrl(), subMenu.getUrl());
			}
		}
		if (urlMap.get(url) == null) {
			throw new PermissionValidException(BizError.无权访问);
		}
	}

}
