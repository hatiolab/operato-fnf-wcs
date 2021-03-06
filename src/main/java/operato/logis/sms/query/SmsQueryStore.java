package operato.logis.sms.query;

import org.springframework.stereotype.Component;

import xyz.anythings.sys.service.AbstractQueryStore;
import xyz.elidom.sys.SysConstants;

@Component
public class SmsQueryStore extends AbstractQueryStore {

	@Override
	public void initQueryStore(String databaseType) {
		this.databaseType = databaseType;
		this.basePath = "operato/logis/sms/query/" + this.databaseType + SysConstants.SLASH;
		this.defaultBasePath = "operato/logis/sms/query/ansi/";
	}

	/**
	 * BatchReceipt 조회 상세 Item 에 Order 타입이 있는 Case
	 * 
	 * @return
	 */
	public String getBatchReceiptOrderTypeStatusQuery() {
		return this.getQueryByPath("batch/BatchReceiptOrderTypeStatus");
	}

	/*** BatchReceipt 관련 데이터 쿼리 ***/
	/**
	 * WMS I/F 테이블로 부터 Sorter SDAS BatchReceipt 데이터를 조회 한다.
	 * 
	 * @return
	 */
	public String getWmsIfToSdasReceiptDataQuery() {
		return this.getQueryByPath("batch/WmsIfToSdasReceiptData");
	}

	/**
	 * WMS I/F 테이블로 부터 Sorter SDAS Orders 데이터를 조회 한다.
	 * 
	 * @return
	 */
	public String getWmsIfToSdasReceiptOrderDataQuery() {
		return this.getQueryByPath("batch/WmsIfToSdasReceiptOrderData");
	}

	/**
	 * WMS I/F 테이블로 부터 Sorter SRTN BatchReceipt 데이터를 조회 한다.
	 * 
	 * @return
	 */
	public String getWmsIfToSrtnReceiptDataQuery() {
		return this.getQueryByPath("batch/WmsIfToSrtnReceiptData");
	}

	/**
	 * WMS I/F 테이블로 부터 Sorter SRTN Orders 데이터를 조회 한다.
	 * 
	 * @return
	 */
	public String getWmsIfToSrtnReceiptOrderDataQuery() {
		return this.getQueryByPath("batch/WmsIfToSrtnReceiptOrderData");
	}

	/**
	 * WMS I/F 테이블로 부터 SDAS 주문수신 완료된 데이터 변경('Y')
	 * 
	 * @return
	 */
	public String getWmsIfToSdasReceiptUpdateQuery() {
		return this.getQueryByPath("batch/WmsIfToSdasReceiptUpdate");
	}
	
	/**
	 * WMS I/F 테이블로 부터 Sorter SDPS BatchReceipt 데이터를 조회 한다.
	 * 
	 * @return
	 */
	public String getWmsIfToSdpsReceiptDataQuery() {
		return this.getQueryByPath("batch/WmsIfToSdpsReceiptData");
	}
	
	/*** SDAS 관련 데이터 쿼리 ***/
	/**
	 * SDAS 가공화면 batch 정보
	 * 
	 */
	public String getSdasBatchInfo() {
		return this.getQueryByPath("sdas/sdasBatchInfo");
	}

	/**
	 * SDAS 가공화면 Chute 정보
	 * 
	 */
	public String getSdasChuteInfo() {
		return this.getQueryByPath("sdas/sdasChuteInfo");
	}
	
	/**
	 * 주문 데이터로 부터 SDAS 주문 가공 쿼리
	 *
	 * @return
	 */
	public String getSdasGeneratePreprocessQuery() {
		return this.getQueryByPath("sdas/sdasGeneratePreprocess");
	}
	
	/**
	 * 작업 배치 별 주문 가공 정보에서 CELL로 SHOP 할당 상태를 조회 쿼리
	 *
	 * @return
	 */
	public String getSdasCellStatusQuery() {
		return this.getQueryByPath("sdas/sdasCellStatus");
	}
	
	/**
	 * 가공화면 SDAS Cell 정보
	 * 
	 */
	public String getSdasCellInfo() {
		return this.getQueryByPath("sdas/sdasCellInfo");
	}
	
	/**
	 * SDAS SORTER Order 전송
	 * 
	 */
	public String getSdasPasOrder() {
		return this.getQueryByPath("sdas/sdasPasOrder");
	}
	
