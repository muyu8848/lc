package com.lc.financial.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotBlank;

import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.lc.common.valid.ParamValid;
import com.lc.constants.Constant;
import com.lc.financial.domain.EverydayRateOfReturn;
import com.lc.financial.domain.FinancialCompany;
import com.lc.financial.domain.FinancialProduct;
import com.lc.financial.domain.FinancialProductFeature;
import com.lc.financial.domain.FinancialProductNotice;
import com.lc.financial.param.AddOrUpdateFinancialProductFeatureParam;
import com.lc.financial.param.AddOrUpdateFinancialProductNoticeParam;
import com.lc.financial.param.AddOrUpdateFinancialProductParam;
import com.lc.financial.repo.EverydayRateOfReturnRepo;
import com.lc.financial.repo.FinancialProductFeatureRepo;
import com.lc.financial.repo.FinancialProductNoticeRepo;
import com.lc.financial.repo.FinancialProductRepo;
import com.lc.financial.vo.CanBuyFinancialProductVO;
import com.lc.financial.vo.EverydayRateOfReturnVO;
import com.lc.financial.vo.FinancialCompanyVO;
import com.lc.financial.vo.FinancialProductFeatureVO;
import com.lc.financial.vo.FinancialProductNoticeVO;
import com.lc.financial.vo.FinancialProductVO;
import com.lc.financial.vo.NpvVO;
import com.lc.financial.vo.TenThousandIncomeVO;
import com.lc.financial.vo.YearRateOfReturnVO;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class FinancialProductService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RedissonClient redissonClient;

