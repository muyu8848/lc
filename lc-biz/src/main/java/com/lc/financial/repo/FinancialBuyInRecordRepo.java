package com.lc.financial.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FinancialBuyInRecord;

public interface FinancialBuyInRecordRepo
		extends JpaRepository<FinancialBuyInRecord, String>, JpaSpecificationExecutor<FinancialBuyInRecord> {

	List<FinancialBuyInRecord> findByFinancialProductIdAndState(String financialProductId, String state);

	List<FinancialBuyInRecord> findByState(String state);

	List<FinancialBuyInRecord> findByAccountIdAndStateInOrderByCreateTimeDesc(String accountId,
			List<String> states);

	List<FinancialBuyInRecord> findByAccountIdAndFinancialProductIdAndStateInOrderByCreateTimeDesc(
			String accountId, String financialProductId, List<String> states);
	
	List<FinancialBuyInRecord> findByAccountIdAndFinancialProductId(
			String accountId, String financialProductId);

}
