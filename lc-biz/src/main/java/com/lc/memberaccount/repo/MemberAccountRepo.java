package com.lc.memberaccount.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lc.memberaccount.domain.MemberAccount;

public interface MemberAccountRepo extends JpaRepository<MemberAccount, String>, JpaSpecificationExecutor<MemberAccount> {

	MemberAccount findByUserNameAndDeletedFlagIsFalse(String userName);
	
}