//	@Autowired
//	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private FinancialProductRepo financialProductRepo;

	@Autowired
	private EverydayRateOfReturnRepo everydayRateOfReturnRepo;

	@Autowired
	private FinancialProductNoticeRepo financialProductNoticeRepo;

	@Autowired
	private FinancialProductFeatureRepo financialProductFeatureRepo;

	@Transactional
	public void adjustProductFeatureOrderNo(List<String> productFeatureIds) {
		List<FinancialProductFeature> financialProductFeatures = financialProductFeatureRepo
				.findByIdInAndDeletedFlagIsFalse(productFeatureIds);
		for (int i = 0; i < productFeatureIds.size(); i++) {
			String productFeatureId = productFeatureIds.get(i);
			for (FinancialProductFeature financialProductFeature : financialProductFeatures) {
				if (productFeatureId.equals(financialProductFeature.getId())) {
					financialProductFeature.setOrderNo((double) (i + 1));
					financialProductFeatureRepo.save(financialProductFeature);
					break;
				}
			}
		}
	}

	@Transactional
	public void delFinancialProductFeature(@NotBlank String id) {
		FinancialProductFeature financialProductFeature = financialProductFeatureRepo.getById(id);
		financialProductFeature.deleted();
		financialProductFeatureRepo.save(financialProductFeature);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateFinancialProductFeature(AddOrUpdateFinancialProductFeatureParam param) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			Double orderNo = 1d;
			FinancialProductFeature maxOrderNo = financialProductFeatureRepo
					.findTopByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNoDesc(param.getFinancialProductId());
			if (maxOrderNo != null) {
				orderNo = maxOrderNo.getOrderNo();
			}
			FinancialProductFeature financialProductFeature = param.convertToPo();
			financialProductFeature.setOrderNo(orderNo);
			financialProductFeatureRepo.save(financialProductFeature);
		}
		// 修改
		else {
			FinancialProductFeature financialProductFeature = financialProductFeatureRepo.getById(param.getId());
			BeanUtils.copyProperties(param, financialProductFeature);
			financialProductFeature.setLastModifyTime(new Date());
			financialProductFeatureRepo.save(financialProductFeature);
		}
	}

	@Transactional(readOnly = true)
	public FinancialProductFeatureVO findFinancialProductFeature(@NotBlank String id) {
		return FinancialProductFeatureVO.convertFor(financialProductFeatureRepo.getById(id));
	}

	@Transactional(readOnly = true)
	public List<FinancialProductFeatureVO> findFinancialProductFeatureByFinancialProductId(
			@NotBlank String financialProductId) {
		return FinancialProductFeatureVO.convertFor(financialProductFeatureRepo
				.findByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNo(financialProductId));
	}

	@Transactional
	public void adjustProductNoticeOrderNo(List<String> productNoticeIds) {
		List<FinancialProductNotice> financialProductNotices = financialProductNoticeRepo
				.findByIdInAndDeletedFlagIsFalse(productNoticeIds);
		for (int i = 0; i < productNoticeIds.size(); i++) {
			String productNoticeId = productNoticeIds.get(i);
			for (FinancialProductNotice financialProductNotice : financialProductNotices) {
				if (productNoticeId.equals(financialProductNotice.getId())) {
					financialProductNotice.setOrderNo((double) (i + 1));
					financialProductNoticeRepo.save(financialProductNotice);
					break;
				}
			}
		}
	}

	@Transactional
	public void delFinancialProductNotice(@NotBlank String id) {
		FinancialProductNotice financialProductNotice = financialProductNoticeRepo.getById(id);
		financialProductNotice.deleted();
		financialProductNoticeRepo.save(financialProductNotice);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateFinancialProductNotice(AddOrUpdateFinancialProductNoticeParam param) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			Double orderNo = 1d;
			FinancialProductNotice maxOrderNo = financialProductNoticeRepo
					.findTopByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNoDesc(param.getFinancialProductId());
			if (maxOrderNo != null) {
				orderNo = maxOrderNo.getOrderNo();
			}
			FinancialProductNotice financialProductNotice = param.convertToPo();
			financialProductNotice.setOrderNo(orderNo);
			financialProductNoticeRepo.save(financialProductNotice);
		}
		// 修改
		else {
			FinancialProductNotice financialProductNotice = financialProductNoticeRepo.getById(param.getId());
			BeanUtils.copyProperties(param, financialProductNotice);
			financialProductNotice.setLastModifyTime(new Date());
			financialProductNoticeRepo.save(financialProductNotice);
		}
	}

	@Transactional(readOnly = true)
	public FinancialProductNoticeVO findFinancialProductNotice(@NotBlank String id) {
		return FinancialProductNoticeVO.convertFor(financialProductNoticeRepo.getById(id));
	}

	@Transactional(readOnly = true)
	public List<FinancialProductNoticeVO> findFinancialProductNoticeByFinancialProductId(
			@NotBlank String financialProductId) {
		return FinancialProductNoticeVO.convertFor(financialProductNoticeRepo
				.findByFinancialProductIdAndDeletedFlagIsFalseOrderByOrderNo(financialProductId));
	}

	@Transactional(readOnly = true)
	public List<EverydayRateOfReturnVO> findEverydayRateOfReturnByFinancialProductId(
			@NotBlank String financialProductId) {
		FinancialProduct product = financialProductRepo.getById(financialProductId);
		List<EverydayRateOfReturnVO> vos = new ArrayList<>();
		if (Constant.收益类型_预期收益型.equals(product.getIncomeType())) {
			Map<String, Double> yearRateOfReturnMap = new HashMap<>();
			List<YearRateOfReturnVO> yearRateOfReturnVos = findYearRateOfReturn(financialProductId);
			for (YearRateOfReturnVO yearRateOfReturnVo : yearRateOfReturnVos) {
				yearRateOfReturnMap.put(yearRateOfReturnVo.getTheDate(), yearRateOfReturnVo.getValue());
			}
			vos = EverydayRateOfReturnVO.convertFor(
					everydayRateOfReturnRepo.findByFinancialProductIdOrderByTheDateDesc(financialProductId));
			for (EverydayRateOfReturnVO vo : vos) {
				vo.setTenThousandIncome(NumberUtil.round(vo.getRateOfReturn() / 100 * 10000, 4).doubleValue());
				Double yearRateOfReturn = yearRateOfReturnMap.get(DateUtil.formatDate(vo.getTheDate()));
				if (yearRateOfReturn != null) {
					vo.setYearRateOfReturn(yearRateOfReturn);
				}
			}
		} else if (Constant.收益类型_净值型.equals(product.getIncomeType())) {
			vos = EverydayRateOfReturnVO.convertFor(
					everydayRateOfReturnRepo.findByFinancialProductIdOrderByTheDateDesc(financialProductId));
		}

		return vos;
	}

	@Transactional(readOnly = true)
	public NpvVO findNpvByFinancialProductIdAndTheDate(@NotBlank String financialProductId, Date theDate) {
		EverydayRateOfReturn po = everydayRateOfReturnRepo.findByTheDateAndFinancialProductId(theDate,
				financialProductId);
		NpvVO vo = new NpvVO();
		vo.setTheDate(DateUtil.formatDate(po.getTheDate()));
		vo.setValue(po.getNpv());
		vo.setChg(po.getChg());
		return vo;
	}

	@Transactional(readOnly = true)
	public TenThousandIncomeVO findTenThousandIncomeByFinancialProductIdAndTheDate(@NotBlank String financialProductId,
			Date theDate) {
		EverydayRateOfReturn po = everydayRateOfReturnRepo.findByTheDateAndFinancialProductId(theDate,
				financialProductId);
		TenThousandIncomeVO vo = new TenThousandIncomeVO();
		vo.setTheDate(po.getTheDate());
		vo.setValue(NumberUtil.round(po.getRateOfReturn() / 100 * 10000, 4).doubleValue());
		return vo;
	}

	@Transactional(readOnly = true)
	public Double getLatestNpv(String financialProductId) {
		double npv = 1;
		EverydayRateOfReturn latestDayRecord = everydayRateOfReturnRepo
				.findTopByFinancialProductIdOrderByTheDateDesc(financialProductId);
		if (latestDayRecord != null) {
			npv = latestDayRecord.getNpv();
		}
		return npv;
	}

	@Transactional(readOnly = true)
	public List<NpvVO> findNpv(@NotBlank String financialProductId) {
		List<NpvVO> vos = new ArrayList<>();
		List<EverydayRateOfReturn> records = everydayRateOfReturnRepo
				.findByFinancialProductIdOrderByTheDateAsc(financialProductId);
		for (EverydayRateOfReturn record : records) {
			NpvVO vo = new NpvVO();
			vo.setTheDate(DateUtil.formatDate(record.getTheDate()));
			vo.setValue(record.getNpv());
			vo.setChg(record.getChg());
			vos.add(vo);
		}
		return vos;
	}

	@Transactional(readOnly = true)
	public List<TenThousandIncomeVO> findTenThousandIncome(@NotBlank String financialProductId) {
		List<TenThousandIncomeVO> vos = new ArrayList<>();
		List<EverydayRateOfReturn> pos = everydayRateOfReturnRepo
				.findByFinancialProductIdOrderByTheDateAsc(financialProductId);
		for (int i = 0; i < pos.size(); i++) {
			EverydayRateOfReturn po = pos.get(i);
			TenThousandIncomeVO vo = new TenThousandIncomeVO();
			vo.setTheDate(po.getTheDate());
			vo.setValue(NumberUtil.round(po.getRateOfReturn() / 100 * 10000, 4).doubleValue());
			vos.add(vo);
		}
		return vos;
	}

	@Transactional(readOnly = true)
	public List<YearRateOfReturnVO> findYearRateOfReturn(@NotBlank String financialProductId) {
		Long size = redisTemplate.opsForList().size(Constant.产品年化收益率 + financialProductId);
		List<String> list = redisTemplate.opsForList().range(Constant.产品年化收益率 + financialProductId, 0, size);
		String jsonData = "[" + CollectionUtil.join(list, ",") + "]";
		JSONArray jsonArray = JSONUtil.parseArray(jsonData);
		List<YearRateOfReturnVO> yearVos = JSONUtil.toList(jsonArray, YearRateOfReturnVO.class);
		return yearVos;
	}

	@Transactional
	public void refreshYearRateOfReturnCache(@NotBlank String financialProductId) {
		redisTemplate.delete(Constant.产品年化收益率 + financialProductId);
		List<EverydayRateOfReturn> pos = everydayRateOfReturnRepo
				.findByFinancialProductIdOrderByTheDateAsc(financialProductId);
		List<YearRateOfReturnVO> vos = buildYearRateOfReturnData(pos);
		for (Iterator<YearRateOfReturnVO> iterator = vos.iterator(); iterator.hasNext();) {
			YearRateOfReturnVO vo = iterator.next();
			redisTemplate.opsForList().rightPush(Constant.产品年化收益率 + financialProductId, JSONUtil.toJsonStr(vo));
			if (!iterator.hasNext()) {
				redisTemplate.opsForValue().set(Constant.产品年化收益率_最新 + financialProductId,
						String.valueOf(vo.getValue()));
			}
		}
	}

	@Transactional
	public void refreshLatestYearNpvRateCache(@NotBlank String financialProductId) {
		Long days = everydayRateOfReturnRepo.countByFinancialProductId(financialProductId);
		EverydayRateOfReturn latestDayRecord = everydayRateOfReturnRepo
				.findTopByFinancialProductIdOrderByTheDateDesc(financialProductId);
		double latestNpv = latestDayRecord != null ? latestDayRecord.getNpv() : 0;
		EverydayRateOfReturn firstDayRecord = everydayRateOfReturnRepo
				.findTopByFinancialProductIdOrderByTheDateAsc(financialProductId);
		double firstNpv = firstDayRecord != null ? firstDayRecord.getNpv() : 0;
		double yearNpv = NumberUtil.round(((latestNpv - firstNpv) / firstNpv / days * 365 * 100), 2).doubleValue();
		redisTemplate.opsForValue().set(Constant.产品年化净值增长率_最新 + financialProductId, String.valueOf(yearNpv));
	}

	public List<YearRateOfReturnVO> buildYearRateOfReturnData(List<EverydayRateOfReturn> pos) {
		List<YearRateOfReturnVO> vos = new ArrayList<>();
		Map<Integer, EverydayRateOfReturn> map = new TreeMap<>();
		for (int i = 0; i < pos.size(); i++) {
			EverydayRateOfReturn vo = pos.get(i);
			map.put(i, vo);
		}
		for (Iterator<Entry<Integer, EverydayRateOfReturn>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, EverydayRateOfReturn> entry = iterator.next();
			Integer index = entry.getKey();
			EverydayRateOfReturn po = entry.getValue();
			Double rateOfReturnTotal = po.getRateOfReturn();
			int rateOfReturnCount = 1;
			for (int i = 1; i < 7; i++) {
				EverydayRateOfReturn pre = map.get(index - i);
				if (pre != null) {
					rateOfReturnTotal += pre.getRateOfReturn();
					rateOfReturnCount++;
				}
			}
			double rateOfReturn = NumberUtil.round(rateOfReturnTotal / rateOfReturnCount * 365, 4).doubleValue();
			YearRateOfReturnVO vo = new YearRateOfReturnVO();
			vo.setTheDate(DateUtil.formatDate(po.getTheDate()));
			vo.setValue(rateOfReturn);
			vos.add(vo);
		}
		return vos;
	}

	@Lock(keys = "'resetNpvData_' + #financialProductId")
	@Transactional
	public void resetNpvData(@NotBlank String financialProductId, Integer days) {
		List<EverydayRateOfReturn> records = everydayRateOfReturnRepo
				.findByFinancialProductIdOrderByTheDateDesc(financialProductId);
		everydayRateOfReturnRepo.deleteAll(records);

		FinancialProduct financialProduct = financialProductRepo.getById(financialProductId);
		Date currentDate = DateUtil.beginOfDay(new Date()).toJdkDate();
		while (days > 0) {
			Date theDate = DateUtil.offset(currentDate, DateField.DAY_OF_YEAR, -days).toJdkDate();
			EverydayRateOfReturn record = everydayRateOfReturnRepo.findByTheDateAndFinancialProductId(theDate,
					financialProduct.getId());
			if (record == null) {
				double chg = NumberUtil
						.round(RandomUtil.randomDouble(financialProduct.getMinChg(), financialProduct.getMaxChg()), 2)
						.doubleValue();
				double beforeNpv = 1d;
				EverydayRateOfReturn beforeRecord = everydayRateOfReturnRepo.findByTheDateAndFinancialProductId(
						DateUtil.offsetDay(theDate, -1).toJdkDate(), financialProduct.getId());
				if (beforeRecord != null) {
					beforeNpv = beforeRecord.getNpv();
				}
				double npv = NumberUtil.round(beforeNpv * (1 + (chg / 100)), 4).doubleValue();
				record = EverydayRateOfReturn.build(chg, npv, theDate, financialProduct.getId());
				everydayRateOfReturnRepo.save(record);
			}
			days--;
		}
		refreshLatestYearNpvRateCache(financialProductId);
	}

	@Lock(keys = "'generatePriorToRateOfReturn_' + #financialProductId")
	@Transactional
	public void generatePriorToRateOfReturn(@NotBlank String financialProductId, Integer days) {
		FinancialProduct financialProduct = financialProductRepo.getById(financialProductId);
		Date currentDate = DateUtil.beginOfDay(new Date()).toJdkDate();
		while (days > 0) {
			Date theDate = DateUtil.offset(currentDate, DateField.DAY_OF_YEAR, -days).toJdkDate();
			EverydayRateOfReturn record = everydayRateOfReturnRepo.findByTheDateAndFinancialProductId(theDate,
					financialProduct.getId());
			if (record == null) {
				double rateOfReturn = NumberUtil.round(RandomUtil.randomDouble(financialProduct.getMinRateOfReturn(),
						financialProduct.getMaxRateOfReturn()), 6).doubleValue();
				record = EverydayRateOfReturn.build(rateOfReturn, theDate, financialProduct.getId());
				everydayRateOfReturnRepo.save(record);
			}
			days--;
		}
		refreshYearRateOfReturnCache(financialProductId);
	}

	@Transactional
	public void generateYesterdayRateOfReturn() {
		Date yesterday = DateUtil.beginOfDay(DateUtil.yesterday().toJdkDate()).toJdkDate();
		for (FinancialProduct financialProduct : financialProductRepo.findByDeletedFlagIsFalseOrderByCreateTimeDesc()) {
			EverydayRateOfReturn yesterdayRecord = everydayRateOfReturnRepo
					.findByTheDateAndFinancialProductId(yesterday, financialProduct.getId());
			if (yesterdayRecord != null) {
				continue;
			}
			if (Constant.收益类型_预期收益型.equals(financialProduct.getIncomeType())) {
				double rateOfReturn = NumberUtil.round(RandomUtil.randomDouble(financialProduct.getMinRateOfReturn(),
						financialProduct.getMaxRateOfReturn()), 6).doubleValue();
				yesterdayRecord = EverydayRateOfReturn.build(rateOfReturn, yesterday, financialProduct.getId());
				everydayRateOfReturnRepo.save(yesterdayRecord);

				// 缓存年化收益率
				refreshYearRateOfReturnCache(financialProduct.getId());
			} else if (Constant.收益类型_净值型.equals(financialProduct.getIncomeType())) {
				double chg = NumberUtil
						.round(RandomUtil.randomDouble(financialProduct.getMinChg(), financialProduct.getMaxChg()), 2)
						.doubleValue();
				double theDayBeforeYesterdayNpv = 1d;
				EverydayRateOfReturn theDayBeforeYesterdayRecord = everydayRateOfReturnRepo
						.findByTheDateAndFinancialProductId(DateUtil.offsetDay(yesterday, -1).toJdkDate(),
								financialProduct.getId());
				if (theDayBeforeYesterdayRecord != null) {
					theDayBeforeYesterdayNpv = theDayBeforeYesterdayRecord.getNpv();
				}
				double npv = NumberUtil.round(theDayBeforeYesterdayNpv * (1 + (chg / 100)), 4).doubleValue();
				yesterdayRecord = EverydayRateOfReturn.build(chg, npv, yesterday, financialProduct.getId());
				everydayRateOfReturnRepo.save(yesterdayRecord);

				// 缓存最新年化净值增长率
				refreshLatestYearNpvRateCache(financialProduct.getId());
			}
		}
	}

	@Transactional(readOnly = true)
	public List<CanBuyFinancialProductVO> findCanBuyFinancialProduct() {
		List<FinancialProduct> financialProducts = financialProductRepo.findByDeletedFlagIsFalseOrderByCreateTimeDesc();
		return CanBuyFinancialProductVO.convertFor(financialProducts);
	}

	@Transactional
	public List<CanBuyFinancialProductVO> searchFinancialProductInDb(@NotBlank String kw) {
		List<CanBuyFinancialProductVO> vos = new ArrayList<>();
		List<FinancialProduct> financialProducts = financialProductRepo
				.findByProductNameLikeAndDeletedFlagIsFalseOrderByCreateTimeDesc("%" + kw + "%");
		for (FinancialProduct financialProduct : financialProducts) {
			CanBuyFinancialProductVO vo = CanBuyFinancialProductVO.convertFor(financialProduct);
			vo.setProductName(vo.getProductName().replace(kw, "<span class=\"hint-txt-highlight\">" + kw + "</span>"));
			vos.add(vo);
		}
		return vos;
	}

