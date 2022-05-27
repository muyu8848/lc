package com.lc.financial.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FinancialIncomeRecord;

public interface FinancialIncomeRecordRepo
		extends JpaRepository<FinancialIncomeRecord, String>, JpaSpecificationExecutor<FinancialIncomeRecord> {

	FinancialIncomeRecord findByBuyInRecordIdAndTheDateAndState(String buyInRecordId, Date theDate, String state);

	FinancialIncomeRecord findByBuyInRecordIdAndTheDate(String buyInRecordId, Date theDate);

	List<FinancialIncomeRecord> findByBuyInRecordIdOrderByTheDateDesc(String buyInRecordId);

	List<FinancialIncomeRecord> findByBuyInRecordIdAndSettlementTimeIsNotNullOrderByTheDateDesc(String buyInRecordId);

	List<FinancialIncomeRecord> findByBuyInRecordAccountIdAndTheDateAndState(String accountId, Date theDate,
			String state);
	
	List<FinancialIncomeRecord> findByBuyInRecordAccountIdAndTheDateAndStateAndBuyInRecordFinancialProductIdOrderByBuyInRecordBuyInDateAsc(
			String accountId, Date theDate, String state, String financialProductId);

	List<FinancialIncomeRecord> findByBuyInRecordAccountIdAndStateAndBuyInRecordFinancialProductIdOrderByTheDateAsc(
			String accountId, String state, String financialProductId);

	List<FinancialIncomeRecord> findByBuyInRecordAccountIdAndState(String accountId, String state);

	List<FinancialIncomeRecord> findBySettlementTimeIsNullAndTheDateLessThan(Date theDate);

	List<FinancialIncomeRecord> findByBuyInRecordIdAndStateOrderByTheDateDesc(String buyInRecordId, String state);

}
