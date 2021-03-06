package operato.fnf.wcs.service.rfid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import operato.fnf.wcs.FnFConstants;
import operato.fnf.wcs.FnfUtils;
import operato.fnf.wcs.entity.RfidBoxItem;
import operato.fnf.wcs.entity.WcsMheBox;
import operato.fnf.wcs.entity.WmsAssortItem;
import xyz.anythings.base.LogisConstants;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.model.ResponseObj;
import xyz.anythings.sys.service.AbstractQueryService;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.util.DateUtil;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.ValueUtil;

@Component
public class DasSendBoxInfoToRfid extends AbstractQueryService {

	/**
	 * Event Publisher
	 */
	@Autowired
	protected ApplicationEventPublisher eventPublisher;
	
	//private final String KEY_RFID_PROCESS_LIMIT = "wcs.rfid.process.limit";
	
	public ResponseObj dasSendBoxInfoToRfid(Map<String, Object> params) throws Exception {
		
		String jobType = String.valueOf(params.get("jobType"));
		if (ValueUtil.isEmpty(jobType)) {
			jobType = LogisConstants.JOB_TYPE_DAS;
		}
		
//		ResponseObj resp = this.dasSendBoxInfoToRfid(1l, jobType);
		
		String batchId = String.valueOf(params.get("batch_id"));
		if (ValueUtil.isEmpty(batchId)) {
			return new ResponseObj();
		}
		
		JobBatch jobBatch = queryManager.select(JobBatch.class, batchId);
		if (ValueUtil.isEmpty(jobBatch)) {
			return new ResponseObj();
		}
		
		return new ResponseObj();
	}
	
