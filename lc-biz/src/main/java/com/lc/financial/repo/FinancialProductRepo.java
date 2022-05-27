package com.lc.financial.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.financial.domain.FinancialProduct;

public interface FinancialProductRepo
		extends JpaRepository<FinancialProduct, String>, JpaSpecificationExecutor<FinancialProduct> {
	
	List<FinancialProduct> findByDeletedFlagIsFalseOrderByCreateTimeDesc();
	
	List<FinancialProduct> findByProductTypeAndDeletedFlagIsFalseOrderByCreateTimeDesc(String productType);
	
	List<FinancialProduct> findByProductNameLikeAndDeletedFlagIsFalseOrderByCreateTimeDesc(String productName);
	
	List<FinancialProduct> findByIdInAndDeletedFlagIsFalse(List<String> ids);
	
	List<FinancialProduct> findTop10ByDeletedFlagIsFalseOrderByCreateTimeDesc();
	
}
