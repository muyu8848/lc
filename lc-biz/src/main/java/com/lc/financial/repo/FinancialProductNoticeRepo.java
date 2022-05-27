package com.lc.financial.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FinancialProductNotice;

public interface FinancialProductNoticeRepo
		extends JpaRepository<FinancialProductNotice, String>, JpaSpecificationExecutor<FinancialProductNotice> {
	
	List<FinancialProductNotice> findByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNo(String financialProductId);
	
	FinancialProductNotice findTopByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNoDesc(String financialProductId);
	
	List<FinancialProductNotice> findByIdInAndDeletedFlagIsFalse(List<String> ids);

}
