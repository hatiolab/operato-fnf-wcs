package operato.logis.sms.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import operato.fnf.wcs.FnFConstants;
import operato.fnf.wcs.entity.WmsWmtUifWcsInbRtnCnfm;
import operato.logis.sms.query.SmsQueryStore;
import xyz.anythings.base.LogisConstants;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.entity.JobInstance;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.anythings.sys.util.AnyValueUtil;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.manager.DataSourceManager;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.SysConfigConstants;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.MessageUtil;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.sys.util.ThrowUtil;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/sms_trackings")
@ServiceDesc(description="SmsTracking Service API")
public class SmsTrackingController extends AbstractRestService {

	@Autowired
	private SmsQueryStore queryStore;
	
	@Override
	protected Class<?> entityClass() {
		return JobInstance.class;
	}
  
	@RequestMapping(value="/chute_result", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search (Pagination) By Chute Result")
	public Page<?> chuteResult(
			@RequestParam(name="page", required=false) Integer page, 
			@RequestParam(name="limit", required=false) Integer limit, 
			@RequestParam(name="select", required=false) String select, 
			@RequestParam(name="sort", required=false) String sort,
			@RequestParam(name="query", required=false) String query) {
		
		Filter[] filters = ValueUtil.isEmpty(query) ? null : this.jsonParser.parse(query, Filter[].class);
		String selectQuery = queryStore.getSmsChuteSummaryQuery();
		
		Map<String, Object> params = ValueUtil.newMap("domainId", Domain.currentDomainId());
		if(ValueUtil.isNotEmpty(filters)) {
			for(Filter filter : filters) {
				String name = filter.getName();
				String op = filter.getOperator();
				Object val = filter.getValue();

				if(ValueUtil.isEqual(val, "true")) {
					val = true;
				} else if(ValueUtil.isEqual(val, "false")) {
					val = false;
				}
				
				if(ValueUtil.isEqual(name, "batch_id")) {
					Query conds = AnyOrmUtil.newConditionForExecution(Domain.currentDomainId());
					conds.addFilter("id", val);
					JobBatch jobBatch = this.queryManager.select(JobBatch.class, conds);
					
					if(ValueUtil.isEmpty(jobBatch)) {
						String msg = MessageUtil.getMessage("no_batch_id", "설비에서 운영중인 BatchId가 아닙니다.");
						throw ThrowUtil.newValidationErrorWithNoLog(msg);
					}
					
					Query condition = AnyOrmUtil.newConditionForExecution(Domain.currentDomainId());
					condition.addFilter("equipType", LogisConstants.EQUIP_TYPE_SORTER.toUpperCase());
					condition.addFilter("batchGroupId", LogisConstants.IN, jobBatch.getBatchGroupId());
					List<JobBatch> jobBatches = this.queryManager.selectList(JobBatch.class, condition);
					
					if(ValueUtil.isEmpty(jobBatches)) {
						throw ThrowUtil.newValidationErrorWithNoLog(MessageUtil.getTerm("terms.text.is_not_wait_state", "JobBatch status is not 'RUN'"));
					}
					
					List<String> batchList = AnyValueUtil.filterValueListBy(jobBatches, "id");
					params.put("batchList", batchList);
					val = jobBatch.getBatchGroupId();
				}

				if(ValueUtil.isEmpty(op) || ValueUtil.isEqualIgnoreCase(op, "eq") || ValueUtil.isEqualIgnoreCase(op, "=")) {
					if(ValueUtil.isEqual(name, "chute_no")) {
						selectQuery += " where c.chute_no = :chute_no";
					}
					params.put(name, val);

				} else if(ValueUtil.isEqualIgnoreCase(op, "contains") || ValueUtil.isEqualIgnoreCase(op, "like")) {
					params.put(name, "%" + val + "%");
				}
			}
		}
		selectQuery += " order by c.chute_no";
		return this.queryManager.selectPageBySql(selectQuery, params, HashMap.class, 0, 0);
	}
	
	@RequestMapping(value="/rtn_box_result", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search (Pagination) By Chute Result")
	public Page<?> rtnBoxResult(
			@RequestParam(name="page", required=false) Integer page, 
			@RequestParam(name="limit", required=false) Integer limit, 
			@RequestParam(name="select", required=false) String select, 
			@RequestParam(name="sort", required=false) String sort,
			@RequestParam(name="query", required=false) String query) {
		
		Filter[] filters = ValueUtil.isEmpty(query) ? null : this.jsonParser.parse(query, Filter[].class);
		String selectQuery = queryStore.getSmsRtnBoxResultQuery();
		
		Map<String, Object> params = ValueUtil.newMap("domainId,comCd", Domain.currentDomainId(), FnFConstants.FNF_COM_CD);
		if(ValueUtil.isNotEmpty(filters)) {
			for(Filter filter : filters) {
				String name = filter.getName();
				String op = filter.getOperator();
				Object val = filter.getValue();

				if(ValueUtil.isEqual(val, "true")) {
					val = true;
				} else if(ValueUtil.isEqual(val, "false")) {
					val = false;
				}
				
				if(ValueUtil.isEqual(name, "batch_id")) {
					Query conds = AnyOrmUtil.newConditionForExecution(Domain.currentDomainId());
					conds.addFilter("id", val);
					JobBatch jobBatch = this.queryManager.select(JobBatch.class, conds);
					
					if(ValueUtil.isEmpty(jobBatch)) {
						String msg = MessageUtil.getMessage("no_batch_id", "설비에서 운영중인 BatchId가 아닙니다.");
						throw ThrowUtil.newValidationErrorWithNoLog(msg);
					}
					val = jobBatch.getBatchGroupId();
				}

				if(ValueUtil.isEmpty(op) || ValueUtil.isEqualIgnoreCase(op, "eq") || ValueUtil.isEqualIgnoreCase(op, "=")) {
					if(ValueUtil.isEqual(name, "title")) {
						selectQuery += " and jb.title = :title";
					} else if(ValueUtil.isEqual(name, "box_no")) {
						selectQuery += " and mdrr.box_no = :box_no";
					} else if(ValueUtil.isEqual(name, "chute_no")) {
						selectQuery += " and mdo.chute_no = :chute_no";
					} else if(ValueUtil.isEqual(name, "cell_no")) {
						selectQuery += " and mdo.cell_no = :cell_no";
					} else if(ValueUtil.isEqual(name, "sku_cd")) {
						selectQuery += " and mdrr.item_cd = :sku_cd";
					}
					params.put(name, val);
				} else if(ValueUtil.isEqualIgnoreCase(op, "contains") || ValueUtil.isEqualIgnoreCase(op, "like")) {
					if(ValueUtil.isEqual(name, "title")) {
						selectQuery += " and jb.title like :title";
					} else if(ValueUtil.isEqual(name, "box_no")) {
						selectQuery += " and mdrr.box_no like :box_no";
					} else if(ValueUtil.isEqual(name, "chute_no")) {
						selectQuery += " and mdo.chute_no like :chute_no";
					} else if(ValueUtil.isEqual(name, "cell_no")) {
						selectQuery += " and mdo.cell_no like :cell_no";
					} else if(ValueUtil.isEqual(name, "sku_cd")) {
						selectQuery += " and mdrr.item_cd like :sku_cd";
					}
					params.put(name, "%" + val + "%");
				}
			}
		}
		selectQuery += " order by mdo.chute_no, mdo.cell_no";
		return this.queryManager.selectPageBySql(selectQuery, params, HashMap.class, 0, 0);
	}
	
	@RequestMapping(value="/das_box_result", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search (Pagination) By Chute Result")
	public Page<?> dasBoxResult(
			@RequestParam(name="page", required=false) Integer page, 
			@RequestParam(name="limit", required=false) Integer limit, 
			@RequestParam(name="select", required=false) String select, 
			@RequestParam(name="sort", required=false) String sort,
			@RequestParam(name="query", required=false) String query) {
		
		Filter[] filters = ValueUtil.isEmpty(query) ? null : this.jsonParser.parse(query, Filter[].class);
		String selectQuery = queryStore.getSmsDasResultQuery();
		
		Map<String, Object> params = ValueUtil.newMap("domainId,comCd", Domain.currentDomainId(), FnFConstants.FNF_COM_CD);
		if(ValueUtil.isNotEmpty(filters)) {
			for(Filter filter : filters) {
				String name = filter.getName();
				String op = filter.getOperator();
				Object val = filter.getValue();

				if(ValueUtil.isEqual(val, "true")) {
					val = true;
				} else if(ValueUtil.isEqual(val, "false")) {
					val = false;
				}

				if(ValueUtil.isEmpty(op) || ValueUtil.isEqualIgnoreCase(op, "eq") || ValueUtil.isEqualIgnoreCase(op, "=")) {
					if(ValueUtil.isEqual(name, "job_date")) {
						selectQuery += " and mdo.job_date = :job_date";
						val = ValueUtil.toString(val).replaceAll(SysConstants.DASH, SysConstants.EMPTY_STRING);
					} else if(ValueUtil.isEqual(name, "chute_no")) {
						selectQuery += " and mdo.chute_no = :chute_no";
					} else if(ValueUtil.isEqual(name, "shop_cd")) {
						selectQuery += " and mdo.shop_cd = :shop_cd";
					} else if(ValueUtil.isEqual(name, "cell_no")) {
						selectQuery += " and mdo.cell_no = :cell_no";
					} else if(ValueUtil.isEqual(name, "sku_cd")) {
						selectQuery += " and mdo.item_cd = :sku_cd";
					} else if(ValueUtil.isEqual(name, "box_no")) {
						selectQuery += " and mb.box_no = :box_no";
					}
					params.put(name, val);
				} else if(ValueUtil.isEqualIgnoreCase(op, "contains") || ValueUtil.isEqualIgnoreCase(op, "like")) {
					if(ValueUtil.isEqual(name, "job_date")) {
						selectQuery += " and mdo.job_date like :job_date";
						val = ValueUtil.toString(val).replaceAll(SysConstants.DASH, SysConstants.EMPTY_STRING);
					} else if(ValueUtil.isEqual(name, "chute_no")) {
						selectQuery += " and mdo.chute_no like :chute_no";
					} else if(ValueUtil.isEqual(name, "shop_cd")) {
						selectQuery += " and mdo.shop_cd like :shop_cd";
					} else if(ValueUtil.isEqual(name, "cell_no")) {
						selectQuery += " and mdo.cell_no like :cell_no";
					} else if(ValueUtil.isEqual(name, "sku_cd")) {
						selectQuery += " and mdo.item_cd like :sku_cd";
					} else if(ValueUtil.isEqual(name, "box_no")) {
						selectQuery += " and mb.box_no like :box_no";
					}
					params.put(name, "%" + val + "%");
				}
			}
		}
		selectQuery += " order by mdo.chute_no, mdo.cell_no";
		return this.queryManager.selectPageBySql(selectQuery, params, HashMap.class, 0, 0);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/rtn_insp_result", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search (Pagination) By Chute Result")
	public Map<String, Object> rtnInspResult(
			@RequestParam(name="page", required=false) Integer page, 
			@RequestParam(name="limit", required=false) Integer limit, 
			@RequestParam(name="select", required=false) String select, 
			@RequestParam(name="sort", required=false) String sort,
			@RequestParam(name="query", required=false) String query) {
		
		Filter[] filters = ValueUtil.isEmpty(query) ? null : this.jsonParser.parse(query, Filter[].class);
		String selectQuery = queryStore.getSmsRtnInspResultQuery();
		String batchId = "";
		
		Map<String, Object> params = ValueUtil.newMap("domainId,comCd", Domain.currentDomainId(), FnFConstants.FNF_COM_CD);
		if(ValueUtil.isNotEmpty(filters)) {
			for(Filter filter : filters) {
				String name = filter.getName();
				String op = filter.getOperator();
				Object val = filter.getValue();

				if(ValueUtil.isEqual(val, "true")) {
					val = true;
				} else if(ValueUtil.isEqual(val, "false")) {
					val = false;
				}
				
				if(ValueUtil.isEqual(name, "batch_id")) {
					batchId = ValueUtil.toString(val);
				}

				if(ValueUtil.isEmpty(op) || ValueUtil.isEqualIgnoreCase(op, "eq") || ValueUtil.isEqualIgnoreCase(op, "=")) {
					params.put(name, val);
				} else if(ValueUtil.isEqualIgnoreCase(op, "contains") || ValueUtil.isEqualIgnoreCase(op, "like")) {
					params.put(name, "%" + val + "%");
				}
			}
		}
		
		Query condition = new Query();
		condition.addFilter("id", batchId);
		JobBatch batch = this.queryManager.select(JobBatch.class, condition);
		
		String[] jobBatchInfo = batch.getId().split("-");
		if(jobBatchInfo.length < 4) {
			String msg = MessageUtil.getMessage("no_batch_id", "설비에서 운영중인 반품 Batch가 아닙니다.");
			throw ThrowUtil.newValidationErrorWithNoLog(msg);
		}
		
		//Map<String, Object> conds = ValueUtil.newMap("domainId,batchId", Domain.currentDomainId(), batch.getBatchGroupId());
		params.put("batchId", batch.getBatchGroupId());
		List<Map> pasInspList = this.queryManager.selectListBySql(selectQuery, params, Map.class, 0, 0);
		
		Query wmsQuery = new Query();
		wmsQuery.addFilter("whCd", FnFConstants.WH_CD_ICF);  
		wmsQuery.addFilter("strrId", batch.getBrandCd());  
		wmsQuery.addFilter("refSeason", batch.getSeasonCd());  
		wmsQuery.addFilter("shopRtnType", jobBatchInfo[2]);  
		wmsQuery.addFilter("shopRtnSeq", batch.getJobSeq());
		
		wmsQuery.addOrder("refNo", true);
		wmsQuery.addOrder("itemCd", true);
		IQueryManager wmsQueryMgr = BeanUtil.get(DataSourceManager.class).getQueryManager(WmsWmtUifWcsInbRtnCnfm.class);
		List<WmsWmtUifWcsInbRtnCnfm> wmsInspList = wmsQueryMgr.selectList(WmsWmtUifWcsInbRtnCnfm.class, wmsQuery);
		
		Map<String, List<Map>> wmsBox = new HashMap<>();
		List<Map> boxInSku = new ArrayList<Map>();
		
		for (WmsWmtUifWcsInbRtnCnfm wmsInsp : wmsInspList) {
			Map<String, Object> skuInfo = new HashMap<>();
			if(wmsBox.containsKey(wmsInsp.getRefNo())) {
				skuInfo.put("sku_Cd", wmsInsp.getItemCd());
				skuInfo.put("rfid_order_qty", wmsInsp.getInbEctQty());
				skuInfo.put("rfid_qty", wmsInsp.getInbCmptQty());
				boxInSku.add(skuInfo);
				wmsBox.put(wmsInsp.getRefNo(), boxInSku);
			} else {
				boxInSku = new ArrayList<Map>();
				skuInfo.put("sku_Cd", wmsInsp.getItemCd());
				skuInfo.put("rfid_order_qty", wmsInsp.getInbEctQty());
				skuInfo.put("rfid_qty", wmsInsp.getInbCmptQty());
				boxInSku.add(skuInfo);
				wmsBox.put(wmsInsp.getRefNo(), boxInSku);
			}
		}
		
		for (Map pasInsp : pasInspList) {
			if(wmsBox.containsKey(pasInsp.get("box_no"))) {
				for (Map sku : wmsBox.get(pasInsp.get("box_no"))) {
					if(ValueUtil.isEqual(pasInsp.get("sku_cd"), sku.get("sku_cd"))) {
						pasInsp.put("rfid_order_qty", sku.get("rfid_order_qty"));
						pasInsp.put("rfid_qty", sku.get("rfid_qty"));
					}
				}
			} else {
				pasInsp.put("rfid_order_qty", 0);
				pasInsp.put("rfid_qty", 0);
			}
		}
		
		
		return ValueUtil.newMap("items,total", pasInspList, pasInspList.size());
	}
	
	@RequestMapping(value="/rtn_summary", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search (Pagination) By SRTN Summary")
	public Page<?> rtnSummary(
			@RequestParam(name="page", required=false) Integer page, 
			@RequestParam(name="limit", required=false) Integer limit, 
			@RequestParam(name="select", required=false) String select, 
			@RequestParam(name="sort", required=false) String sort,
			@RequestParam(name="query", required=false) String query) {
		
		Filter[] filters = ValueUtil.isEmpty(query) ? null : this.jsonParser.parse(query, Filter[].class);
		String selectQuery = queryStore.getSmsRtnSummaryQuery();
		
		Map<String, Object> params = ValueUtil.newMap("domainId", Domain.currentDomainId());
		if(ValueUtil.isNotEmpty(filters)) {
			for(Filter filter : filters) {
				String name = filter.getName();
				String op = filter.getOperator();
				Object val = filter.getValue();

				if(ValueUtil.isEqual(val, "true")) {
					val = true;
				} else if(ValueUtil.isEqual(val, "false")) {
					val = false;
				}

				if(ValueUtil.isEmpty(op) || ValueUtil.isEqualIgnoreCase(op, "eq") || ValueUtil.isEqualIgnoreCase(op, "=")) {
					params.put(name, val);

				} else if(ValueUtil.isEqualIgnoreCase(op, "contains") || ValueUtil.isEqualIgnoreCase(op, "like")) {
					params.put(name, "%" + val + "%");
				}
			}
		}
		
		page = (page == null) ? 1 : page;
		limit = (limit == null) ? ValueUtil.toInteger(SettingUtil.getValue(SysConfigConstants.SCREEN_PAGE_LIMIT, "10000")) : limit;
		return this.queryManager.selectPageBySql(selectQuery, params, HashMap.class, page, limit);
		
	}
	
	@RequestMapping(value="/das_summary", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search (Pagination) By SDAS, SDPS Summary")
	public Page<?> dasSummary(
			@RequestParam(name="page", required=false) Integer page, 
			@RequestParam(name="limit", required=false) Integer limit, 
			@RequestParam(name="select", required=false) String select, 
			@RequestParam(name="sort", required=false) String sort,
			@RequestParam(name="query", required=false) String query) {
		
		Filter[] filters = ValueUtil.isEmpty(query) ? null : this.jsonParser.parse(query, Filter[].class);
		String selectQuery = queryStore.getSmsDasSummaryQuery();
		
		Map<String, Object> params = ValueUtil.newMap("domainId", Domain.currentDomainId());
		if(ValueUtil.isNotEmpty(filters)) {
			for(Filter filter : filters) {
				String name = filter.getName();
				String op = filter.getOperator();
				Object val = filter.getValue();

				if(ValueUtil.isEqual(val, "true")) {
					val = true;
				} else if(ValueUtil.isEqual(val, "false")) {
					val = false;
				}

				if(ValueUtil.isEmpty(op) || ValueUtil.isEqualIgnoreCase(op, "eq") || ValueUtil.isEqualIgnoreCase(op, "=")) {
					params.put(name, val);

				} else if(ValueUtil.isEqualIgnoreCase(op, "contains") || ValueUtil.isEqualIgnoreCase(op, "like")) {
					params.put(name, "%" + val + "%");
				} else if(ValueUtil.isEqualIgnoreCase(op, "in")) {
					
					params.put(name, ValueUtil.toList(val));
				}
			}
		}
		
		page = (page == null) ? 1 : page;
		limit = (limit == null) ? ValueUtil.toInteger(SettingUtil.getValue(SysConfigConstants.SCREEN_PAGE_LIMIT, "10000")) : limit;
		return this.queryManager.selectPageBySql(selectQuery, params, HashMap.class, page, limit);
		
	}
}