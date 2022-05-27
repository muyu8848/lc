package com.lc.financial.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.lc.common.valid.ParamValid;
import com.lc.financial.domain.MonetaryFund;
import com.lc.financial.param.AddOrUpdateMonetaryFundParam;
import com.lc.financial.repo.MonetaryFundRepo;
import com.lc.financial.vo.MonetaryFundVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class MonetaryFundService {

	@Autowired
	private MonetaryFundRepo monetaryFundRepo;

	@Transactional(readOnly = true)
	public MonetaryFundVO findMonetaryFund(String id) {
		return MonetaryFundVO.convertFor(monetaryFundRepo.getById(id));
	}

	@Transactional(readOnly = true)
	public List<MonetaryFundVO> findAllMonetaryFund() {
		return MonetaryFundVO.convertFor(monetaryFundRepo.findByDeletedFlagIsFalseOrderByCreateTimeDesc());
	}
	
	@Transactional
	public void delMonetaryFund(@NotBlank String id) {
		MonetaryFund monetaryFund = monetaryFundRepo.getById(id);
		monetaryFund.deleted();
		monetaryFundRepo.save(monetaryFund);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateMonetaryFund(AddOrUpdateMonetaryFundParam param) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			MonetaryFund monetaryFund = param.convertToPo();
			monetaryFundRepo.save(monetaryFund);
		}
		// 修改
		else {
			MonetaryFund monetaryFund = monetaryFundRepo.getById(param.getId());
			BeanUtils.copyProperties(param, monetaryFund);
			monetaryFundRepo.save(monetaryFund);
		}
	}

}
