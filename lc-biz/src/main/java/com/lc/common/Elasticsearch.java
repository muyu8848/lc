package com.lc.common;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Elasticsearch {

	public static final String 索引_理财产品 = "financial_product";

//	@Autowired
//	private RestHighLevelClient restHighLevelClient;
//
//	@Autowired
//	private FinancialProductRepo financialProductRepo;
//
//	public void delDoc(String id) {
//		DeleteRequest request = new DeleteRequest(索引_理财产品, id);
//		try {
//			restHighLevelClient.delete(request, RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			log.error("es搜索引擎不可用", e);
//			throw new BizException(e.getMessage());
//		}
//	}
//
//	@Transactional(readOnly = true)
//	public void addOrUpdateDoc(String id) {
//		FinancialProduct financialProduct = financialProductRepo.getById(id);
//		HashMap<String, String> map = MapUtil.newHashMap();
//		map.put("productId", financialProduct.getId());
//		map.put("productName", financialProduct.getProductName());
//		String source = JSONUtil.toJsonStr(map);
//		
//		IndexRequest request = new IndexRequest(索引_理财产品).source(source, XContentType.JSON);
//		request.id(financialProduct.getId());
//		UpdateRequest updateRequest = new UpdateRequest(索引_理财产品, id).doc(source, XContentType.JSON);
//		updateRequest.upsert(request);
//		try {
//			restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			log.error("es搜索引擎不可用", e);
//			throw new BizException(e.getMessage());
//		}
//	}
//
//	@Transactional(readOnly = true)
//	public void checkExistsAndCreateIndex() {
//		boolean existsIndexFlag = existsIndex(索引_理财产品);
//		if (existsIndexFlag) {
//			return;
//		}
//		try {
//			CreateIndexRequest request = new CreateIndexRequest(索引_理财产品);
//			request.settings(Settings.builder().put("index.number_of_shards", 2).put("index.number_of_replicas", 0));
//			XContentBuilder mappingBuilder = JsonXContent.contentBuilder().startObject().startObject("properties")
//					.startObject("id").field("type", "text").endObject().startObject("productName")
//					.field("type", "text").field("analyzer", "ik_max_word").endObject().endObject().endObject();
//			request.mapping(mappingBuilder);
//			CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
//			if (!response.isAcknowledged()) {
//				throw new BizException("索引创建失败");
//			}
//		} catch (IOException e) {
//			log.error("es搜索引擎不可用", e);
//			throw new BizException(e.getMessage());
//		}
//		try {
//			List<FinancialProduct> financialProducts = financialProductRepo
//					.findByDeletedFlagIsFalseOrderByCreateTimeDesc();
//			BulkRequest request = new BulkRequest(索引_理财产品);
//			for (FinancialProduct financialProduct : financialProducts) {
//				HashMap<String, String> map = MapUtil.newHashMap();
//				map.put("productId", financialProduct.getId());
//				map.put("productName", financialProduct.getProductName());
//				String source = JSONUtil.toJsonStr(map);
//				IndexRequest indexRequest = new IndexRequest().source(source, XContentType.JSON);
//				indexRequest.id(financialProduct.getId());
//				request.add(indexRequest);
//			}
//			restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
//		} catch (IOException e) {
//			log.error("es搜索引擎不可用", e);
//			throw new BizException(e.getMessage());
//		}
//	}
//
//	public boolean existsIndex(String index) {
//		GetIndexRequest request = new GetIndexRequest(index);
//		try {
//			boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
//			return exists;
//		} catch (IOException e) {
//			log.error("es搜索引擎不可用", e);
//			throw new BizException("搜索功能不可用,请稍后再试");
//		}
//	}

}