	/**
	 * SDAS DAS Order 전송
	 * 
	 */
	public String getSdasDasOrder() {
		return this.getQueryByPath("sdas/sdasDasOrder");
	}
	
	
	/*** SDPS 관련 데이터 쿼리 ***/
	/**
	 * SDPS 가공화면 batch 정보
	 * 
	 */
	public String getSdpsBatchInfo() {
		return this.getQueryByPath("sdps/sdpsBatchInfo");
	}
	/**
	 * SDPS 가공화면 Chute 정보
	 * 
	 */
	public String getSdpsChuteInfo() {
		return this.getQueryByPath("sdps/sdpsChuteInfo");
	}
	/**
	 * 주문 데이터로 부터 SDPS 주문 가공 쿼리
	 *
	 * @return
	 */
	public String getSdpsGeneratePreprocessQuery() {
		return this.getQueryByPath("sdps/sdpsGeneratePreprocess");
	}
	/**
	 * 작업 배치 별 주문 가공 정보에서 CELL로 REF_NO(Order No) 할당 상태를 조회 쿼리
	 *
	 * @return
	 */
	public String getSdpsCellStatusQuery() {
		return this.getQueryByPath("sdps/sdpsCellStatus");
	}
	/**
	 * SDPS SORTER Order 전송
	 * 
	 */
	public String getSdpsPasOrder() {
		return this.getQueryByPath("sdps/sdpsPasOrder");
	}
	/**
	 * SDPS DAS Order 전송
	 * 
	 */
	public String getSdpsDasOrder() {
		return this.getQueryByPath("sdps/sdpsDasOrder");
	}
	/**
	 * SDPS DPS_JOB_INSTANCES 전송
	 * 
	 */
	public String getSdpsBoxResult() {
		return this.getQueryByPath("sdps/sdpsBoxResult");
	}
	

	
	/*** SRTN 관련 데이터 쿼리 ***/
	/**
	 * 주문 데이터로 부터 반품 주문 가공 쿼리
	 *
	 * @return
	 */
	public String getSrtnGeneratePreprocessQuery() {
		return this.getQueryByPath("srtn/srtnGeneratePreprocess");
	}

	/**
	 * 주문 데이터로 부터 반품 주문 카테고리 Cell 가공 쿼리
	 *
	 * @return
	 */
	public String getSrtnGenerateCategoryPreprocessQuery() {
		return this.getQueryByPath("srtn/srtnGenerateCategoryPreprocess");
	}

	/**
	 * 작업 배치 별 주문 가공 정보에서 슈트별로 SKU 할당 상태를 조회 쿼리
	 *
	 * @return
	 */
	public String getSrtnChuteStatusQuery() {
		return this.getQueryByPath("srtn/srtnChuteStatus");
	}

	/**
	 * 작업 배치 별 주문 가공 정보에서 CELL로 SKU 할당 상태를 조회 쿼리
	 *
	 * @return
	 */
	public String getSrtnCellStatusQuery() {
		return this.getQueryByPath("srtn/srtnCellStatus");
	}
	
	/**
	 * 작업 배치 별 주문 가공 정보에서 슈트별 가용 Cell 수량 조회 쿼리
	 *
	 * @return
	 */
	public String getSrtnEnableCellCntQuery() {
		return this.getQueryByPath("srtn/srtnEnableCellCnt");
	}

	/**
	 * 작업 배치 별 주문 가공 정보에서 Category Cell 상태를 조회 쿼리
	 *
	 * @return
	 */
	public String getSrtnCategoryCellStatusQuery() {
		return this.getQueryByPath("srtn/srtnCategoryCellStatus");
	}

	/**
	 * 작업 배치 별 슈트별 물량 할당 요약 정보를 조회 쿼리
	 *
	 * @return
	 */
	public String getSrtnPreprocessSummaryQuery() {
		return this.getQueryByPath("srtn/srtnPreprocessSummary");
	}

	/**
	 * 작업 마감을 위한 작업 데이터 요약 정보 조회
	 *
	 * @return
	 */
	public String getSmsBatchResultSummaryQuery() {
		return this.getQueryByPath("batch/BatchResultSummary");
	}

	/**
	 * 검수확정 정보를 최초 PAS Interface
	 *
	 * @return
	 */
	public String getSrtnCnfmQuery() {
		return this.getQueryByPath("srtn/srtnCnfm");
	}
	
	/**
	 * 검수확정 정보를 지속 PAS Interface
	 *
	 * @return
	 */
	public String getSrtnReCnfmQuery() {
		return this.getQueryByPath("srtn/srtnReCnfm");
	}
	
	/**
	 * 검수확정 정보를 PAS Interface
	 *
	 * @return
	 */
	public String getSrtnPasResultQuery() {
		return this.getQueryByPath("srtn/srtnPasResult");
	}

