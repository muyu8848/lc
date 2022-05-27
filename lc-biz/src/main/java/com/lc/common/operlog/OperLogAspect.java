package com.lc.common.operlog;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.lc.common.utils.IdUtils;
import com.lc.log.domain.BackgroundOperLog;
import com.lc.log.service.OperLogService;
import com.lc.rbac.vo.BackgroundAccountUserDetails;

import cn.hutool.http.HttpUtil;

@Aspect
@Component
public class OperLogAspect {

	@Autowired
	private OperLogService operLogService;

	@Pointcut("@annotation(com.lc.common.operlog.OperLog)")
	public void operLogAspect() {
	}

	@AfterReturning(pointcut = "operLogAspect()", returning = "result")
	public void doAfterReturning(JoinPoint joinPoint, Object result) {
		recordOperLog(joinPoint, null, result);
	}

	@AfterThrowing(value = "operLogAspect()", throwing = "e")
	public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
		recordOperLog(joinPoint, e, null);
	}

	public void recordOperLog(JoinPoint joinPoint, Exception e, Object result) {
		OperLog annotation = getOperLogAnnotation(joinPoint);
		if (annotation == null) {
			return;
		}
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String operAccountId = null;
		String operAccountUserName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof BackgroundAccountUserDetails) {
			BackgroundAccountUserDetails user = (BackgroundAccountUserDetails) principal;
			operAccountId = user.getAccountId();
			operAccountUserName = user.getUsername();
		}

		BackgroundOperLog operLog = new BackgroundOperLog();
		operLog.setId(IdUtils.getId());
		operLog.setModule(annotation.module());
		operLog.setOperate(annotation.operate());
		operLog.setRequestMethod(request.getMethod());
		operLog.setRequestUrl(request.getRequestURL().toString());
		operLog.setRequestParam(JSON.toJSONString(joinPoint.getArgs()));
		operLog.setIpAddr(HttpUtil.getClientIP(request));
		operLog.setOperAccountId(operAccountId);
		operLog.setOperAccountUserName(operAccountUserName);
		operLog.setOperTime(new Date());
		operLogService.recordOperLog(operLog);
	}

	private OperLog getOperLogAnnotation(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method != null) {
			return method.getAnnotation(OperLog.class);
		}
		return null;
	}

}
