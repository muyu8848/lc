package com.lc.financial.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FinancialCompany;

public interface FinancialCompanyRepo
		extends JpaRepository<FinancialCompany, String>, JpaSpecificationExecutor<FinancialCompany> {
	
	List<FinancialCompany> findByDeletedFlagIsFalseOrderByCreateTimeDesc();

}
