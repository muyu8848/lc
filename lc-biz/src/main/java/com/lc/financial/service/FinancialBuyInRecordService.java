package com.lc.financial.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.lc.common.exception.BizError;
import com.lc.common.exception.BizException;
import com.lc.common.utils.IdUtils;
import com.lc.common.vo.PageResult;
import com.lc.constants.Constant;
import com.lc.financial.domain.EverydayRateOfReturn;
import com.lc.financial.domain.FinancialBuyInRecord;
import com.lc.financial.domain.FinancialIncomeRecord;
import com.lc.financial.domain.FinancialProduct;
import com.lc.financial.domain.FinancialTakeOutRecord;
import com.lc.financial.domain.TransactionDetail;
import com.lc.financial.param.BuyInFinancialProductParam;
import com.lc.financial.param.FinancialProductBuyInRecordQueryCondParam;
import com.lc.financial.param.IncomeDetailQueryCondParam;
import com.lc.financial.param.TakeOutParam;
import com.lc.financial.param.TransactionDetailQueryCondParam;
import com.lc.financial.repo.EverydayRateOfReturnRepo;
import com.lc.financial.repo.FinancialBuyInRecordRepo;
import com.lc.financial.repo.FinancialIncomeRecordRepo;
import com.lc.financial.repo.FinancialProductRepo;
import com.lc.financial.repo.FinancialTakeOutRecordRepo;
import com.lc.financial.repo.TransactionDetailRepo;
import com.lc.financial.vo.EverydayCumsumIncomeVO;
import com.lc.financial.vo.FinancialBuyInRecordVO;
import com.lc.financial.vo.FinancialIncomeRecordVO;
import com.lc.financial.vo.FinancialProductIncomeVO;
import com.lc.financial.vo.FinancialTakeOutRecordVO;
import com.lc.financial.vo.HoldingProductAssetAndIncomeVO;
import com.lc.financial.vo.HoldingRecordGroupByBuyInDateVO;
import com.lc.financial.vo.IncomeDetailVO;
import com.lc.financial.vo.TransactionDetailVO;
import com.lc.memberaccount.domain.MemberAccount;
import com.lc.memberaccount.repo.MemberAccountRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class FinancialBuyInRecordService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private FinancialBuyInRecordRepo buyInRecordRepo;

	@Autowired
	private FinancialProductRepo financialProductRepo;

	@Autowired
	private EverydayRateOfReturnRepo everydayRateOfReturnRepo;

	@Autowired
	private FinancialIncomeRecordRepo incomeRecordRepo;

	@Autowired
	private MemberAccountRepo accountRepo;

	@Autowired
	private TransactionDetailRepo transactionDetailRepo;

	@Autowired
	private FinancialTakeOutRecordRepo takeOutRecordRepo;

	public List<HoldingRecordGroupByBuyInDateVO> findHoldingRecordByFinancialProductIdGroupByBuyInDate(
			@NotBlank String accountId, @NotBlank String financialProductId) {
		List<FinancialBuyInRecord> records = buyInRecordRepo
				.findByAccountIdAndFinancialProductIdAndStateInOrderByCreateTimeDesc(accountId, financialProductId,
						Arrays.asList(Constant.理财产品买入记录状态_持有中));
		Map<Date, HoldingRecordGroupByBuyInDateVO> buyInDateMap = new TreeMap<>();
		for (FinancialBuyInRecord record : records) {
			if (buyInDateMap.get(record.getBuyInDate()) == null) {
				buyInDateMap.put(record.getBuyInDate(), new HoldingRecordGroupByBuyInDateVO());
			}
			HoldingRecordGroupByBuyInDateVO vo = buyInDateMap.get(record.getBuyInDate());
			vo.setAsset(NumberUtil.round(vo.getAsset() + record.getAvailableAmount(), 2).doubleValue());
			vo.setTotalIncome(NumberUtil.round(vo.getTotalIncome() + record.getIncome(), 2).doubleValue());
			FinancialIncomeRecord yesterdayIncomeRecord = incomeRecordRepo.findByBuyInRecordIdAndTheDateAndState(
					record.getId(), DateUtil.beginOfDay(DateUtil.yesterday()), Constant.理财收益状态_已结算);
			if (yesterdayIncomeRecord != null) {
				vo.setYesterdayIncome(
						NumberUtil.round(vo.getYesterdayIncome() + yesterdayIncomeRecord.getIncome(), 2).doubleValue());
			}
		}
		
		FinancialProduct product = financialProductRepo.getById(financialProductId);
		List<HoldingRecordGroupByBuyInDateVO> vos = new ArrayList<>();
		for (Entry<Date, HoldingRecordGroupByBuyInDateVO> entry : buyInDateMap.entrySet()) {
			Date buyInDate = entry.getKey();
			HoldingRecordGroupByBuyInDateVO vo = entry.getValue();
			vo.setIssueNum(DateUtil.format(buyInDate, DatePattern.PURE_DATE_PATTERN));
			Date expiredDate = DateUtil.offset(buyInDate, DateField.DAY_OF_YEAR, product.getProductTerm()).toJdkDate();
			vo.setDaysRemaining(DateUtil.between(DateUtil.beginOfDay(new Date()), expiredDate, DateUnit.DAY));
			vos.add(vo);
		}
		return vos;
	}

	public List<IncomeDetailVO> findIncomeDetailByFinancialProductIdAndTheDateGroupByBuyInDate(
			@NotBlank String accountId, @NotBlank String financialProductId, Date theDate) {
		List<FinancialIncomeRecord> records = incomeRecordRepo
				.findByBuyInRecordAccountIdAndTheDateAndStateAndBuyInRecordFinancialProductIdOrderByBuyInRecordBuyInDateAsc(
						accountId, theDate, Constant.理财收益状态_已结算, financialProductId);
		Map<Date, IncomeDetailVO> buyInDateMap = new LinkedHashMap<>();
		for (FinancialIncomeRecord record : records) {
			Date buyInDate = record.getBuyInRecord().getBuyInDate();
			if (buyInDateMap.get(record.getBuyInRecord().getBuyInDate()) == null) {
				IncomeDetailVO vo = new IncomeDetailVO();
				vo.setBuyInDate(buyInDate);
				buyInDateMap.put(buyInDate, vo);
			}
			IncomeDetailVO vo = buyInDateMap.get(buyInDate);
			if (record.getGenerateIncomeAmount() != null) {
				vo.setGenerateIncomeAmount(
						NumberUtil.round(vo.getGenerateIncomeAmount() + record.getGenerateIncomeAmount(), 2).doubleValue());
			}
			vo.setIncome(NumberUtil.round(vo.getIncome() + record.getIncome(), 2).doubleValue());
		}
		return new ArrayList<>(buyInDateMap.values());
	}

	public List<EverydayCumsumIncomeVO> findEverydayCumsumIncome(@NotBlank String accountId,
			@NotBlank String financialProductId) {
		Map<Date, Double> theDateMap = new LinkedHashMap<>();
		List<EverydayCumsumIncomeVO> vos = new ArrayList<>();
		List<FinancialIncomeRecord> records = incomeRecordRepo
				.findByBuyInRecordAccountIdAndStateAndBuyInRecordFinancialProductIdOrderByTheDateAsc(accountId,
						Constant.理财收益状态_已结算, financialProductId);
		for (FinancialIncomeRecord record : records) {
			if (theDateMap.get(record.getTheDate()) == null) {
				theDateMap.put(record.getTheDate(), 0d);
			}
			theDateMap.put(record.getTheDate(), theDateMap.get(record.getTheDate()) + record.getIncome());
		}
		double cumsumIncome = 0d;
		for (Entry<Date, Double> entry : theDateMap.entrySet()) {
			cumsumIncome = NumberUtil.round(cumsumIncome + entry.getValue(), 4).doubleValue();
			vos.add(EverydayCumsumIncomeVO.build(entry.getKey(), cumsumIncome));
		}
		return vos;
	}

	public List<FinancialProductIncomeVO> findYesterdayIncomeGroupByProduct(@NotBlank String accountId) {
		Map<String, FinancialProductIncomeVO> productMap = new LinkedHashMap<>();
		List<FinancialIncomeRecord> records = incomeRecordRepo.findByBuyInRecordAccountIdAndTheDateAndState(accountId,
				DateUtil.beginOfDay(DateUtil.yesterday()), Constant.理财收益状态_已结算);
		for (FinancialIncomeRecord record : records) {
			if (productMap.get(record.getBuyInRecord().getFinancialProductId()) == null) {
				productMap.put(record.getBuyInRecord().getFinancialProductId(),
						FinancialProductIncomeVO.build(record.getBuyInRecord().getFinancialProductId(),
								record.getBuyInRecord().getFinancialProduct().getProductName()));
			}
			FinancialProductIncomeVO vo = productMap.get(record.getBuyInRecord().getFinancialProductId());
			vo.setIncome(NumberUtil.round(vo.getIncome() + record.getIncome(), 2).doubleValue());
		}
		Map<Double, FinancialProductIncomeVO> sortMap = new TreeMap<>();
		for (FinancialProductIncomeVO vo : productMap.values()) {
			sortMap.put(vo.getIncome(), vo);
		}
		return new ArrayList<>(sortMap.values());
	}

	public List<FinancialProductIncomeVO> findTotalIncomeGroupByProduct(@NotBlank String accountId) {
		Map<String, FinancialProductIncomeVO> productMap = new LinkedHashMap<>();
		List<FinancialIncomeRecord> records = incomeRecordRepo.findByBuyInRecordAccountIdAndState(accountId,
				Constant.理财收益状态_已结算);
		for (FinancialIncomeRecord record : records) {
			if (productMap.get(record.getBuyInRecord().getFinancialProductId()) == null) {
				productMap.put(record.getBuyInRecord().getFinancialProductId(),
						FinancialProductIncomeVO.build(record.getBuyInRecord().getFinancialProductId(),
								record.getBuyInRecord().getFinancialProduct().getProductName()));
			}
			FinancialProductIncomeVO vo = productMap.get(record.getBuyInRecord().getFinancialProductId());
			vo.setIncome(NumberUtil.round(vo.getIncome() + record.getIncome(), 2).doubleValue());
		}
		Map<Double, FinancialProductIncomeVO> sortMap = new TreeMap<>();
		for (FinancialProductIncomeVO vo : productMap.values()) {
			sortMap.put(vo.getIncome(), vo);
		}
		return new ArrayList<>(sortMap.values());
	}

	public List<HoldingProductAssetAndIncomeVO> findHoldingProductAssetAndIncome(@NotBlank String accountId) {
		Map<String, HoldingProductAssetAndIncomeVO> productMap = new LinkedHashMap<>();
		List<FinancialBuyInRecord> records = buyInRecordRepo.findByAccountIdAndStateInOrderByCreateTimeDesc(accountId,
				Arrays.asList(Constant.理财产品买入记录状态_持有中));
		for (FinancialBuyInRecord record : records) {
			if (productMap.get(record.getFinancialProductId()) == null) {
				productMap.put(record.getFinancialProductId(), HoldingProductAssetAndIncomeVO
						.build(record.getFinancialProductId(), record.getFinancialProduct().getProductName()));
			}
			HoldingProductAssetAndIncomeVO vo = productMap.get(record.getFinancialProductId());
			vo.setTotalAsset(NumberUtil.round(vo.getTotalAsset() + record.getAvailableAmount(), 2).doubleValue());
		}
		for (Entry<String, HoldingProductAssetAndIncomeVO> entry : productMap.entrySet()) {
			HoldingProductAssetAndIncomeVO vo = entry.getValue();
			vo.setTotalIncome(getTotalIncome(accountId, entry.getKey()));
			vo.setYesterdayIncome(getYesterdayIncome(accountId, entry.getKey()));
		}
		return new ArrayList<>(productMap.values());
	}

	public HoldingProductAssetAndIncomeVO findHoldingProductAssetAndIncome(@NotBlank String accountId,
			@NotBlank String financialProductId) {
		HoldingProductAssetAndIncomeVO vo = new HoldingProductAssetAndIncomeVO();
		List<FinancialBuyInRecord> records = buyInRecordRepo
				.findByAccountIdAndFinancialProductIdAndStateInOrderByCreateTimeDesc(accountId, financialProductId,
						Arrays.asList(Constant.理财产品买入记录状态_持有中));
		for (FinancialBuyInRecord record : records) {
			vo.setTotalAsset(NumberUtil.round(vo.getTotalAsset() + record.getAvailableAmount(), 2).doubleValue());
			vo.setTotalQuota(NumberUtil.round(vo.getTotalQuota() + record.getAvailableQuota(), 2).doubleValue());
		}
		vo.setTotalIncome(getTotalIncome(accountId, financialProductId));
		vo.setYesterdayIncome(getYesterdayIncome(accountId, financialProductId));
		return vo;
	}

	@Transactional(readOnly = true)
	public void autoNoticeIncomeRecordSettlement() {
		List<FinancialIncomeRecord> incomeRecords = incomeRecordRepo
				.findBySettlementTimeIsNullAndTheDateLessThan(DateUtil.beginOfDay(new Date()));
		for (FinancialIncomeRecord incomeRecord : incomeRecords) {
			redissonClient.getTopic(Constant.待结算收益记录ID).publish(incomeRecord.getId());
		}
	}

	@Lock(keys = "'incomeSettlement_' + #incomeId")
	@Transactional
	public void incomeSettlement(@NotBlank String incomeId) {
		FinancialIncomeRecord incomeRecord = incomeRecordRepo.getById(incomeId);
		if (incomeRecord.getSettlementTime() != null) {
			log.warn("当前的收益已结算,无法重复结算;id:{}", incomeId);
			return;
		}
		incomeRecord.setState(Constant.理财收益状态_已结算);
		incomeRecord.setSettlementTime(new Date());
		incomeRecordRepo.save(incomeRecord);

		FinancialBuyInRecord buyInRecord = incomeRecord.getBuyInRecord();
		buyInRecord.setIncome(NumberUtil.round(buyInRecord.getIncome() + incomeRecord.getIncome(), 2).doubleValue());
		buyInRecord.setAvailableAmount(
				NumberUtil.round(buyInRecord.getAvailableAmount() + incomeRecord.getIncome(), 2).doubleValue());
		buyInRecordRepo.save(buyInRecord);

		FinancialProduct product = buyInRecord.getFinancialProduct();
		Date nowTime = DateUtil.beginOfDay(new Date()).toJdkDate();
		Date expiredTime = DateUtil.beginOfDay(
				DateUtil.offset(buyInRecord.getCreateTime(), DateField.DAY_OF_YEAR, product.getProductTerm() + 1))
				.toJdkDate();
		if (product.getProductTerm() > 0 && nowTime.getTime() >= expiredTime.getTime()) {
			buyInRecord.setState(Constant.理财产品买入记录状态_待取出);
			buyInRecordRepo.save(buyInRecord);
		}
	}

	@Transactional(readOnly = true)
	public Double getTotalAsset(@NotBlank String accountId) {
		double totalAsset = 0d;
		List<FinancialBuyInRecord> records = buyInRecordRepo.findByAccountIdAndStateInOrderByCreateTimeDesc(accountId,
				Arrays.asList(Constant.理财产品买入记录状态_持有中));
		for (FinancialBuyInRecord record : records) {
			totalAsset += record.getAvailableAmount();
		}
		totalAsset = NumberUtil.round(totalAsset, 4).doubleValue();
		return totalAsset;
	}

	@Transactional(readOnly = true)
	public String getFinancialProductHoldingState(@NotBlank String financialProductId, @NotBlank String accountId) {
		List<FinancialBuyInRecord> records = buyInRecordRepo.findByAccountIdAndFinancialProductId(accountId,
				financialProductId);
		if (CollectionUtil.isEmpty(records)) {
			return Constant.持有状态_未买过;
		}
		for (FinancialBuyInRecord record : records) {
			if (Constant.理财产品买入记录状态_持有中.equals(record.getState())) {
				return Constant.持有状态_持有中;
			}
		}
		return Constant.持有状态_买过;
	}

	@Transactional(readOnly = true)
	public Double getTotalIncome(@NotBlank String accountId, @NotBlank String financialProductId) {
		double income = 0d;
		List<FinancialIncomeRecord> records = incomeRecordRepo
				.findByBuyInRecordAccountIdAndStateAndBuyInRecordFinancialProductIdOrderByTheDateAsc(accountId,
						Constant.理财收益状态_已结算, financialProductId);
		for (FinancialIncomeRecord record : records) {
			income += +record.getIncome();
		}
		income = NumberUtil.round(income, 2).doubleValue();
		return income;
	}

	@Transactional(readOnly = true)
	public Double getYesterdayIncome(@NotBlank String accountId, @NotBlank String financialProductId) {
		double income = 0d;
		List<FinancialIncomeRecord> records = incomeRecordRepo
				.findByBuyInRecordAccountIdAndTheDateAndStateAndBuyInRecordFinancialProductIdOrderByBuyInRecordBuyInDateAsc(
						accountId, DateUtil.beginOfDay(DateUtil.yesterday()), Constant.理财收益状态_已结算, financialProductId);
		for (FinancialIncomeRecord record : records) {
			income += +record.getIncome();
		}
		income = NumberUtil.round(income, 2).doubleValue();
		return income;
	}

	@Transactional(readOnly = true)
	public Double getYesterdayIncome(@NotBlank String accountId) {
		double income = 0d;
		List<FinancialIncomeRecord> records = incomeRecordRepo.findByBuyInRecordAccountIdAndTheDateAndState(accountId,
				DateUtil.beginOfDay(DateUtil.yesterday()), Constant.理财收益状态_已结算);
		for (FinancialIncomeRecord record : records) {
			income += +record.getIncome();
		}
		income = NumberUtil.round(income, 4).doubleValue();
		return income;
	}

	@Transactional(readOnly = true)
	public Double getTotalIncome(@NotBlank String accountId) {
		double income = 0d;
		List<FinancialIncomeRecord> records = incomeRecordRepo.findByBuyInRecordAccountIdAndState(accountId,
				Constant.理财收益状态_已结算);
		for (FinancialIncomeRecord record : records) {
			income += +record.getIncome();
		}
		income = NumberUtil.round(income, 2).doubleValue();
		return income;
	}

	@Transactional(readOnly = true)
	public List<IncomeDetailVO> findIncomeDetailGroupByTheDate(IncomeDetailQueryCondParam param) {
		Specification<FinancialIncomeRecord> spec = new Specification<FinancialIncomeRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<FinancialIncomeRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("state"), Constant.理财收益状态_已结算));
				predicates.add(
						builder.equal(root.join("buyInRecord", JoinType.INNER).get("accountId"), param.getAccountId()));
				if (StrUtil.isNotBlank(param.getFinancialProductId())) {
					predicates.add(builder.equal(root.join("buyInRecord", JoinType.INNER).get("financialProductId"),
							param.getFinancialProductId()));
				}
				if (param.getTheMonth() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfMonth(param.getTheMonth())));
				}
				if (param.getTheMonth() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfMonth(param.getTheMonth())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		List<FinancialIncomeRecord> records = incomeRecordRepo.findAll(spec, Sort.by(Sort.Order.desc("theDate")));
		Map<Date, Double> theDateMap = new LinkedHashMap<>();
		List<IncomeDetailVO> vos = new ArrayList<>();
		for (FinancialIncomeRecord record : records) {
			if (theDateMap.get(record.getTheDate()) == null) {
				theDateMap.put(record.getTheDate(), 0d);
			}
			theDateMap.put(record.getTheDate(), theDateMap.get(record.getTheDate()) + record.getIncome());
		}
		for (Entry<Date, Double> entry : theDateMap.entrySet()) {
			double income = NumberUtil.round(entry.getValue(), 2).doubleValue();
			vos.add(IncomeDetailVO.buildTheDate(income, entry.getKey()));
		}
		return vos;
	}

	@Transactional(readOnly = true)
	public List<TransactionDetailVO> findTransactionDetail(TransactionDetailQueryCondParam param) {
		Specification<TransactionDetail> spec = new Specification<TransactionDetail>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<TransactionDetail> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getAccountId())) {
					predicates.add(builder.equal(root.get("accountId"), param.getAccountId()));
				}
				if (StrUtil.isNotBlank(param.getType())) {
					predicates.add(builder.equal(root.get("type"), param.getType()));
				}
				if (StrUtil.isNotBlank(param.getFinancialProductId())) {
					predicates.add(builder.equal(root.get("financialProductId"), param.getFinancialProductId()));
				}
				if (param.getTheMonth() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfMonth(param.getTheMonth())));
				}
				if (param.getTheMonth() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfMonth(param.getTheMonth())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		List<TransactionDetail> result = transactionDetailRepo.findAll(spec, Sort.by(Sort.Order.desc("createTime")));
		return TransactionDetailVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public List<FinancialIncomeRecordVO> findSettledIncomeRecordByBuyInRecordId(@NotBlank String buyInRecordId) {
		return FinancialIncomeRecordVO.convertFor(
				incomeRecordRepo.findByBuyInRecordIdAndSettlementTimeIsNotNullOrderByTheDateDesc(buyInRecordId));
	}

	@Transactional(readOnly = true)
	public PageResult<FinancialBuyInRecordVO> findBuyInRecordByPage(FinancialProductBuyInRecordQueryCondParam param) {
		Specification<FinancialBuyInRecord> spec = buildQueryCond(param);
		Page<FinancialBuyInRecord> result = buyInRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<FinancialBuyInRecordVO> pageResult = new PageResult<>(
				FinancialBuyInRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public FinancialTakeOutRecordVO findTakeOutRecordById(String id) {
		FinancialTakeOutRecord po = takeOutRecordRepo.getById(id);
		return FinancialTakeOutRecordVO.convertFor(po);
	}

	@Transactional(readOnly = true)
	public FinancialBuyInRecordVO findBuyInRecordById(String id) {
		FinancialBuyInRecord po = buyInRecordRepo.getById(id);
		return FinancialBuyInRecordVO.convertFor(po);
	}

	public Specification<FinancialBuyInRecord> buildQueryCond(FinancialProductBuyInRecordQueryCondParam param) {
		Specification<FinancialBuyInRecord> spec = new Specification<FinancialBuyInRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<FinancialBuyInRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (StrUtil.isNotBlank(param.getAccountId())) {
					predicates.add(builder.equal(root.get("accountId"), param.getAccountId()));
				}
				if (StrUtil.isNotBlank(param.getUserName())) {
					predicates.add(
							builder.equal(root.join("account", JoinType.INNER).get("userName"), param.getUserName()));
				}
				if (param.getCreateTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getCreateTimeStart())));
				}
				if (param.getCreateTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getCreateTimeEnd())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional
	public void generateYesterdayIncomeRecord() {
		Date yesterday = DateUtil.beginOfDay(DateUtil.yesterday().toJdkDate()).toJdkDate();
		for (FinancialProduct financialProduct : financialProductRepo.findByDeletedFlagIsFalseOrderByCreateTimeDesc()) {
			EverydayRateOfReturn yesterdayRateOfReturn = everydayRateOfReturnRepo
					.findByTheDateAndFinancialProductId(yesterday, financialProduct.getId());
			List<FinancialBuyInRecord> buyInRecords = buyInRecordRepo
					.findByFinancialProductIdAndState(financialProduct.getId(), Constant.理财产品买入记录状态_持有中);
			for (FinancialBuyInRecord buyInRecord : buyInRecords) {
				long betweenDay = DateUtil.between(buyInRecord.getBuyInDate(), yesterday, DateUnit.DAY, false);
				if (betweenDay < 1) {
					continue;
				}
				FinancialIncomeRecord incomeRecord = incomeRecordRepo.findByBuyInRecordIdAndTheDate(buyInRecord.getId(),
						yesterday);
				if (incomeRecord != null) {
					continue;
				}
				if (Constant.收益类型_预期收益型.equals(financialProduct.getIncomeType())) {
					double income = NumberUtil
							.round(buyInRecord.getAvailableAmount() * yesterdayRateOfReturn.getRateOfReturn() / 100, 2)
							.doubleValue();
					incomeRecord = FinancialIncomeRecord.buildIncomeType(buyInRecord.getAvailableAmount(),
							yesterdayRateOfReturn.getRateOfReturn(), income, yesterday, buyInRecord.getId());
					incomeRecordRepo.save(incomeRecord);
				} else if (Constant.收益类型_净值型.equals(financialProduct.getIncomeType())) {
					double income = NumberUtil
							.round(buyInRecord.getAvailableQuota() * yesterdayRateOfReturn.getChg() / 100, 2)
							.doubleValue();
					incomeRecord = FinancialIncomeRecord.buildNpvType(buyInRecord.getAvailableQuota(),
							yesterdayRateOfReturn.getChg(), income, yesterday, buyInRecord.getId());
					incomeRecordRepo.save(incomeRecord);
				}
			}
		}
	}

	@Transactional(readOnly = true)
	public void expireTakeOut() {
		List<FinancialBuyInRecord> records = buyInRecordRepo.findByState(Constant.理财产品买入记录状态_待取出);
		for (FinancialBuyInRecord record : records) {
			redissonClient.getTopic(Constant.待取出记录ID).publish(record.getId());
		}
	}

	@Lock(keys = "'expireTakeOut_' + #recordId")
	@Transactional
	public void expireTakeOut(@NotBlank String recordId) {
		FinancialBuyInRecord record = buyInRecordRepo.getById(recordId);
		if (!Constant.理财产品买入记录状态_待取出.equals(record.getState())) {
			log.warn("待取出的记录状态异常;id:{}", recordId);
			return;
		}
		if (record.getTakeOutTime() != null) {
			log.warn("当前的记录已取出,无法重复取出;id:{}", recordId);
			return;
		}
		Date now = new Date();
		Double takeOutAmount = record.getAvailableAmount();
		Double takeOutQuota = record.getAvailableQuota();
		record.setAvailableAmount(0d);
		record.setAvailableQuota(0d);
		record.setState(Constant.理财产品买入记录状态_已取出);
		record.setTakeOutTime(now);
		buyInRecordRepo.save(record);

		FinancialTakeOutRecord takeOutRecord = new FinancialTakeOutRecord();
		takeOutRecord.setId(IdUtils.getId());
		takeOutRecord.setOrderNo(takeOutRecord.getId());
		takeOutRecord.setCreateTime(now);
		takeOutRecord.setAmount(takeOutAmount);
		takeOutRecord.setQuota(takeOutQuota);
		takeOutRecord.setAccountId(record.getAccountId());
		takeOutRecord.setFinancialProductId(record.getFinancialProductId());
		takeOutRecordRepo.save(takeOutRecord);

		TransactionDetail transactionDetail = TransactionDetail.buildWithTakeOut(takeOutRecord);
		transactionDetailRepo.save(transactionDetail);

		MemberAccount account = record.getAccount();
		account.setBalance(NumberUtil.round(account.getBalance() + takeOutAmount, 2).doubleValue());
		accountRepo.save(account);
	}

	@Transactional
	public String takeOutWithAmount(TakeOutParam param) {
		if (param.getAmount() == null) {
			throw new BizException("取出金额不能为空");
		}
		if (param.getAmount() <= 0) {
			throw new BizException("取出金额必须大于0");
		}
		Date now = new Date();
		double totalAsset = 0d;
		List<FinancialBuyInRecord> records = buyInRecordRepo
				.findByAccountIdAndFinancialProductIdAndStateInOrderByCreateTimeDesc(param.getAccountId(),
						param.getFinancialProductId(), Arrays.asList(Constant.理财产品买入记录状态_持有中));
		for (FinancialBuyInRecord record : records) {
			totalAsset += record.getAvailableAmount();
		}
		if (param.getAmount() > totalAsset) {
			throw new BizException("余额不足");
		}
		double surplusTakeOutAmount = param.getAmount();
		for (FinancialBuyInRecord record : records) {
			surplusTakeOutAmount = NumberUtil.round(surplusTakeOutAmount - record.getAvailableAmount(), 2)
					.doubleValue();
			if (surplusTakeOutAmount >= 0) {
				record.setAvailableAmount(0d);
				record.setState(Constant.理财产品买入记录状态_已取出);
				record.setTakeOutTime(now);
				buyInRecordRepo.save(record);
				if (surplusTakeOutAmount == 0) {
					break;
				}
			} else {
				record.setAvailableAmount(Math.abs(surplusTakeOutAmount));
				buyInRecordRepo.save(record);
				break;
			}
		}
		FinancialTakeOutRecord takeOutRecord = param.convertToPo();
		takeOutRecord.setCreateTime(now);
		takeOutRecord.setQuota(0d);
		takeOutRecordRepo.save(takeOutRecord);

		TransactionDetail transactionDetail = TransactionDetail.buildWithTakeOut(takeOutRecord);
		transactionDetailRepo.save(transactionDetail);

		MemberAccount account = accountRepo.getById(param.getAccountId());
		account.setBalance(NumberUtil.round(account.getBalance() + param.getAmount(), 2).doubleValue());
		accountRepo.save(account);
		return takeOutRecord.getId();
	}

	@Transactional
	public String takeOutWithQuota(TakeOutParam param) {
		if (param.getQuota() == null) {
			throw new BizException("取出份额不能为空");
		}
		if (param.getQuota() <= 0) {
			throw new BizException("取出份额必须大于0");
		}
		double npv = 1;
		EverydayRateOfReturn latestDayRecord = everydayRateOfReturnRepo
				.findTopByFinancialProductIdOrderByTheDateDesc(param.getFinancialProductId());
		if (latestDayRecord != null) {
			npv = latestDayRecord.getNpv();
		}
		Date now = new Date();
		double totalQuota = 0d;
		List<FinancialBuyInRecord> records = buyInRecordRepo
				.findByAccountIdAndFinancialProductIdAndStateInOrderByCreateTimeDesc(param.getAccountId(),
						param.getFinancialProductId(), Arrays.asList(Constant.理财产品买入记录状态_持有中));
		for (FinancialBuyInRecord record : records) {
			totalQuota += record.getAvailableQuota();
		}
		if (param.getQuota() > totalQuota) {
			throw new BizException("份额不足");
		}
		double surplusTakeOutQuota = param.getQuota();
		for (FinancialBuyInRecord record : records) {
			surplusTakeOutQuota = NumberUtil.round(surplusTakeOutQuota - record.getAvailableQuota(), 2).doubleValue();
			if (surplusTakeOutQuota >= 0) {
				record.setAvailableQuota(0d);
				record.setAvailableAmount(0d);
				record.setState(Constant.理财产品买入记录状态_已取出);
				record.setTakeOutTime(now);
				buyInRecordRepo.save(record);
				if (surplusTakeOutQuota == 0) {
					break;
				}
			} else {
				record.setAvailableQuota(Math.abs(surplusTakeOutQuota));
				record.setAvailableAmount(NumberUtil.round(record.getAvailableQuota() * npv, 2).doubleValue());
				buyInRecordRepo.save(record);
				break;
			}
		}
		FinancialTakeOutRecord takeOutRecord = param.convertToPo();
		takeOutRecord.setCreateTime(now);
		takeOutRecord.setNpv(npv);
		takeOutRecord.setAmount(NumberUtil.round(param.getQuota() * npv, 2).doubleValue());
		takeOutRecordRepo.save(takeOutRecord);

		TransactionDetail transactionDetail = TransactionDetail.buildWithTakeOut(takeOutRecord);
		transactionDetailRepo.save(transactionDetail);

		MemberAccount account = accountRepo.getById(param.getAccountId());
		account.setBalance(NumberUtil.round(account.getBalance() + (param.getQuota() * npv), 2).doubleValue());
		accountRepo.save(account);
		return takeOutRecord.getId();
	}

	@Lock(keys = "'takeOut_' + #param.accountId")
	@Transactional
	public String takeOut(TakeOutParam param) {
		FinancialProduct product = financialProductRepo.getById(param.getFinancialProductId());
		if (product.getProductTerm() > 0) {
			throw new BizException("该产品不支持随时取出");
		}
		if (Constant.收益类型_预期收益型.equals(product.getIncomeType())) {
			return takeOutWithAmount(param);
		} else if (Constant.收益类型_净值型.equals(product.getIncomeType())) {
			return takeOutWithQuota(param);
		}
		throw new BizException("未知收益类型产品");
	}

	@Lock(keys = "'buyIn_' + #param.accountId")
	@Transactional
	public String buyIn(BuyInFinancialProductParam param) {
		FinancialProduct financialProduct = financialProductRepo.getById(param.getFinancialProductId());
		MemberAccount account = accountRepo.getById(param.getAccountId());
		if (financialProduct.getMinSubscribeAmount() > param.getBuyInAmount()) {
			throw new BizException(BizError.业务异常.getCode(),
					MessageFormat.format("认购金额需大于或等于{0}", financialProduct.getMinSubscribeAmount()));
		}
		double balance = NumberUtil.round(account.getBalance() - param.getBuyInAmount(), 2).doubleValue();
		if (balance < 0) {
			throw new BizException("余额不足");
		}
		FinancialBuyInRecord buyInRecord = param.convertToPo();
		if (Constant.收益类型_净值型.equals(financialProduct.getIncomeType())) {
			double npv = 1;
			EverydayRateOfReturn latestDayRecord = everydayRateOfReturnRepo
					.findTopByFinancialProductIdOrderByTheDateDesc(financialProduct.getId());
			if (latestDayRecord != null) {
				npv = latestDayRecord.getNpv();
			}
			buyInRecord.setBuyInNpv(npv);
			buyInRecord.setBuyInQuota(NumberUtil.round(buyInRecord.getBuyInAmount() / npv, 2).doubleValue());
			buyInRecord.setAvailableQuota(buyInRecord.getBuyInQuota());
		}
		buyInRecordRepo.save(buyInRecord);
		TransactionDetail transactionDetail = TransactionDetail.buildWithBuyIn(buyInRecord);
		transactionDetailRepo.save(transactionDetail);

		account.setBalance(balance);
		accountRepo.save(account);
		return buyInRecord.getId();
	}

}
