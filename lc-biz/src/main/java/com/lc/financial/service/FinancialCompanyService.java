package com.lc.financial.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.lc.financial.domain.FinancialCompany;
import com.lc.financial.param.AddOrUpdateFinancialCompanyParam;
import com.lc.financial.repo.FinancialCompanyRepo;
import com.lc.financial.vo.FinancialCompanyVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class FinancialCompanyService {

	@Autowired
	private FinancialCompanyRepo financialCompanyRepo;

	@Transactional(readOnly = true)
	public List<FinancialCompanyVO> findAllFinancialCompany() {
		return FinancialCompanyVO.convertFor(financialCompanyRepo.findByDeletedFlagIsFalseOrderByCreateTimeDesc());
	}

	@Transactional(readOnly = true)
	public FinancialCompanyVO findFinancialCompany(@NotBlank String id) {
		return FinancialCompanyVO.convertFor(financialCompanyRepo.getById(id));
	}

	@Transactional
	public void addOrUpdateFinancialCompany(AddOrUpdateFinancialCompanyParam param) {
		if (StrUtil.isBlank(param.getId())) {
			FinancialCompany financialCompany = param.convertToPo();
			financialCompanyRepo.save(financialCompany);
		} else {
			FinancialCompany financialCompany = financialCompanyRepo.getById(param.getId());
			BeanUtils.copyProperties(param, financialCompany);
			financialCompanyRepo.save(financialCompany);
		}
	}

	@Transactional
	public void delFinancialCompany(@NotBlank String id) {
		FinancialCompany financialCompany = financialCompanyRepo.getById(id);
		financialCompany.deleted();
		financialCompanyRepo.save(financialCompany);
	}

}
