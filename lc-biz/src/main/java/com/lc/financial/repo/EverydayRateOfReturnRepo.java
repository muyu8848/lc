package com.lc.financial.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.EverydayRateOfReturn;

public interface EverydayRateOfReturnRepo
		extends JpaRepository<EverydayRateOfReturn, String>, JpaSpecificationExecutor<EverydayRateOfReturn> {
	
	EverydayRateOfReturn findByTheDateAndFinancialProductId(Date theDate, String financialProductId);
	
	List<EverydayRateOfReturn> findByFinancialProductIdOrderByTheDateDesc(String financialProductId);
	
	List<EverydayRateOfReturn> findByFinancialProductIdOrderByTheDateAsc(String financialProductId);
	
	List<EverydayRateOfReturn> findTop7ByFinancialProductIdOrderByTheDateDesc(String financialProductId);
	
	Long countByFinancialProductId(String financialProductId);
	
	EverydayRateOfReturn findTopByFinancialProductIdOrderByTheDateAsc(String financialProductId);
	
	EverydayRateOfReturn findTopByFinancialProductIdOrderByTheDateDesc(String financialProductId);

}