	public ResponseObj dasSendBoxInfoToRfid(Long domainId, JobBatch batch) throws Exception {
		String scopeSql = FnfUtils.queryCustServiceWithCheck("das_box_process_date");
		Map<String, Object> params = new HashMap<>();
		params.put(SysConstants.ENTITY_FIELD_DOMAIN_ID, domainId);
		params.put("status", JobBatch.STATUS_RUNNING);
		params.put("jobType", ValueUtil.toList(batch.getJobType()));
		List<String> runningBatchWorkDates = this.queryManager.selectListBySql(scopeSql, params, String.class, 0, 0);
		
		ResponseObj resp = new ResponseObj();
		if (ValueUtil.isEmpty(runningBatchWorkDates) || runningBatchWorkDates.size() == 0) {
			resp.setMsg("No Data1");
			return resp;
		}
		
		List<String> dates = new ArrayList<>();
		for (String date: runningBatchWorkDates) {
			dates.add(date.replaceAll("-", ""));
		}
		
		String serviceSql = FnfUtils.queryCustService("das_box_info_with_outb_date");
		if (ValueUtil.isEmpty(serviceSql)) {
			throw new ValidationException("커스텀 서비스 [das_box_info_with_outb_date]가 존재하지 않습니다.");
		}
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("whCd", FnFConstants.WH_CD_ICF);
		paramMap.put("workDates", dates);
		paramMap.put("delYn", LogisConstants.Y_CAP_STRING);
		paramMap.put("ifYn", LogisConstants.N_CAP_STRING);
		List<WcsMheBox> wcsMheBoxes = queryManager.selectListBySql(serviceSql, paramMap, WcsMheBox.class, 0, 1000);
		if (ValueUtil.isEmpty(wcsMheBoxes) || wcsMheBoxes.size() == 0) {
			resp.setMsg("No Data2");
			return resp;
		}
		
//		Query conds = new Query(0, 1000);
//		conds.addFilter("workUnit", batch.getWmsBatchNo());
//		conds.addFilter("delYn", "N");
//		conds.addFilter("ifYn", "N");
//		List<WcsMheBox> wcsMheBoxes = queryManager.selectList(WcsMheBox.class, conds);
//		if (ValueUtil.isEmpty(wcsMheBoxes)) {
//			return new ResponseObj();
//		}
		
		IQueryManager rfidQueryMgr = this.getDataSourceQueryManager(RfidBoxItem.class);
		IQueryManager wmsQueryMgr = this.getDataSourceQueryManager(WmsAssortItem.class);
		
		String extFieldsSql = FnfUtils.queryCustServiceWithCheck("das_rfid_box_ext_fields");
		
		for (WcsMheBox box: wcsMheBoxes) {
			try {
				BeanUtil.get(DasSendBoxInfoToRfid.class).sendPackingsToRfid(box, wmsQueryMgr, rfidQueryMgr, extFieldsSql);
				
				BeanUtil.get(DasSendBoxInfoToRfid.class).updateBoxFlag(box, "Y");
			} catch(Exception e) {
				logger.error("DasSendBoxInfoToRfid error~~", e);
				BeanUtil.get(DasSendBoxInfoToRfid.class).updateBoxFlag(box, "E");
			}
		}
		
		return new ResponseObj();
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateBoxFlag(WcsMheBox box, String flag) {
		try {
			box.setIfYn(flag);
			box.setIfDatetime(new Date());
			queryManager.update(box, "ifYn", "ifDatetime");
		} catch (Exception e) {
			logger.error("DasSendBoxInfoToRfid.updateBoxFlag() error~~", e);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendPackingsToRfid(WcsMheBox box, IQueryManager wmsQueryMgr, IQueryManager rfidQueryMgr, String extFieldsSql) {
		if(ValueUtil.isEmpty(box)) {
			return;
		}
		
		// WMS 전송 데이터 생성 
		RfidBoxItem rfidBoxItem = new RfidBoxItem();
		rfidBoxItem.setCdWarehouse(box.getWhCd());
		rfidBoxItem.setCdBrand(box.getStrrId());
		rfidBoxItem.setTpMachine("2");
		rfidBoxItem.setDtDelivery(box.getWorkDate());	// 실제로는 MHE_DR.outb_ect_date값임.
		rfidBoxItem.setDsBatchNo(box.getWorkUnit());
		rfidBoxItem.setNoBox(box.getBoxNo());
		rfidBoxItem.setNoWaybill(box.getBoxNo());
		//rfidBoxItem.setNoBox(fromBox.getBoxNo() != null ? fromBox.getBoxNo() : fromBox.getWaybillNo());
		//rfidBoxItem.setNoWaybill(fromBox.getWaybillNo() != null ? fromBox.getWaybillNo() : fromBox.getBoxNo());
		rfidBoxItem.setIfCdItem(box.getItemCd());

		// 아소트 여부 조회 후 설정 
		int count = wmsQueryMgr.selectSize(WmsAssortItem.class, ValueUtil.newMap("itemCd", box.getItemCd()));
		rfidBoxItem.setYnAssort(count > 0 ? LogisConstants.Y_CAP_STRING : LogisConstants.N_CAP_STRING);
		
		rfidBoxItem.setCdShop(box.getShiptoId());
		rfidBoxItem.setTpDelivery("1");
		rfidBoxItem.setDsShuteno(null);
		rfidBoxItem.setOutbNo(box.getOutbNo());
		rfidBoxItem.setQtDelivery(box.getCmptQty());
		String dmBfRecv = DateUtil.dateStr(new Date(), "yyyyMMddHHmmss");
		rfidBoxItem.setDmBfRecv(dmBfRecv);
		rfidBoxItem.setYnCancel(LogisConstants.N_CAP_STRING);
		rfidBoxItem.setTpWeight("0");
		rfidBoxItem.setTpSend("0");
		rfidBoxItem.setNoWeight(null);
		
		Map<String, Object> params = ValueUtil.newMap("whCd,batchId,orderNo", box.getWhCd(), box.getWorkUnit(), box.getOutbNo());
		@SuppressWarnings("unchecked")
		Map<String, Object> order = queryManager.selectBySql(extFieldsSql, params, Map.class);
		rfidBoxItem.setRefNo(ValueUtil.toString(order.get("ref_no")));
		rfidBoxItem.setOutbTcd(ValueUtil.toString(order.get("outb_tcd")));
		//rfidBoxItem.setCdEquipment(ValueUtil.toString(order.get("equip_group_cd")));
		rfidBoxItem.setCdEquipment(box.getMheNo());
		rfidQueryMgr.insert(rfidBoxItem);
	}
}
