package operato.logis.sms.service.impl.sdps;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import operato.fnf.wcs.FnFConstants;
import operato.fnf.wcs.entity.WcsMhePasBatchStatus;
import operato.fnf.wcs.service.batch.SmsCloseBatchService;
import xyz.anythings.base.entity.JobBatch;
import xyz.anythings.base.event.main.BatchCloseEvent;
import xyz.anythings.base.service.api.IBatchService;
import xyz.anythings.base.service.impl.AbstractLogisService;
import xyz.anythings.sys.event.model.SysEvent;
import xyz.anythings.sys.service.ICustomService;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.util.MessageUtil;
import xyz.elidom.sys.util.ThrowUtil;
import xyz.elidom.util.ValueUtil;

/**
 * SDAS 작업 배치 서비스
 * 
 *
 */
@Component("sdpsBatchService")
public class SdpsBatchService extends AbstractLogisService implements IBatchService {
	/**
	 * Sms 배치 종료 서비스
	 */
	@Autowired
	private SmsCloseBatchService smsCloseBatchSvc;
	/**
	* 커스텀 서비스 실행기
	*/
	@Autowired
	protected ICustomService customService;
		
	@Override
	public void isPossibleCloseBatch(JobBatch batch, boolean closeForcibly) {
		//0. 설비 배치가 종료 되었는지 확인
		WcsMhePasBatchStatus pasBatchStatus = new WcsMhePasBatchStatus();
		Query batchConds = new Query();
		batchConds.addFilter("mheNo", batch.getEquipCd());
		batchConds.addFilter("wcsBatchNo", batch.getBatchGroupId());
		pasBatchStatus = this.queryManager.selectByCondition(WcsMhePasBatchStatus.class, batchConds);
		
		if(ValueUtil.isNotEqual(pasBatchStatus.getStatus(), FnFConstants.PAS_BATCH_STOP)) {
			throw ThrowUtil.newInvalidStatus(batch.getStatus(), batch.getId(), JobBatch.STATUS_END);
		}
		
		// 1. 배치 마감 전 처리 이벤트 전송
		BatchCloseEvent event = new BatchCloseEvent(batch, SysEvent.EVENT_STEP_BEFORE);
		event = (BatchCloseEvent)this.eventPublisher.publishEvent(event);
		
		// 2. 이벤트 취소라면 ...
		if(event.isAfterEventCancel()) {
			return;
		}
		
		// 3. 작업 배치 상태 체크
		if(ValueUtil.isNotEqual(batch.getStatus(), JobBatch.STATUS_RUNNING)) {
			// 진행 중인 작업배치가 아닙니다
			throw ThrowUtil.newStatusIsNotIng("terms.label.job_batch");
		}

		Query condition = AnyOrmUtil.newConditionForExecution(batch.getDomainId());
		condition.addFilter(new Filter("batchId", batch.getId()));

		// 6. batchId별 작업 실행 데이터 중에 완료되지 않은 것이 있는지 체크
		if(!closeForcibly) {
			Query params = new Query();
			params.addFilter(new Filter("wcsBatchNo", batch.getId()));
			WcsMhePasBatchStatus batchStatus = this.queryManager.selectByCondition(WcsMhePasBatchStatus.class, params);
			
			if(ValueUtil.isEmpty(batchStatus)) {
				String msg = MessageUtil.getMessage("no_batch_id", "설비에서 운영중인 BatchId가 아닙니다.");
				throw ThrowUtil.newValidationErrorWithNoLog(msg);
			}
			
			if(ValueUtil.isNotEqual(WcsMhePasBatchStatus.STATUS_STOP, batchStatus.getStatus())) {
				String msg = MessageUtil.getMessage("ASSORTING_NOT_FINISHED_IN_RACKS", "{0} 등 {1}개의 호기에서 작업이 끝나지 않았습니다.", ValueUtil.toList(batch.getEquipCd(), "1"));
				throw ThrowUtil.newValidationErrorWithNoLog(msg);
			}
		}
	}

	@Override
	public void closeBatch(JobBatch batch, boolean forcibly) {
		// 1. 작업 마감 가능 여부 체크 
		this.isPossibleCloseBatch(batch, forcibly);

		// 2. 배치 마감 후 처리 이벤트 전송
		BatchCloseEvent event = new BatchCloseEvent(batch, SysEvent.EVENT_STEP_AFTER);
		event = (BatchCloseEvent)this.eventPublisher.publishEvent(event);
		
		// 3. 이벤트 취소라면 ...
		if(event.isAfterEventCancel()) {
			return;
		}
		
		// 4. 해당 배치에 랙, 작업 셀 정보 리셋
		this.resetRacksAndCells(batch);

//		// 5. OREDER_PREPROCESS 삭제
		Query query = AnyOrmUtil.newConditionForExecution(batch.getDomainId());
		query.addFilter("batchGroupId", batch.getBatchGroupId());
		List<JobBatch> jobBatches = this.queryManager.selectList(JobBatch.class, query);
		for (JobBatch jobBatch : jobBatches) {
			this.deletePreprocess(jobBatch);
		}

//		// 6. JobBatch 상태 변경 
		this.updateJobBatchFinished(batch, new Date());
		
		// 7. 커스텀 서비스 호출(이력 테이블 Insert)
		this.customService.doCustomService(Domain.currentDomainId(), "diy-expas-after-sdps-batch-close", ValueUtil.newMap("batch", batch));
	}

	@Override
	public void isPossibleCloseBatchGroup(Long domainId, String batchGroupId, boolean closeForcibly) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int closeBatchGroup(Long domainId, String batchGroupId, boolean forcibly) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void isPossibleCancelBatch(JobBatch batch) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 해당 배치의 랙, 작업 셀 정보 리셋
	 *
	 * @param batch
	 * @return
	 */
	protected void resetRacksAndCells(JobBatch batch) {
		// rack, cell
		Map<String, Object> params = ValueUtil.newMap("domainId,batchId", batch.getDomainId(), batch.getBatchGroupId());
	  	this.queryManager.executeBySql("UPDATE RACKS SET STATUS = null, BATCH_ID = null WHERE DOMAIN_ID = :domainId AND BATCH_ID = :batchId", params);
	  	this.queryManager.executeBySql("UPDATE CELLS SET CLASS_CD = null, BATCH_ID = null, BRAND_CD = null WHERE DOMAIN_ID = :domainId AND BATCH_ID = :batchId", params);
	}
	
	/**
	 * 주문 가공 정보를 모두 삭제한다.
	 *
	 * @param batch
	 * @return
	 */
	protected void deletePreprocess(JobBatch batch) {
		this.queryManager.executeBySql("DELETE FROM ORDER_PREPROCESSES WHERE DOMAIN_ID = :domainId AND BATCH_ID= :batchId", ValueUtil.newMap("domainId,batchId", batch.getDomainId(), batch.getId()));
	}
	
	/**
	 * 작업 배치를 마감 처리
	 * 
	 * @param batch
	 * @param finishedAt
	 */
	protected void updateJobBatchFinished(JobBatch batch, Date finishedAt) {
		// 배치 마감을 위한 물량 주문 대비 최종 실적 요약 정보 조회
		this.smsCloseBatchSvc.closeBatch(batch);
	}

	@Override
	public void isPossibleChangeEquipment(JobBatch batch, String toEquipCd) {
		throw ThrowUtil.newNotSupportedMethod();
	}

	@Override
	public void changeEquipment(JobBatch batch, String toEquipCd) {
		throw ThrowUtil.newNotSupportedMethod();
	}

}
