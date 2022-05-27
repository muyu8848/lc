package com.lc.rbac.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.lc.common.exception.BizException;
import com.lc.common.valid.ParamValid;
import com.lc.common.vo.PageResult;
import com.lc.rbac.domain.BackgroundAccount;
import com.lc.rbac.param.AddBackgroundAccountParam;
import com.lc.rbac.param.BackgroundAccountEditParam;
import com.lc.rbac.param.BackgroundAccountQueryCondParam;
import com.lc.rbac.repo.BackgroundAccountRepo;
import com.lc.rbac.vo.BackgroundAccountLoginInfoVO;
import com.lc.rbac.vo.BackgroundAccountVO;
import com.lc.rbac.vo.SuperAdminVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class BackgroundAccountService {

	@Autowired
	private BackgroundAccountRepo backgroundAccountRepo;

	@Transactional
	public void modifySuperAdminLoginPwd(@NotBlank String id, @NotBlank String newLoginPwd,
			@NotBlank String operatorId) {
		BackgroundAccount superAdmin = backgroundAccountRepo.getById(operatorId);
		if (!superAdmin.getSuperAdminFlag()) {
			throw new BizException("无权修改超级管理员的密码");
		}
		BackgroundAccount account = backgroundAccountRepo.getById(id);
		account.setLoginPwd(new BCryptPasswordEncoder().encode(newLoginPwd));
		backgroundAccountRepo.save(account);
	}

	@Transactional(readOnly = true)
	public List<SuperAdminVO> findSuperAdmin() {
		return SuperAdminVO.convertFor(backgroundAccountRepo.findBySuperAdminFlagTrueAndDeletedFlagIsFalse());
	}

	@Transactional
	public void updateLatelyLoginTime(String id) {
		BackgroundAccount account = backgroundAccountRepo.getById(id);
		account.setLatelyLoginTime(new Date());
		backgroundAccountRepo.save(account);
	}

	@ParamValid
	@Transactional
	public void updateAccount(BackgroundAccountEditParam param) {
		BackgroundAccount existAccount = backgroundAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existAccount != null && !existAccount.getId().equals(param.getId())) {
			throw new BizException("账号已存在");
		}
		if (existAccount != null && existAccount.getSuperAdminFlag()) {
			throw new BizException("无权操作超级管理员");
		}
		BackgroundAccount account = backgroundAccountRepo.getById(param.getId());
		BeanUtils.copyProperties(param, account);
		backgroundAccountRepo.save(account);
	}

	@Transactional(readOnly = true)
	public PageResult<BackgroundAccountVO> findAccountByPage(BackgroundAccountQueryCondParam param) {
		Specification<BackgroundAccount> spec = new Specification<BackgroundAccount>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<BackgroundAccount> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				predicates.add(builder.equal(root.get("superAdminFlag"), false));
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(builder.like(root.get("userName"), "%" + param.getUserName() + "%"));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<BackgroundAccount> result = backgroundAccountRepo.findAll(spec, PageRequest.of(param.getPageNum() - 1,
				param.getPageSize(), Sort.by(Sort.Order.desc("registeredTime"))));
		PageResult<BackgroundAccountVO> pageResult = new PageResult<>(
				BackgroundAccountVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void modifyLoginPwd(@NotBlank String id, @NotBlank String newLoginPwd) {
		BackgroundAccount account = backgroundAccountRepo.getById(id);
		if (account.getSuperAdminFlag()) {
			throw new BizException("无权操作超级管理员");
		}
		account.setLoginPwd(new BCryptPasswordEncoder().encode(newLoginPwd));
		backgroundAccountRepo.save(account);
	}

	@Transactional(readOnly = true)
	public BackgroundAccountLoginInfoVO getAccountLoginInfo(String userName) {
		return BackgroundAccountLoginInfoVO
				.convertFor(backgroundAccountRepo.findByUserNameAndDeletedFlagIsFalse(userName));
	}

	@Transactional(readOnly = true)
	public BackgroundAccountVO findAccountById(String id) {
		return BackgroundAccountVO.convertFor(backgroundAccountRepo.getById(id));
	}

	@ParamValid
	@Transactional
	public void addAccount(AddBackgroundAccountParam param) {
		BackgroundAccount existAccount = backgroundAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existAccount != null) {
			throw new BizException("账号已存在");
		}
		String encodePwd = new BCryptPasswordEncoder().encode(param.getLoginPwd());
		param.setLoginPwd(encodePwd);
		BackgroundAccount newAccount = param.convertToPo();
		backgroundAccountRepo.save(newAccount);
	}

	@Transactional
	public void delAccount(@NotBlank String id) {
		BackgroundAccount account = backgroundAccountRepo.getById(id);
		if (account.getSuperAdminFlag()) {
			throw new BizException("无权操作超级管理员");
		}
		account.deleted();
		backgroundAccountRepo.save(account);
	}

}
