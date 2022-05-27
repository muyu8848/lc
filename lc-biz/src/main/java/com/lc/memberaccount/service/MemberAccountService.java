package com.lc.memberaccount.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
import com.lc.memberaccount.domain.MemberAccount;
import com.lc.memberaccount.param.AddMemberAccountParam;
import com.lc.memberaccount.param.MemberAccountEditParam;
import com.lc.memberaccount.param.MemberAccountQueryCondParam;
import com.lc.memberaccount.param.MemberAccountRegisterParam;
import com.lc.memberaccount.repo.MemberAccountRepo;
import com.lc.memberaccount.vo.BalanceVO;
import com.lc.memberaccount.vo.MemberAccountLoginInfoVO;
import com.lc.memberaccount.vo.MemberAccountVO;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class MemberAccountService {

	@Autowired
	private MemberAccountRepo accountRepo;

	@ParamValid
	@Transactional
	public void addBalance(@NotBlank String id,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double balance) {
		MemberAccount account = accountRepo.getById(id);
		double balanceAfter = NumberUtil.round(account.getBalance() + balance, 2).doubleValue();
		account.setBalance(balanceAfter);
		accountRepo.save(account);
	}

	@ParamValid
	@Transactional
	public void reduceBalance(@NotBlank String id,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double balance) {
		MemberAccount account = accountRepo.getById(id);
		double balanceAfter = NumberUtil.round(account.getBalance() - balance, 2).doubleValue();
		if (balanceAfter < 0) {
			throw new BizException("余额不能少于0");
		}
		account.setBalance(balanceAfter);
		accountRepo.save(account);
	}

	@Transactional
	public void updateLatelyLoginTime(String accountId) {
		MemberAccount userAccount = accountRepo.getById(accountId);
		userAccount.setLatelyLoginTime(new Date());
		accountRepo.save(userAccount);
	}

	@ParamValid
	@Transactional
	public void updateAccount(MemberAccountEditParam param) {
		MemberAccount existAccount = accountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existAccount != null && !existAccount.getId().equals(param.getId())) {
			throw new BizException("账号已存在");
		}
		MemberAccount account = accountRepo.getById(param.getId());
		BeanUtils.copyProperties(param, account);
		accountRepo.save(account);
	}

	@Transactional(readOnly = true)
	public MemberAccountVO findAccountById(String id) {
		MemberAccount account = accountRepo.getById(id);
		return MemberAccountVO.convertFor(account);
	}

	public Specification<MemberAccount> buildAccountQueryCond(MemberAccountQueryCondParam param) {
		Specification<MemberAccount> spec = new Specification<MemberAccount>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MemberAccount> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(builder.like(root.get("userName"), "%" + param.getUserName() + "%"));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public PageResult<MemberAccountVO> findAccountByPage(MemberAccountQueryCondParam param) {
		Specification<MemberAccount> spec = buildAccountQueryCond(param);
		Page<MemberAccount> result = accountRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("registeredTime"))));
		PageResult<MemberAccountVO> pageResult = new PageResult<>(MemberAccountVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void modifyLoginPwd(@NotBlank String id, @NotBlank String newLoginPwd) {
		MemberAccount account = accountRepo.getById(id);
		account.setLoginPwd(new BCryptPasswordEncoder().encode(newLoginPwd));
		accountRepo.save(account);
	}

	@Transactional(readOnly = true)
	public MemberAccountLoginInfoVO getLoginAccountInfo(String userName) {
		return MemberAccountLoginInfoVO.convertFor(accountRepo.findByUserNameAndDeletedFlagIsFalse(userName));
	}

	@Transactional(readOnly = true)
	public BalanceVO getBalance(String userAccountId) {
		return BalanceVO.convertFor(accountRepo.getById(userAccountId));
	}

	@ParamValid
	@Transactional
	public void addAccount(AddMemberAccountParam param) {
		MemberAccount existAccount = accountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existAccount != null) {
			throw new BizException("账号已存在");
		}
		String encodePwd = new BCryptPasswordEncoder().encode(param.getLoginPwd());
		param.setLoginPwd(encodePwd);
		MemberAccount newAccount = param.convertToPo();
		accountRepo.save(newAccount);
	}

	@Lock(keys = "'userName_' + #param.userName")
	@ParamValid
	@Transactional
	public void register(MemberAccountRegisterParam param) {
		MemberAccount existAccount = accountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existAccount != null) {
			throw new BizException("账号已存在");
		}
		param.setLoginPwd(new BCryptPasswordEncoder().encode(param.getLoginPwd()));
		MemberAccount newUserAccount = param.convertToPo();
		accountRepo.save(newUserAccount);
	}

	@Transactional
	public void delAccount(@NotBlank String id) {
		MemberAccount account = accountRepo.getById(id);
		account.deleted();
		accountRepo.save(account);
	}

}
