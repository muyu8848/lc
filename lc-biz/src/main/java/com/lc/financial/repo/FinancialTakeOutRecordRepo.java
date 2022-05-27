package com.lc.financial.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FinancialTakeOutRecord;

public interface FinancialTakeOutRecordRepo
		extends JpaRepository<FinancialTakeOutRecord, String>, JpaSpecificationExecutor<FinancialTakeOutRecord> {

}