	/**
	 * 반품 가공화면 batch 정보
	 * 
	 */
	public String getSrtnBatchInfo() {
		return this.getQueryByPath("srtn/srtnBatchInfo");
	}

	/**
	 * 반품 가공화면 Chute 정보
	 * 
	 */
	public String getSrtnChuteInfo() {
		return this.getQueryByPath("srtn/srtnChuteInfo");
	}

	/**
	 * 가공화면 Cell 정보
	 * 
	 */
	public String getSrtnCellInfo() {
		return this.getQueryByPath("srtn/srtnCellInfo");
	}

	/**
	 * 매장 반품 검수예정정보 조회
	 * 
	 */
	public String getSrtnInspBoxTrg() {
		return this.getQueryByPath("srtn/srtnInspBoxTrg");
	}
	/**
	 * 매장 반품 검수예정정보 조회
	 * 
	 */
	public String getSrtnInspCnfmBox() {
		return this.getQueryByPath("srtn/srtnInspCnfmBox");
	}
	
	/**
	 * Upload 한 반품 배치 소터에 데이터 전송
	 * 
	 */
	public String getSrtnUploadBatchSendPasOrder() {
		return this.getQueryByPath("srtn/srtnUploadBatchSendPasOrder");
	}
	
	
	
	/*** SMS 조회 관련 데이터 쿼리 ***/
	/**
	 * SMS Chute 별 실적 조회 쿼리
	 *
	 * @return
	 */
	public String getSmsChuteSummaryQuery() {
		return this.getQueryByPath("select/smsChuteSummary");
	}
	/**
	 * SMS RTN 실적 조회 쿼리
	 *
	 * @return
	 */
	public String getSmsRtnBoxResultQuery() {
		return this.getQueryByPath("select/smsRtnBoxResult");
	}
	/**
	 * SMS DAS 실적 조회 쿼리
	 *
	 * @return
	 */
	public String getSmsDasResultQuery() {
		return this.getQueryByPath("select/smsDasResult");
	}
	/**
	 * SMS DAS 실적 조회 쿼리
	 *
	 * @return
	 */
	public String getSmsRtnInspResultQuery() {
		return this.getQueryByPath("select/smsRtnInspResult");
	}
	/**
	 * SMS 일자별 반품 요약 쿼리
	 *
	 * @return
	 */
	public String getSmsRtnSummaryQuery() {
		return this.getQueryByPath("select/smsRtnSummary");
	}
	/**
	 * SMS 일자별 출고 요약 쿼리
	 *
	 * @return
	 */
	public String getSmsDasSummaryQuery() {
		return this.getQueryByPath("select/smsDasSummary");
	}
	/**
	 * SMS PAS - DAS(출고) 실적 조회
	 *
	 * @return
	 */
	public String getSmsPasDasResultQuery() {
		return this.getQueryByPath("select/smsPasDasResult");
	}
	/**
	 * SMS PAS - DAS(반품) 실적 조회
	 *
	 * @return
	 */
	public String getSmsPasRtnResultQuery() {
		return this.getQueryByPath("select/smsPasRtnResult");
	}
	
	/**
	 * SMS 반품 분류 상세 내역
	 *
	 * @return
	 */
	public String getSmsRtnResultQuery() {
		return this.getQueryByPath("select/smsRtnResult");
	}
	/**
	 * SMS 반품 분류 박스 내역
	 *
	 * @return
	 */
	public String getSmsRtnBoxQuery() {
		return this.getQueryByPath("select/smsRtnBox");
	}
	/**
	 * SMS 출고 분류 상세 내역
	 *
	 * @return
	 */
	public String getSmsDasResult2Query() {
		return this.getQueryByPath("select/smsDasResult2");
	}
	/**
	 * SMS 출고 분류 박스 내역
	 *
	 * @return
	 */
	public String getSmsDasBoxQuery() {
		return this.getQueryByPath("select/smsDasBox");
	}
	/**
	 * SMS PAS 실적
	 *
	 * @return
	 */
	public String getSmsPasRejectQuery() {
		return this.getQueryByPath("select/smsRejectResult");
	}
	/**
	 * SMS 반품 입고/검수 결과 
	 *
	 * @return
	 */
	public String getSmsRtnInspQuery() {
		return this.getQueryByPath("select/smsRtnInsp");
	}
	/**
	 * SMS 출보분류 상세 내역
	 *
	 * @return
	 */
	public String getSmsDasResultDetailQuery() {
		return this.getQueryByPath("select/smsDasResultDetail");
	}
}