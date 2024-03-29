package com.lc.common.exception;

import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import com.lc.common.vo.Result;
import com.zengtengpeng.excepiton.LockException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(PermissionValidException.class)
	public ModelAndView handlePermissionValidException(PermissionValidException e) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("permission-fail");
		return mv;
	}

	@ExceptionHandler(LockException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public Result handleLockException(LockException e) {
		String msg = "lock exception";
		if (e != null) {
			msg = e.getMessage();
			log.warn(e.toString());
		}
		return Result.fail(msg);
	}

	@ExceptionHandler(BizException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public Result handleBizException(BizException e) {
		String msg = "biz exception";
		if (e != null) {
			msg = e.getMsg();
			log.warn(e.toString());
		}
		return Result.fail(msg);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public Result handleConstraintViolationException(ConstraintViolationException e) {
		String msg = "param valid exception";
		if (e != null) {
			Iterator<ConstraintViolation<?>> iterator = e.getConstraintViolations().iterator();
			if (iterator.hasNext()) {
				ConstraintViolation<?> violation = iterator.next();
				msg = violation.getPropertyPath() + ":" + violation.getMessage();
			}
			log.warn(e.toString());
		}
		return Result.fail(msg);
	}

	@ExceptionHandler(ParamValidException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public Result handleParamValidException(ParamValidException e) {
		String msg = "param valid exception";
		if (e != null) {
			msg = e.getMsg();
			log.warn(e.toString());
		}
		return Result.fail(msg);
	}
}