//	@Transactional
//	public List<CanBuyFinancialProductVO> searchFinancialProduct(@NotBlank String kw) {
//		redisTemplate.opsForZSet().incrementScore(Constant.搜索热词, kw, 1.0);
//		SearchRequest searchRequest = new SearchRequest(Elasticsearch.索引_理财产品);
//		MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("productName", kw);
//		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//		sourceBuilder.query(matchQuery);
//		sourceBuilder.from(0);
//		sourceBuilder.size(10);
//		searchRequest.source(sourceBuilder);
//		HighlightBuilder highlightBuilder = new HighlightBuilder();
//		highlightBuilder.preTags("<span class=\"hint-txt-highlight\">");
//		highlightBuilder.postTags("</span>");
//		highlightBuilder.field("productName");
//		sourceBuilder.highlighter(highlightBuilder);
//		Map<String, String> hitProductMap = new LinkedHashMap<String, String>();
//		try {
//			SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//			SearchHits hits = response.getHits();
//			int length = hits.getHits().length;
//			if (length == 0) {
//				return searchFinancialProductInDb(kw);
//			}
//			for (SearchHit hit : hits) {
//				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//				String id = sourceAsMap.get("productId").toString();
//				String productName = hit.getHighlightFields().get("productName").getFragments()[0].string();
//				hitProductMap.put(id, productName);
//			}
//		} catch (IOException e) {
//			log.error("es搜索引擎不可用", e);
//			throw new BizException("搜索功能不可用,请稍后再试");
//		}
//		List<CanBuyFinancialProductVO> vos = new ArrayList<>();
//		List<String> ids = new ArrayList<>(hitProductMap.keySet());
//		List<FinancialProduct> financialProducts = financialProductRepo.findByIdInAndDeletedFlagIsFalse(ids);
//		for (FinancialProduct financialProduct : financialProducts) {
//			CanBuyFinancialProductVO vo = CanBuyFinancialProductVO.convertFor(financialProduct);
//			String productNameHighlight = hitProductMap.get(financialProduct.getId());
//			if (StrUtil.isNotBlank(productNameHighlight)) {
//				vo.setProductName(productNameHighlight);
//			}
//			vos.add(vo);
//		}
//		return vos;
//	}

	@Transactional(readOnly = true)
	public List<String> findTop5HotWord() {
		List<String> hotWords = new ArrayList<>();
		Set<TypedTuple<String>> sets = redisTemplate.opsForZSet().reverseRangeWithScores(Constant.搜索热词, 0, 4);
		for (TypedTuple<String> hotWord : sets) {
			hotWords.add(hotWord.getValue());
		}
		return hotWords;
	}

	@Transactional
	public void logThisWeekProductClickCount(@NotBlank String id) {
		redisTemplate.opsForZSet().incrementScore(Constant.产品每周点击次数 + DateUtil.thisWeekOfYear(), id, 1.0);
	}

	@Transactional(readOnly = true)
	public List<CanBuyFinancialProductVO> findLastWeekHotSearchProduct() {
		Date now = new Date();
		Date lastWeek = DateUtil.offset(now, DateField.WEEK_OF_YEAR, -1).toJdkDate();
		int week = DateUtil.weekOfYear(lastWeek);
		Set<TypedTuple<String>> sets = redisTemplate.opsForZSet().reverseRangeWithScores(Constant.产品每周点击次数 + week, 0,
				9);
		if (CollUtil.isNotEmpty(sets)) {
			List<String> ids = new ArrayList<>();
			for (TypedTuple<String> id : sets) {
				ids.add(id.getValue());
			}
			List<FinancialProduct> products = financialProductRepo.findByIdInAndDeletedFlagIsFalse(ids);
			return CanBuyFinancialProductVO.convertFor(products);
		} else {
			List<FinancialProduct> products = financialProductRepo.findTop10ByDeletedFlagIsFalseOrderByCreateTimeDesc();
			return CanBuyFinancialProductVO.convertFor(products);
		}
	}

	@Transactional(readOnly = true)
	public CanBuyFinancialProductVO findCanBuyFinancialProduct(@NotBlank String id) {
		CanBuyFinancialProductVO vo = CanBuyFinancialProductVO.convertFor(financialProductRepo.getById(id));
		return vo;
	}

	@Transactional
	public void delFinancialProduct(@NotBlank String id) {
		FinancialProduct financialProduct = financialProductRepo.getById(id);
		financialProduct.deleted();
		financialProductRepo.save(financialProduct);
		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
			redissonClient.getTopic(Constant.删除ES文档).publish(id);
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional(readOnly = true)
	public FinancialProductVO findFinancialProduct(@NotBlank String id) {
		return FinancialProductVO.convertFor(financialProductRepo.getById(id));
	}

	@Transactional(readOnly = true)
	public List<FinancialProductVO> findAllFinancialProduct() {
		return FinancialProductVO.convertFor(financialProductRepo.findByDeletedFlagIsFalseOrderByCreateTimeDesc());
	}

	@ParamValid
	@Transactional
	public void addOrUpdateFinancialProduct(AddOrUpdateFinancialProductParam param) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			FinancialProduct financialProduct = param.convertToPo();
			financialProductRepo.save(financialProduct);
			param.setId(financialProduct.getId());
		}
		// 修改
		else {
			FinancialProduct financialProduct = financialProductRepo.getById(param.getId());
			BeanUtils.copyProperties(param, financialProduct);
			financialProductRepo.save(financialProduct);
		}
		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
			redissonClient.getTopic(Constant.添加或修改ES文档).publish(param.getId());
		}, 1, TimeUnit.SECONDS);
	}

//	@ParamValid
//	@Transactional
//	public void addOrUpdateMonetaryFund(AddOrUpdateMonetaryFundParam param) {
//		// 新增
//		if (StrUtil.isBlank(param.getId())) {
//			FinancialProduct financialProduct = param.convertToPo();
//			financialProductRepo.save(financialProduct);
//			param.setId(financialProduct.getId());
//		}
//		// 修改
//		else {
//			FinancialProduct financialProduct = financialProductRepo.getById(param.getId());
//			BeanUtils.copyProperties(param, financialProduct);
//			financialProductRepo.save(financialProduct);
//		}
//		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
//			redissonClient.getTopic(Constant.添加或修改ES文档).publish(param.getId());
//		}, 1, TimeUnit.SECONDS);
//	}

	@Transactional(readOnly = true)
	public FinancialCompanyVO findFinancialCompanyByFinancialProductId(@NotBlank String id) {
		FinancialProduct product = financialProductRepo.getById(id);
		FinancialCompany company = product.getFinancialCompany();
		return FinancialCompanyVO.convertFor(company);
	}

}
